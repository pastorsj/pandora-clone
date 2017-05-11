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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_page);

        Intent intent = getIntent();
        ipAddress = intent.getExtras().getString("ipAddress");
        jwt = intent.getExtras().getString("token");
        TextView textView = (TextView) findViewById(R.id.songName);
        TextView ipTxt = (TextView) findViewById(R.id.ipAddressSong);
        textView.setText("JWT: " + jwt);
        ipTxt.setText("IP: " + ipAddress);

        try {
            this.play(new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/play/song/random")));
        } catch (MalformedURLException e) {
            // i had an exceptiong
        }

        ImageButton button = (ImageButton) findViewById(R.id.Play);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

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

                //URL songUrl = new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id);
                String songUrl = "http://ec2-34-224-40-124.compute-1.amazonaws.com:8080" + "/song/play/" + id;
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();



//                this.ais = AudioSystem.getAudioInputStream(songUrl);
//                this.clip = AudioSystem.getClip();
//                this.clip.open(ais);
//                this.clip.start();
//                Thread.sleep(100); // given clip.drain a chance to start
//                this.clip.drain();
//                while (this.streamPaused.get()) {
//                    Thread.sleep(100); // given clip.drain a chance to start
//                    this.clip.drain();
//                }
                //this.nextSong();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
