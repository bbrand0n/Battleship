package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    // Video background
    VideoView videoView;

    // Username input and button to submit
    EditText nameIn;
    String uName;
    Button find_room;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------- DEFAULT ----------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // --------- VIDEO -----------
        // Instantiate videoView
        videoView = findViewById(R.id.videoView);
        // Parse video from raw folder to uri instance
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.boats);
        // Set uri to videoView
        videoView.setVideoURI(uri);
        // Start videoView by listener
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Set video looping
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }});
        // Start the video
        videoView.start();



        // --------- INPUT -----------
        // Get username
        nameIn = (EditText) findViewById(R.id.nameIn);
        find_room = (Button) findViewById(R.id.find_room);

        // Set button listener
        find_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract string
                uName = nameIn.getText().toString();
                // Testing purposes to make sure we get correct input!
                Toast.makeText(MainActivity.this, "Hello " + uName, Toast.LENGTH_SHORT).show();

                // TODO - pass name into room
            }});



        // -------- QUIT ------------
        Button quit = (Button) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

    }





}