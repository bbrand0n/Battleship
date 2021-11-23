package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Debug;
import android.text.InputType;
import android.util.Log;
import android.util.LogPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
                Player pObj = new Player(uName);
                Map<String, Object> p = new HashMap<>();
                p.put("name",       pObj.getName());
                p.put("shotsFired", pObj.getShots());
                p.put("shotsHit",   pObj.getHits());

                // Add to database
                mDb.child("players").child(uName).updateChildren(p);
                player = database.getReference("players/" + uName);
                addEventListener();
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
                    i.putExtra("name", player.getKey());
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