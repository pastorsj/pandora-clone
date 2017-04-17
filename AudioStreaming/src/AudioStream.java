import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sampastoriza on 4/16/17.
 */

public class AudioStream implements Runnable {
    private Socket client;
    private String song = "./AudioStreaming/songs/song.wav";
    private File soundFile;

    public AudioStream(Socket client) {
        this.client = client;
        this.soundFile = AudioUtil.getSoundFile(song);
    }

    @Override
    public void run() {

        try {
            OutputStream out = this.client.getOutputStream();
            FileInputStream in = new FileInputStream(this.soundFile);
            byte buffer[] = new byte[2048];
            int count;
            while ((count = in.read(buffer)) != -1)
                out.write(buffer, 0, count);
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
    }
}