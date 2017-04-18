package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sampastoriza on 4/16/17.
 */

public class AudioServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            while (true) {
                final Socket client = serverSocket.accept();
                new Thread(new AudioStream(client)).start();
            }
        }
    }
}