package client;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public void getGenres() {
        try {
            GenericUrl url = new GenericUrl("http://127.0.0.1:" + 8080 + "/genres");
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            System.out.println(response.parseAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playGenre(String genre) {
        try {
            GenericUrl url = new GenericUrl("http://127.0.0.1:" + 8080 + "/genre/" + genre);
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            JsonParser parser = new JsonParser();
            JsonElement song = parser.parse(response.parseAsString());

            if(song.isJsonObject()) {
                JsonObject songInfo = song.getAsJsonObject();

                int id = songInfo.get("id").getAsInt();
                String artist = songInfo.get("artist").getAsString();
                String year = songInfo.get("year").getAsString();
                String album = songInfo.get("album").getAsString();
                String title = songInfo.get("title").getAsString();
                String track = songInfo.get("track").getAsString();

                System.out.println("Artist: " + artist);
                System.out.println("Album: " + album);
                System.out.println("Year: " + year);
                System.out.println("Genre: " + genre);
                System.out.println("Title: " + title);
                System.out.println("Track: " + track);

                URL songUrl = new URL("http://127.0.0.1:" + 8080 + "/song/play/" + id);

                this.ais = AudioSystem.getAudioInputStream(songUrl);
                this.clip = AudioSystem.getClip();
                this.clip.open(ais);
                this.clip.start();
                Thread.sleep(100); // given clip.drain a chance to start
                this.clip.drain();
                while (this.streamPaused.get()) {
                    Thread.sleep(100); // given clip.drain a chance to start
                    this.clip.drain();
                }
                this.playGenre(genre);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
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
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            JsonParser parser = new JsonParser();
            JsonElement song = parser.parse(response.parseAsString());

            if(song.isJsonObject()) {
                JsonObject songInfo = song.getAsJsonObject();

                int id = songInfo.get("id").getAsInt();
                String artist = songInfo.get("artist").getAsString();
                String year = songInfo.get("year").getAsString();
                String album = songInfo.get("album").getAsString();
                String genre = songInfo.get("genre").getAsString();
                String title = songInfo.get("title").getAsString();
                String track = songInfo.get("track").getAsString();

                System.out.println("Artist: " + artist);
                System.out.println("Album: " + album);
                System.out.println("Year: " + year);
                System.out.println("Genre: " + genre);
                System.out.println("Title: " + title);
                System.out.println("Track: " + track);

                URL songUrl = new URL("http://127.0.0.1:" + 8080 + "/song/play/" + id);

                this.ais = AudioSystem.getAudioInputStream(songUrl);
                this.clip = AudioSystem.getClip();
                this.clip.open(ais);
                this.clip.start();
                Thread.sleep(100); // given clip.drain a chance to start
                this.clip.drain();
                while (this.streamPaused.get()) {
                    Thread.sleep(100); // given clip.drain a chance to start
                    this.clip.drain();
                }
                this.nextSong();
            }

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

    public void getVolume() {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            System.out.println("The volume is currently " + 100 *((gainControl.getValue() - gainControl.getMinimum()) / (gainControl.getMaximum() - gainControl.getMinimum())));
        }
    }

    public void setVolume(int val) {
        // Check input
        if (val < 0) {
            val = 0;
        } else if ( val > 100) {
            val = 100;
        }
        if (clip != null) {
            // Change the Volume.
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((((float) val / (float) 100) * (gainControl.getMaximum() - gainControl.getMinimum())) + gainControl.getMinimum());
        }
    }

    public void volumeDown() {
        if (clip != null) {
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if ((gainControl.getValue() - 1.0f) < gainControl.getMinimum()) {
                System.out.println("Volume is at min");
            } else {
                System.out.println("Decreasing the volume");
                gainControl.setValue(gainControl.getValue() - 1.0f); // Reduce volume by 10 decibels.
            }
        }
    }

    public void volumeUp() {
        if (clip != null) {
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if ((gainControl.getValue() + 1.0f) > gainControl.getMaximum()) {
                System.out.println("Volume is at max");
            } else {
                System.out.println("Increasing the volume");
                gainControl.setValue(gainControl.getValue() + 1.0f); // increase volume by 10 decibels.
            }
        }
    }

    public void nextSong() {
        if (!this.streamStopped.get()) {
            try {
                this.play(new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/song/play/random")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
