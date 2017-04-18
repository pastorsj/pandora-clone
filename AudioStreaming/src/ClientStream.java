import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by sampastoriza on 4/18/17.
 */
public class ClientStream implements Runnable {

    private volatile Socket socket;
    private volatile Clip clip;

    @Override
    public void run() {
        try {
            this.socket = new Socket("127.0.0.1", 6666);
            if (this.socket.isConnected()) {
                InputStream in = new BufferedInputStream(this.socket.getInputStream());
                this.play(in);
            }
        } catch (Exception e) {
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

    private synchronized void play(final InputStream in) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            this.clip = AudioSystem.getClip();
            this.clip.open(ais);
            this.clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            this.clip.drain();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseStream() {
        this.clip.stop();
    }

    public void resumeStream() {
        this.clip.start();
    }
}
