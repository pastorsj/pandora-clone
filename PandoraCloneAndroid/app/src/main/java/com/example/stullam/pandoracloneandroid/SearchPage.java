package com.example.stullam.pandoracloneandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SearchPage extends AppCompatActivity {

    LinearLayout songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        Button listSongsButton = (Button) findViewById(R.id.listSongs);
        listSongsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addButtons();
            }
        });

        songList = (LinearLayout) findViewById(R.id.songListLinearLayout);

        //songList.add(new Button());
        //addButtons();
    }

    private void addButtons(){
        //songList.add("hey girl");
        //songList.add
    }
}
