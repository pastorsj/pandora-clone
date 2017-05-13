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
import java.util.HashMap;
import java.util.Map;
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
    private String songUrl;
    TextView songName;
    TextView songArtist;
    TextView songYear;
    HashMap<String, HashMap<String, String>> previousSongMap = new HashMap<String, HashMap<String, String>>();

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_page);

        Intent intent = getIntent();
        jwt = intent.getExtras().getString("token");

        this.songName = (TextView) findViewById(R.id.songName);
        this.songArtist = (TextView) findViewById(R.id.songArtist);
        this.songYear = (TextView) findViewById(R.id.songYear);

        try {
            this.play(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/song/random")));
        } catch (MalformedURLException e) {
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
                skip(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/song/random")));
            } catch (MalformedURLException e) {
            }
            }
        });

        ImageButton pauseButton = (ImageButton) findViewById(R.id.Pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

        ImageButton previousButton = (ImageButton) findViewById(R.id.Previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
                previous();
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

                this.songName.setText("Title: " + title);
                this.songArtist.setText("Artist: " + artist);
                this.songYear.setText("Year: " + year);

                HashMap<String, String> songInfoMap = new HashMap<String, String>();
                songInfoMap.put("artist", artist);
                songInfoMap.put("year", year);
                songInfoMap.put("album", album);
                songInfoMap.put("genre", genre);
                songInfoMap.put("title", title);
                songInfoMap.put("track", track);
                songInfoMap.put("previousSong", "base");

                //URL songUrl = new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id);
                this.songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
                previousSongMap.put(this.songUrl, songInfoMap);
                //MediaPlayer mediaPlayer = new MediaPlayer();
                this.mediaPlayer  = new MediaPlayer();
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
            //this.mediaPlayer = new MediaPlayer();
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

                this.songName.setText("Title: " + title);this.songName.setText("Title: " + title);
                this.songArtist.setText("Artist: " + artist);
                this.songYear.setText("Year: " + year);

                HashMap<String, String> songInfoMap = new HashMap<String, String>();
                songInfoMap.put("artist", artist);
                songInfoMap.put("year", year);
                songInfoMap.put("album", album);
                songInfoMap.put("genre", genre);
                songInfoMap.put("title", title);
                songInfoMap.put("track", track);
                songInfoMap.put("previousSong", this.songUrl);

                this.songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
                previousSongMap.put(this.songUrl, songInfoMap);
                this.mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void previous() {
        try {
            HashMap<String, String> previousSongInfo = this.previousSongMap.get(this.songUrl);
            this.songUrl = previousSongInfo.get("previousSong");
            previousSongInfo = this.previousSongMap.get(this.songUrl);

            String title = previousSongInfo.get("title");
            String artist = previousSongInfo.get("artist");
            String year = previousSongInfo.get("year");

            this.songName.setText("Title: " + title);
            this.songArtist.setText("Artist: " + artist);
            this.songYear.setText("Year: " + year);

            this.mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this.songUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
