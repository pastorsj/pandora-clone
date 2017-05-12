package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;
//import javax.sound.sampled.AudioInputStream;
import javaXCopied.AudioSystem;
import javaXCopied.Clip;
import javaXCopied.AudioInputStream;


public class SongPage extends AppCompatActivity {

    private Clip clip;
    private AudioInputStream ais;
    private AtomicBoolean streamPaused = new AtomicBoolean(false);
    private AtomicBoolean streamStopped = new AtomicBoolean(false);
    private String jwt = "";
    private String ipAddress = "";
    TextView songName;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_page);

        this.mediaPlayer  = new MediaPlayer();

        Intent intent = getIntent();
        ipAddress = intent.getExtras().getString("ipAddress");
        jwt = intent.getExtras().getString("token");
        this.songName = (TextView) findViewById(R.id.songName);
        TextView ipTxt = (TextView) findViewById(R.id.ipAddressSong);
        this.songName.setText("JWT: " + jwt);
        ipTxt.setText("IP: " + ipAddress);

        try {
            this.play(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/song/random")));
        } catch (MalformedURLException e) {
            // i had an exceptiong
        }

        ImageButton button = (ImageButton) findViewById(R.id.Play);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resume();
            }
        });

        ImageButton skipButton = (ImageButton) findViewById(R.id.Skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    stop();
                    play(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/song/random")));
                } catch (MalformedURLException e) {
                    // i had an exceptiong
                }

            }
        });

        ImageButton pauseButton = (ImageButton) findViewById(R.id.Pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

    }

    private void play(GenericUrl url) {
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpHeaders headers = new HttpHeaders();
            System.out.println("JWT" + jwt);
            headers.setAuthorization("Bearer " + jwt);
            request.setHeaders(headers);

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

                this.songName.setText("Title: " + title);

                //URL songUrl = new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id);
                String songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
                //MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        mediaPlayer.pause();
    }

    private void resume() {
        mediaPlayer.start();
    }

    private void stop(){
        mediaPlayer.stop();
    }

    private void skip(GenericUrl url) {
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpHeaders headers = new HttpHeaders();
            System.out.println("JWT" + jwt);
            headers.setAuthorization("Bearer " + jwt);
            request.setHeaders(headers);

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

                this.songName.setText("Title: " + title);

                String songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
