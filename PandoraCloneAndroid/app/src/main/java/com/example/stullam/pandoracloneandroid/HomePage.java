package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {
    private String jwt = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Intent intent = getIntent();
        jwt = intent.getExtras().getString("token");

        Button playRandomButton = (Button) findViewById(R.id.buttonToRandom);
        playRandomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playRandomSongs();
            }
        });

        Button playGenreButton = (Button) findViewById(R.id.buttonToGenre);
        playGenreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playGenreSongs();
            }
        });

        Button playLikedButton = (Button) findViewById(R.id.buttonToLiked);
        playLikedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playLikedSongs();
            }
        });
    }

    private void playRandomSongs(){
        Intent intent = new Intent(this, SongPage.class);
        intent.putExtra("token", jwt);
        intent.putExtra("ipAddress", "hello");
        startActivity(intent);
    }
    private void playGenreSongs(){
        Intent intent = new Intent(this, PlayGenres.class);
        intent.putExtra("token", jwt);
        startActivity(intent);
    }
    private void playLikedSongs(){
        Intent intent = new Intent(this, PlayByLikes.class);
        intent.putExtra("token", jwt);
        startActivity(intent);
    }
}
