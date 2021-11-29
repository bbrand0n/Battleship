package com.example.battleship;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    Player player;
    List<String> roomsList;
    String playerName = "";
    String roomName = "1";
    FirebaseDatabase database;
    DatabaseReference mDb, roomRef, player1ref;
    DatabaseReference roomsRef;
    ValueEventListener listener;


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
        player = i.getParcelableExtra("player");
        roomName = player.getName();


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
                roomName = player.getName();


                // Get reference to room
                roomRef = database.getReference("rooms/" + roomName );
                addRoomEventListener();


                // Create game object and set player as player1
                newGame = new Game(player);
                Map<String, Object> game = new HashMap<>();
                game.put("player1", newGame.getPlayer1());
                game.put("player2", newGame.getPlayer2());
                game.put("isOpen",  newGame.getOpen());
                game.put("winner",  newGame.getWinner());


                // Get reference to room and add listener
                mDb.child("rooms").child(roomName).updateChildren(game);

            }
        });

        // Join game from list of available games
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Join existing room
                roomName = roomsList.get(position);
                roomRef = database.getReference("rooms/" + roomName );
                addRoomEventListener();


                // Push player object/map to database
                Map<String, Object> player2 = new HashMap<>();
                player2.put("name",         player.getName());
                player2.put("shotsFired",   player.getShots());
                player2.put("shotsHit",     player.getHits());
                mDb.child("rooms").child(roomName).child("player2").updateChildren(player2);

            }
        });


        // If new room is available
        addRoomsEventListener();

    }


    // ----------- REMOVE LISTENER TO PREVENT CONSTANT NEW ACTIVITIES ---------
    @Override
    public void onPause() {
        super.onPause();
        roomRef.removeEventListener(listener);
    }



    // ----------- Add listener for when player joins ------------
    private void addRoomEventListener() {

        roomRef.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Join room
                createRoom.setText("CREATE ROOM");
                createRoom.setEnabled(true);
                Intent i = new Intent(getApplicationContext(), NewGameActivity.class);



                // Pass player object into NewGameActivity
                i.putExtra("player", player);
                i.putExtra("roomName", roomName);


                // Start NewGameActivity
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


                // Iterate through available rooms
                for(DataSnapshot snapshot : rooms) {

                    // Only show games that are open
                    if(snapshot.child("isOpen").getValue(Boolean.class) == true) {

                        // Add room to list if open
                        roomsList.add(snapshot.getKey());

                        // Show on activity
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Rooms.this,
                                android.R.layout.simple_list_item_1, roomsList);
                        listView.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
            }
        });
    }

}