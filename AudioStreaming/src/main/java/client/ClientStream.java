package client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
import java.net.Socket;

/**
 * Created by sampastoriza on 4/18/17.
 */
public class ClientStream implements Runnable {

    private Socket socket;
    private Clip clip;
    private AudioInputStream ais;

    @Override
    public void run() {
        try {
            this.socket = new Socket("127.0.0.1", 6666);
            if (this.socket.isConnected()) {
                InputStream in = new BufferedInputStream(this.socket.getInputStream());
                this.play(in);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void stopStream() {
        try {
            this.clip.stop();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play(final InputStream in) {
        try {
            this.ais = AudioSystem.getAudioInputStream(in);
            this.clip = AudioSystem.getClip();
            this.clip.open(ais);
            this.clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            this.clip.drain();
            this.nextSong(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endCurrentSong() {
        //this.clip.stop();
        //this.nextSong();
        this.clip.stop();
        this.clip.setFramePosition(this.clip.getFrameLength() - 1);
        this.clip.start();
        //this.clip.flush();
    }

    public void pauseStream() {
        this.clip.stop();
    }

    public void resumeStream() {
        this.clip.start();
    }

    public void nextSong(InputStream in) {
        if (!this.socket.isClosed() && this.socket.isConnected()) {
            try (OutputStream out = this.socket.getOutputStream()) {
                PrintWriter pw = new PrintWriter(this.socket.getOutputStream(), true);
                pw.println("nextsong");
                this.play(in);
            } catch (IOException e) {
                e.printStackTrace(); // shhh
            }
        }
    }
}
