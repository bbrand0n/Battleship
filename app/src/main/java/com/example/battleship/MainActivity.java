package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Video background
    VideoView videoView;

    // Username input and button to submit
    EditText nameIn;
    String uName;
    Button find_room, create_game;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------- DEFAULT ----------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // -------- GET DATABASE ---------
        FirebaseFirestore db = FirebaseFirestore.getInstance();


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



        // --------- INPUT USERNAME -----------
        // Get username
        nameIn =       (EditText) findViewById(R.id.nameIn);
        find_room =    (Button) findViewById(R.id.find_room);
        create_game =  (Button) findViewById(R.id.create_game);



        // -----------  FIND ROOM  -------------
        // Find Room button
        find_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Extract string
                uName = nameIn.getText().toString();

                // Create new player object
                Player p = new Player(uName);

                // Add player to database
                addUser(p, db);

            }});



        // -----------  CREATE GAME  ------------
        // Create Game button
        create_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract string
                uName = nameIn.getText().toString();

                // Create new player object
                Player p = new Player(uName);

                // Add player to database
                DocumentReference player1 = addUser(p, db);

                // Create new game
                DocumentReference newGame = createGame(player1, db);

                // Launch new activity
                Intent i = new Intent(MainActivity.this, NewGameActivity.class);
                i.putExtra("player1_name", player1.getId() );
                i.putExtra("gameId",       newGame.getId());
                startActivity(i);
            }
        });



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


    public DocumentReference addUser(Player player, FirebaseFirestore db) {

        // Create the player
        Map<String, Object> user = new HashMap<>();
        user.put("name", player.getName());
        user.put("shotsFired", player.getShots());
        user.put("shotsHit", player.getHits());

        // Add player to database
        db.collection("players")
                .document(player.getName())
                    .set(user);

        // Return player
        return db.collection("players")
                .document(player.getName());

    }

    public DocumentReference createGame(DocumentReference player1, FirebaseFirestore db) {

        // Create game
        Map<String, Object> game = new HashMap<>();
        game.put("created", new Timestamp(new Date()));
        game.put("isOpen", true);
        game.put("player1", player1);

        // Add to database, auto-generating game ID
        return db.collection("games").add(game).getResult();

    }

    public void joinGame(CollectionReference games, DocumentReference player2, FirebaseFirestore db) {

        // Create query to find open games
        Query q = games.whereEqualTo("isOpen", true);

        // Get open game
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // If game is found, update game attributes
                if(task.isSuccessful()) {
                    // Retrieve reference to the found game
                    DocumentReference newGame = task.getResult()
                            .getDocuments().get(0).getReference();

                    // Update isOpen and players
                    newGame.update("isOpen", false);
                    newGame.update("player2", player2);
                }
                else{
                    // TODO: display error: unable to find game
                }


            }
        });

    }



}