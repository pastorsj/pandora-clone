package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class GenreSongPage extends AppCompatActivity {

    private String jwt = "";
    private String ipAddress = "";
    private String songUrl;
    TextView songName;
    TextView songArtist;
    TextView songYear;

    String genreIntent;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_song_page);

        Intent intent = getIntent();
        jwt = intent.getExtras().getString("token");
        genreIntent = intent.getExtras().getString("genre");

        this.songName = (TextView) findViewById(R.id.genreSongName);
        this.songArtist = (TextView) findViewById(R.id.genreSongArtist);
        this.songYear = (TextView) findViewById(R.id.genreSongYear);

        playGenre(genreIntent);

        Button classicalButton = (Button) findViewById(R.id.playClassicalGenreButton);
        classicalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
                playGenre("Classical");
            }
        });

        Button rockButton = (Button) findViewById(R.id.playRockGenreButton);
        rockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
                playGenre("Rock");
            }
        });

        ImageButton button = (ImageButton) findViewById(R.id.GenrePlay);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resume();
            }
        });

        ImageButton skipButton = (ImageButton) findViewById(R.id.GenreSkip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //try {
                    stop();
                    //skip(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/song/random")));
                //} catch (MalformedURLException e) {}
            }
        });
        ImageButton pauseButton = (ImageButton) findViewById(R.id.GenrePause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

        ImageButton previousButton = (ImageButton) findViewById(R.id.GenrePrevious);
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
                //previous();
            }
        });
    }

    private void stop(){ this.mediaPlayer.stop();}

    private void pause() {
        mediaPlayer.pause();
    }

    private void resume() {
        mediaPlayer.start();
    }

    private void playGenre(String genreString){
        try {
            String urlToBuild = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/genre/" + genreString;
            GenericUrl url = new GenericUrl(new URL(urlToBuild));

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

                this.songName.setText(title);
                this.songArtist.setText(artist);
                this.songYear.setText(year);

                String urlToPlay = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;

                this.mediaPlayer  = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(urlToPlay);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
