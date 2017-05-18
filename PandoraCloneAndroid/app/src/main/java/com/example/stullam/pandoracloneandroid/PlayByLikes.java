package com.example.stullam.pandoracloneandroid;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
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


public class PlayByLikes extends AppCompatActivity {

    private String jwt = "";
    private String songUrl;
    TextView songName;
    TextView songArtist;
    TextView songYear;
    private boolean isLiked = true;
    String id = "";

    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private MediaPlayer mediaPlayer;

    Button likeSongButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_by_likes);

        Intent intent = getIntent();
        jwt = intent.getExtras().getString("token");

        this.mediaPlayer  = new MediaPlayer();
        initControls();

        this.songName = (TextView) findViewById(R.id.likesSongName);
        this.songArtist = (TextView) findViewById(R.id.likesSongArtist);
        this.songYear = (TextView) findViewById(R.id.likesSongYear);

        try {
            this.play(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/likes")));
        } catch (MalformedURLException e) {}

        ImageButton skipInLikes = (ImageButton) findViewById(R.id.likesSkip);
        skipInLikes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    stop();
                    play(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/likes")));
                    isLiked = true;
                    likeSongButton.setText("Dislike this song");
                }
                catch (MalformedURLException e) {}
            }
        });

        ImageButton button = (ImageButton) findViewById(R.id.likesPlay);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resume();
            }
        });

        ImageButton pauseButton = (ImageButton) findViewById(R.id.likesPause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

        ImageButton previousButton = (ImageButton) findViewById(R.id.likesPrevious);
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
            }
        });

        ImageButton homePageButton = (ImageButton) findViewById(R.id.homePageButtonLikes);
        homePageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
                goToHomePage();
            }
        });

        Button playGenreButton = (Button) findViewById(R.id.likesPlayGenreButton);
        playGenreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
                playGenres();
            }
        });

        this.likeSongButton = (Button) findViewById(R.id.likesLikeSongButton);
        likeSongButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isLiked){
                    likeSong();
                } else {
                    dislikeSong();
                }
            }
        });
    }

    private void goToHomePage(){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("token", jwt);
        intent.putExtra("ipAddress", "hello");
        startActivity(intent);
    }

    private void likeSong(){
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

            HttpRequest request =
                    requestFactory.buildGetRequest(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/like/song/" + this.id)));

            HttpHeaders headers = new HttpHeaders();
            System.out.println("JWT" + jwt);
            headers.setAuthorization("Bearer " + jwt);
            request.setHeaders(headers);

            HttpResponse response = request.execute();

            this.isLiked = true;
            this.likeSongButton.setText("Dislike this song");
        } catch (Exception e) {

        }
    }

    private void dislikeSong(){
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

            HttpRequest request =
                    requestFactory.buildGetRequest(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/dislike/song/" + this.id)));

            HttpHeaders headers = new HttpHeaders();
            System.out.println("JWT" + jwt);
            headers.setAuthorization("Bearer " + jwt);
            request.setHeaders(headers);

            HttpResponse response = request.execute();

            this.isLiked = false;
            this.likeSongButton.setText("Like this song");
        } catch (Exception e) {

        }
    }

    private void initControls(){
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        try{
            volumeSeekbar = (SeekBar)findViewById(R.id.likesSoundSeekBar);
            this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onStopTrackingTouch(SeekBar arg0){}

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {}

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playGenres(){
        Intent intent = new Intent(this, PlayGenres.class);
        intent.putExtra("token", jwt);
        startActivity(intent);
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
                this.id = "" + id;
                String artist = songInfo.get("artist").getAsString();
                String year = songInfo.get("year").getAsString();
                String album = songInfo.get("album").getAsString();
                String genre = songInfo.get("genre").getAsString();
                String title = songInfo.get("title").getAsString();
                String track = songInfo.get("track").getAsString();

                this.songName.setText(title);
                this.songArtist.setText(artist);
                this.songYear.setText(year);

                this.songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
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
                this.id = "" + id;
                String artist = songInfo.get("artist").getAsString();
                String year = songInfo.get("year").getAsString();
                String album = songInfo.get("album").getAsString();
                String genre = songInfo.get("genre").getAsString();
                String title = songInfo.get("title").getAsString();
                String track = songInfo.get("track").getAsString();

                this.songName.setText(title);
                this.songArtist.setText(artist);
                this.songYear.setText(year);

                this.songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
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
}