package com.example.battleship;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    // Video background
    VideoView videoView;

    // Username input and button to submit
    EditText nameIn;
    String uName = "";
    Button find_room, create_game, settings, quit;
    DatabaseReference player, mDb;
    FirebaseDatabase database;
    public static Player pObj = new Player("");

    // For text color in console logging
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_RESET = "\u001B[0m";

    // Start App
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------- DEFAULT ----------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // -------- GET DATABASE ---------
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        mDb = database.getReference();


        // --------- VIDEO STUFF-----------
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



        // --------- GET BUTTONS -----------
        nameIn =       (EditText) findViewById(R.id.nameIn);
        find_room =    (Button) findViewById(R.id.find_room);
        settings =     (Button) findViewById(R.id.settings);
        quit =         (Button) findViewById(R.id.quit);





        // ---------- GET ROOM LIST ------------
        find_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve name from EditText
                uName = nameIn.getText().toString();
                find_room.setText("Finding Room...");
                find_room.setEnabled(false);

                // Create player object
                pObj = new Player(uName);
                Map<String, Object> p = new HashMap<>();
                p.put("name",       pObj.getName());
                p.put("shotsFired", pObj.getShots());
                p.put("shotsHit",   pObj.getHits());

                // Add to database
                mDb.child("players").child(uName).updateChildren(p);
                player = database.getReference("players/" + uName);
                addEventListener();

                System.out.println("______________________________________________________________________________________________________");
                System.out.println(pObj.getName());
                System.out.println(mDb.child("rooms").child(pObj.getName()).getKey());
                System.out.println(COLOR_GREEN + "Added " + mDb.child("rooms").child(pObj.getName()).getKey() + " to database." + COLOR_RESET);
                System.out.println("______________________________________________________________________________________________________");
            }
        });
    }



    // --------- LISTENS TO CHANGES IN DATABASE --------------
    private void addEventListener() {

        // Read player from database
        player.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Found
                if(!player.equals("")) {

                    // Launch new activity
                    Intent i = new Intent(getApplicationContext(), Rooms.class);
                    i.putExtra("player", pObj);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
                find_room.setText("Find Room");
                find_room.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // -------- OPEN SETTINGS ---------
    public void settings(View v){
        Intent i = new Intent(MainActivity.this, TestActivity.class);
        startActivity(i);
        // TODO: fix
    }


    // -------- QUIT GAME ------------
    public void quit(View v){
        finish();
        System.exit(0);
    }


}