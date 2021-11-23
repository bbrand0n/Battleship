package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    String uName, otherName;
    Button find_room, create_game, settings, quit;
    DocumentReference player1, player2, newGame;



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
        settings =     (Button) findViewById(R.id.settings);
        quit =         (Button) findViewById(R.id.quit);





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
                player1 = addUser(p, db);


                // Create new game object
                Game game = new Game(p);
                // Create new game
                newGame = createGame(player1, game,  db);


                // Launch activity
                Intent i = new Intent(getApplicationContext(), NewGameActivity.class);
                i.putExtra("p1name", player1.getId());
                i.putExtra("p2name", "Joining...");
                startActivity(i);
            }
        });





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
                player2 = addUser(p, db);


                // Display alert box to get other username
                AlertDialog.Builder builder = new AlertDialog
                        .Builder(MainActivity.this,
                        R.style.Base_Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Join Game");
                builder.setMessage("Enter opponents username");
                // Make the view
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                // FIND button
                builder.setPositiveButton("Find", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        otherName = input.getText().toString();
                        dialog.cancel();
                        joinGame(otherName, player2, db);
                    }});
                // CANCEL button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});


                // Show alert dialogue
                builder.show();

            }});


//        db.collection("games")
//                .whereEqualTo("isOpen", true)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        for(QueryDocumentSnapshot doc : value){
//
//                            value.getDocuments().get(0).getReference().set(doc);
//
//                            // Launch activity
//                            Intent i = new Intent(MainActivity.this, NewGameActivity.class);
//                            i.putExtra("p1name", doc.get(String.valueOf(player1)).toString());
//                            i.putExtra("p2name", doc.get(String.valueOf(player2)).toString());
//                            startActivity(i);
//                        }
//
//                    }
//                });






    }

    // --------- CREATE GAME -----------
    public DocumentReference createGame(DocumentReference player1, Game game,  FirebaseFirestore db) {

        // Create game map
        Map<String, Object> newGame = new HashMap<>();
        newGame.put("created", game.getCreated());
        newGame.put("isOpen", game.getOpen());
        newGame.put("player1", game.getPlayer1().getName());  // TODO: change to string ??
        newGame.put("player2", game.getPlayer2());
        newGame.put("winner", game.getWinner());


        // Add to database
        db.collection("games")
                .document(player1.getId())
                    .set(newGame);


        // Reference to game
        DocumentReference newGameDoc =  db.collection("games")
                                    .document(player1.getId());



        // Return game reference
        return newGameDoc;

    }




    public void joinGame(String player1name, DocumentReference player2, FirebaseFirestore db) {

        //
        player1 = db.collection("games").document(player1name);

        // Create query to find the game
        //DocumentReference game;
//        Query q = db.collection("games").whereEqualTo("player1", player1name);
//        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    Log.d("GREAT", "Joined game successfully: "
//                            + task.getResult().getDocuments().get(0));
//                }
//                else {
//                    Log.d("BAD", "Unable to find player");
//                    // TODO: go back to main menu
//                }
//            }
//        });

        // Get reference to game
        //DocumentReference game = q.get().getResult().getDocuments().get(0).getReference();
        DocumentReference game = db.collection("games").document(player1name);

        // Update fields
        game.update("isOpen", false);
        game.update("player2", player2.getId());

        Intent i = new Intent(getApplicationContext(), NewGameActivity.class);
        i.putExtra("p2name", player2.getId());
        i.putExtra("p1name", player1.getId());
        startActivity(i);



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
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("ADDED", "Player added! " + player.getName());
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ADDED", "Error adding player");
                    }});

        // Add document reference to player object
        return player.setDocRef(db.collection("players")
                .document(player.getName()));


    }



    // -------- SETTINGS ---------
    public void settings(View v){
        Intent i = new Intent(MainActivity.this, TestActivity.class);
        startActivity(i);
        // TODO: fix
    }


    // -------- QUIT ------------
    public void quit(View v){
        finish();
        System.exit(0);
    }


}