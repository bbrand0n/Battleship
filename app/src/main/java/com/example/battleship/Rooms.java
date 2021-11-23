package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rooms extends AppCompatActivity {


    ListView listView;
    Button createRoom;
    Game newGame;
    List<String> roomsList;
    String playerName = "";
    String roomName = "";
    FirebaseDatabase database;
    DatabaseReference mDb, roomRef, player1;
    DatabaseReference roomsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);


        // Get database reference
        database = FirebaseDatabase.getInstance();
        mDb = database.getReference();


        // Get player name and assign a room to player
        Intent i = getIntent();
        playerName = i.getStringExtra("name");
        roomName = playerName;


        // Get views
        listView =    (ListView) findViewById(R.id.listView);
        createRoom =  (Button)   findViewById(R.id.createRoom);
        roomsList =              new ArrayList<>();


        // Create Room button
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create room when clicked, set to player name
                createRoom.setText("CREATING ROOM");
                createRoom.setEnabled(false);
                roomName = playerName;


                // Create game object and set player as player1
                player1 = database.getReference("players/" + playerName);
                newGame = new Game(player1.getKey());
                Map<String, Object> game = new HashMap<>();
                game.put("player1", newGame.getPlayer1());
                game.put("player2", newGame.getPlayer2());
                game.put("isOpen",  newGame.getOpen());
                game.put("winner",  newGame.getWinner());


                // Get reference to room and add listener
                mDb.child("rooms").child(playerName).updateChildren(game);
                roomRef = database.getReference("rooms/" + roomName + "/player1");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Join existing room
                roomName = roomsList.get(position);
                roomRef = database.getReference("rooms/" + roomName);
                addRoomEventListener();
                roomRef.child("player2").setValue(playerName);
            }
        });

        // If new room is available
        addRoomsEventListener();
    }

    private void addRoomEventListener() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Join room
                createRoom.setText("CREATE ROOM");
                createRoom.setEnabled(true);
                Intent i = new Intent(getApplicationContext(), NewGameActivity.class);
                i.putExtra("playerName", playerName);
                startActivity(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
                createRoom.setText("CREATE ROOM");
                createRoom.setEnabled(true);
                Toast.makeText(Rooms.this, "Error!", Toast.LENGTH_SHORT);

            }
        });
    }

    private void addRoomsEventListener() {
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Show list of available rooms
                roomsList.clear();
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : rooms) {
                    roomsList.add(snapshot.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Rooms.this,
                            android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
            }
        });
    }
}