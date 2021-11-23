package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewGameActivity extends AppCompatActivity {

    Button poke;
    TextView p1, p2;
    String player1 = "", player2 = "", playerName = "";
    String roomName = "", role = "";
    String message ="";

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
        player1  = i.getStringExtra("playerName");
        player2  = roomRef.child("rooms/" + player1).child("player2").toString();
        roomRef = roomRef.child("rooms/" + player1);



        // -------- SET HOST ----------
        if(roomName.equals(playerName)) { role = "host";  }
        else                            { role = "guest"; }


        addRoomEventListener();


        // -------- DISPLAY INFO -----------
        p1 = (TextView) findViewById(R.id.p1);
        p2 = (TextView) findViewById(R.id.p2);



    }

    private void addRoomEventListener() {

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Show players
                Iterable<DataSnapshot> players = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : players) {
                    //p1.setText(player1);
                    //p2.setText(player2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
            }
        });
    }
}