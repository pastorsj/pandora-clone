/**
 * Created by sampastoriza on 4/16/17.
 */
import java.io.*;
import java.net.*;

public class AudioServer {
    public static void main(String[] args) throws IOException {
        String song = "/Users/sampastoriza/Documents/Programming/Java Development/AudioStreaming/song.mp3";
        File soundFile = AudioUtil.getSoundFile(song);

        System.out.println("server: " + soundFile);

        try (ServerSocket serverSocker = new ServerSocket(6666);
             FileInputStream in = new FileInputStream(soundFile)) {
            if (serverSocker.isBound()) {
                Socket client = serverSocker.accept();
                OutputStream out = client.getOutputStream();

                byte buffer[] = new byte[2048];
                int count;
                while ((count = in.read(buffer)) != -1)
                    out.write(buffer, 0, count);
            }
        }

        System.out.println("server: shutdown");
    }
}