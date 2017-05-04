package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void skipToPage(View view) {
        Intent intent = new Intent(this, SongPage.class);
        startActivity(intent);
    }

    public void login(View view){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public void openSearch(View view){
        Intent intent = new Intent(this, SearchPage.class);
        startActivity(intent);
    }
}
