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
                    case "login":
                        this.login(br);
                        break;
                    case "register":
                        this.register(br);
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
        stream.playStream();
    }

    private void login(BufferedReader br) {
        System.out.println("Logging in user");
        try {
            String username = br.readLine();
            String password = br.readLine();
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        } catch (IOException e) {
            System.err.println("Username or password does not exist");
            e.printStackTrace();
        }
    }

    private void register(BufferedReader br) {
        System.out.println("Registering user");
    }
}
