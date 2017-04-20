package client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.*;

/**
 * Created by sampastoriza on 4/18/17.
 */
public class ClientStream implements Runnable {

    private Socket socket;
    private Clip clip;
    private AudioInputStream ais;
    private AtomicBoolean streamPaused = new AtomicBoolean(false);
    private AtomicBoolean streamStopped = new AtomicBoolean(false);

    public ClientStream() {
        try {
            this.socket = new Socket("127.0.0.1", 6666);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        InputStream in = null;
        try {
            in = new BufferedInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.play(in);
    }

    public void stopStream() {
        try {
            this.streamStopped.set(true);
            this.streamPaused.set(false);
            this.clip.stop();
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Stopped stream");
            e.printStackTrace();
        }
    }

    private void play(InputStream in) {
        try {
            this.ais = AudioSystem.getAudioInputStream(in);
            this.clip = AudioSystem.getClip();
            this.clip.open(ais);
            this.clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            this.clip.drain();
            while(this.streamPaused.get()) {
                Thread.sleep(100); // given clip.drain a chance to start
                this.clip.drain();
            }
            this.nextSong(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseStream() {
        this.streamPaused.set(true);
        this.clip.stop();
    }

    public void resumeStream() {
        this.clip.start();
        this.streamPaused.set(false);
    }

    public void volumeDown() {
        FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if ((gainControl.getValue() - 1.0f) < gainControl.getMinimum()) {
            System.out.println("Volume is at min");
        } else {
            System.out.println("Decreasing the volume");
            gainControl.setValue(gainControl.getValue() - 1.0f); // Reduce volume by 10 decibels.
        }
    }

    public void volumeUp() {
        FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if ((gainControl.getValue() + 1.0f) > gainControl.getMaximum()) {
            System.out.println("Volume is at max");
        } else {
            System.out.println("Increasing the volume");
            gainControl.setValue(gainControl.getValue() + 1.0f); // increase volume by 10 decibels.
        }
    }

    public void nextSong(InputStream in) {
        if(!this.streamStopped.get()) {
            if (!this.socket.isClosed() && this.socket.isConnected()) {
                try (OutputStream out = this.socket.getOutputStream()) {
                    PrintWriter pw = new PrintWriter(out, true);
                    pw.println("nextsong");
                    this.play(in);
                } catch (IOException e) {
                    e.printStackTrace(); // shhh
                }
            }
        }
    }
}
