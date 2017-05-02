package client;

/**
 * Created by sampastoriza on 4/16/17.
 */

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AudioClient {

    private ClientStream cs = null;
    private Thread t;
    private String jwt;

    public static void main(String[] args) throws Exception {
        AudioClient ac = new AudioClient();
        ac.beginCLI();
    }

    public void beginCLI() {
        System.out.println("Welcome to Pandora's Box! This is a simple cli to interact with our service. \n Type help if you are new and need the commands possibles");
        System.out.print("$ ");
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            line = line.toLowerCase();
            switch (line) {
                case "help":
                    this.printHelpCommands();
                    break;
                case "play":
                    this.playStream();
                    break;
                case "stop":
                    this.stopStream();
                    break;
                case "pause":
                    this.pauseStream();
                    break;
                case "next":
                    this.nextSong();
                    break;
                case "resume":
                    this.resumeSong();
                    break;
                case "volume up":
                    this.volumeUp();
                    break;
                case "volume down":
                    this.volumeDown();
                    break;
                case "volume":
                    this.getVolume();
                    break;
                case "genres":
                    this.getGenres();
                    break;
                case "play genre":
                    this.playGenres(sc);
                    break;
                case "login":
                    this.login(sc);
                    break;
                case "register":
                    this.register(sc);
                    break;
                case "quit":
                case "exit":
                case "q":
                    this.stopStream();
                    return;
                default:
                    if (line.startsWith("volume") && line.split(" ").length == 2) {
                        try {
                            int volume = Integer.parseInt(line.split(" ")[1]);
                            this.setVolume(volume);
                        } catch (NumberFormatException e) {
                            System.out.println("To set volume please type volume followed by a number between 0 - 100.");
                        }
                        break;
                    } else {
                        System.out.println("The command " + line + " does not exist. Type help if you need help");
                        break;
                    }
            }
            System.out.print("$ ");

        }
        System.out.println("Client: end");
    }

    private void getGenres() {
        if (cs == null) {
            this.connect(this.jwt);
        }
        cs.getGenres();
    }

    private void playGenres(Scanner sc) {
        if (cs == null) {
            this.connect(this.jwt);
        }
        String genre = sc.nextLine();
        cs.playGenre(genre);
    }

    private void playStream() {
        this.connect(jwt);
        t = new Thread(cs);
        t.setDaemon(true);
        t.start();
    }

    private void connect(String jwt) {
        cs = new ClientStream(jwt);
    }

    private void register(Scanner sc) {
        System.out.println("Register Here");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Confirm Password: ");
        String confirmPassword = sc.nextLine();
        if (password.equals(confirmPassword)) {
            this.registerClient(email, username, password);
        }
    }

    private void login(Scanner sc) {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        this.loginClient(username, password);
    }

    public void registerClient(String email, String username, String encryptedPassword) {
        try {
            GenericUrl url = new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/register"));
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

            Map<String, String> json = new HashMap<>();
            json.put("email", email);
            json.put("username", username);
            json.put("password", encryptedPassword);

            HttpContent content = new JsonHttpContent(new JacksonFactory(), json);
            HttpRequest request = requestFactory.buildPostRequest(url, content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType("application/json");
            request.setHeaders(headers);

            HttpResponse response = request.execute();
            jwt = response.parseAsString();
            System.out.println("jwt " + jwt);
        } catch (MalformedURLException e) {
            System.err.println("Register error 1");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Register error 2");
            e.printStackTrace();
        }
    }

    public void loginClient(String username, String password) {
        try {
            GenericUrl url = new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/login"));
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization(username + ":" + password);
            request.setHeaders(headers);

            HttpResponse response = request.execute();
            jwt = response.parseAsString();
            System.out.println("Response " + jwt);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Login error 1");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Login error 2");
        }
    }

    private void stopStream() {
        System.out.println("Stopping stream");
        if (cs == null) {
            return;
        }
        cs.stopStream();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void pauseStream() {
        if(cs != null) {
            System.out.println("Pausing stream");
            cs.pauseStream();
        }

    }

    private void nextSong() {
        this.stopStream();
        this.playStream();
    }

    private void resumeSong() {
        if(cs != null) {
            System.out.println("Resume the music");
            cs.resumeStream();
        }
    }

    private void getVolume(){
        if(cs != null) {
            try {
                cs.getVolume();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void setVolume(int val){
        if(cs != null) {
            try {
                cs.setVolume(val);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void volumeUp(){
        if(cs != null) {
            try {
                cs.volumeUp();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void volumeDown(){
        if(cs != null) {
            try {
                cs.volumeDown();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void printHelpCommands() {
        System.out.println("help: Prints all of the commands associated with the application");
        System.out.println("play: Connects to and plays the stream");
        System.out.println("stop: Stops the stream");
        System.out.println("pause: Pauses the current song");
        System.out.println("next: Skips to the next song");
        System.out.println("resume: Resumes the music");
        System.out.println("quit: Quits the CLI");
    }


}
