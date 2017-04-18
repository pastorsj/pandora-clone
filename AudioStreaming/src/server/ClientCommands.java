package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by sampastoriza on 4/18/17.
 */
public class ClientCommands implements Runnable {

    public volatile AudioStream stream;
    public volatile Socket client;

    public ClientCommands(AudioStream as, Socket client) {
        this.stream = as;
        this.client = client;
    }


    @Override
    public void run() {
        try {
            InputStream in = this.client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
                switch (line) {
                    case "nextsong":
                        this.nextSong();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    public void nextSong() {
        System.out.println("Going to the next song");
        stream.playNextSong();
    }
}
