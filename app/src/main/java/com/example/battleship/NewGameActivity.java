package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NewGameActivity extends AppCompatActivity {

    String player1, gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------- DEFAULT STUFF -----------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);


        // -------- RETRIEVE INFORMATION --------
        Intent i = getIntent();
        player1  = i.getStringExtra("player1_name");
        gameId   = i.getStringExtra("gameId");


        // -------- DISPLAY INFO -----------
        TextView p1 = (TextView) findViewById(R.id.p1);
        p1.setText(player1);
    }
}