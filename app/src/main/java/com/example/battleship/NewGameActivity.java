package com.example.battleship;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    String player1, player2, gameId;
    DocumentReference game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------- DEFAULT STUFF -----------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);



        // -------- RETRIEVE INFORMATION --------
        Intent i = getIntent();
        player1  = i.getStringExtra("p1name");
        player2  = i.getStringExtra("p2name");



        // -------- DISPLAY INFO -----------
        TextView p1 = (TextView) findViewById(R.id.p1);
        TextView p2 = (TextView) findViewById(R.id.p2);




        // -------- GET DATABASE ---------
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        game = db.collection("games").document(player1);


        game.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists() && value != null){
                    if(value.get("player1") != null){
                        game.update("player1", value.get("player1").toString());
                        p1.setText("Player 1:  " + player1);
                    }
                    if(value.get("player2") != null){
                        game.update("player2", value.get("player2").toString());
                        p2.setText("Player 2:  " + player2);
                    }

                }

            }
        });

        // -------- UPDATE ACTIVITY WHEN NEW EVENT HAPPENS ----------
//        db.collection("games")
//                .whereEqualTo("player1", player1)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//
//
//                        for(QueryDocumentSnapshot doc : value){
//                            //game.set(value.getDocuments().get(0).getData());
//                            game.update(doc.getData());
//                            p1.setText("Player 1:  " + player1);
//                            p2.setText("Player 2:  " + player2);
//                        }
//                    }
//                });



    }


}