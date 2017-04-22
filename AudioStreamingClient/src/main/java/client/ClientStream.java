package client;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.in;

/**
 * Created by sampastoriza on 4/18/17.
 */
public class ClientStream implements Runnable {

    private Clip clip;
    private AudioInputStream ais;
    private AtomicBoolean streamPaused = new AtomicBoolean(false);
    private AtomicBoolean streamStopped = new AtomicBoolean(false);

    private HttpRequestFactory requestFactory;

    public ClientStream() {
        this.requestFactory = new NetHttpTransport().createRequestFactory();
    }

    @Override
    public void run() {
        try {
            GenericUrl url = new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/song/play/random"));
            this.play(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopStream() {
        this.streamStopped.set(true);
        this.streamPaused.set(false);
        this.clip.stop();
    }

    private void play(GenericUrl url) {
        try {
            this.ais = AudioSystem.getAudioInputStream(url.toURL());
            this.clip = AudioSystem.getClip();
            this.clip.open(ais);
            this.clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            this.clip.drain();
            while (this.streamPaused.get()) {
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
        if (!this.streamStopped.get()) {
            try {
                this.play(new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/song/play/random")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
