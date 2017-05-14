package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayGenres extends AppCompatActivity {

    String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_genres);

        Intent intent = getIntent();
        jwt = intent.getExtras().getString("token");

        Button classicalButton = (Button) findViewById(R.id.playClassicalButton);
        classicalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playGenre("Classical");
            }
        });

        Button rockButton = (Button) findViewById(R.id.playRockButton);
        rockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playGenre("Rock");
            }
        });
    }

    private void playGenre(String genre){
        Intent intent = new Intent(this, GenreSongPage.class);
        intent.putExtra("token", jwt);
        intent.putExtra("genre", genre);
        startActivity(intent);
    }
}
