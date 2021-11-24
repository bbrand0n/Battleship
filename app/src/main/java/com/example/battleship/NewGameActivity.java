package com.example.battleship;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {

    Button ready;
    TextView p1, p2;
    String player1 = "", player2 = "", playerName = "";
    String roomName = "", role = "";
    String message ="";
    List<String> playerslist;

    FirebaseDatabase database;
    DatabaseReference roomRef, messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------- DEFAULT STUFF -----------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);


        // --------- GET REFERENCE TO DATABASE ---------
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference();



        // -------- RETRIEVE NAMES --------
        Intent i = getIntent();
        playerName  = i.getStringExtra("playerName");
        roomName    = i.getStringExtra("roomName");
        roomRef     = database.getReference("rooms/" + roomName);
        playerslist = new ArrayList<>();


        // ------------ ADD LISTENER -------------
        addRoomEventListener();


        // -------- SET HOST ----------
        if(roomName.equals(playerName)) { role = "host";   player1 = roomName;   }
        else                            { role = "guest";  player2 = playerName; }



        // -------- GET VIEWS -----------
        p1 =    (TextView) findViewById(R.id.p1);
        p2 =    (TextView) findViewById(R.id.p2);
        ready = (Button)   findViewById(R.id.ready_up);
        p1.setText(roomName);


    }



    // Adding listener to the room
    private void addRoomEventListener() {

        // Set reference to room
        roomRef = database.getReference("rooms/" + roomName);

        // Listener
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Check if the data change was player 2 joining
                if(!dataSnapshot.child("player2").getValue(String.class).equals("")) {

                    // Get player 2 name and add it to both players screens
                    player2 = dataSnapshot.child("player2").getValue(String.class);
                    p2.setText(player2);

                    // Set isOpen to false if player 2 is in game
                    roomRef.child("isOpen").setValue(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
            }
        });
    }
}