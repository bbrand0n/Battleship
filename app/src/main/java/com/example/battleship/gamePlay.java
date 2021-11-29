package com.example.battleship;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class gamePlay extends AppCompatActivity {

    Game game;
    Player player1, player2, player;
    int turn , host  ;
    BoardView  boardView, playerBoardView;
    Board player1Board, player2Board, board;
    String roomName;
    ValueEventListener shotListener;
    FirebaseDatabase database;
    DatabaseReference mDb, roomRef, playerRef;


    public static List<Integer> spX = new ArrayList<Integer>();
    public static List<Integer> spY = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Default stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);


        // Get database reference
        database = FirebaseDatabase.getInstance();
        mDb = database.getReference();


        Intent i = getIntent();
        player = i.getParcelableExtra("player");
        board = NewGameActivity.playerBoard;
        roomName = i.getStringExtra("roomName");

        player1Board = NewGameActivity.player1Board;
        player2Board = NewGameActivity.player2Board;

        boardView = (BoardView) findViewById(R.id.playBoard);
        boardView.setBoard(board);
        boardView.setClickable(false);
        turn = 1;
        //boardView.displayBoardsShips(true);


        if(roomName.equals(player.getName())) {
            host = 1;

            //player = player1;
            //playerBoard = player.getBoard();

            //boardView.setBoard(player1Board);
            boardView.displayBoardsShips(false);
            boardView.setEnabled(true);
            boardView.setClickable(true);
            //playBoard;
        }
        else {
            host = 2;
            //player = player2;
            //playerBoard = player.getBoard();
            //boardView.setBoard(player.getBoard());
            //boardView.displayBoardsShips(true);
            boardView.displayBoardsShips(true);
            boardView.setEnabled(false);
            boardView.setClickable(false);
        }






        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                //if(host == 1){

                mDb.child("rooms").child(roomName).child("lastTurn").setValue(host);
                mDb.child("rooms").child(roomName).child("lastShotX").setValue(x);
                mDb.child("rooms").child(roomName).child("lastShotY").setValue(y);
                boardView.setClickable(false);
                //boardView.setBoard(player.getBoard());
                boardView.displayBoardsShips(true);

                //board.setBoard(player2board);
                //turn = 2;
                //mDb.child("rooms").child(player1.getName()).child("lastTurn").setValue(turn);
                //}
//                else {
//                    mDb.child("rooms").child(roomName).child("lastTurn").setValue(host);
//                    mDb.child("rooms").child(roomName).child("lastShotX").setValue(x);
//                    mDb.child("rooms").child(roomName).child("lastShotY").setValue(y);
//                    shotBoard.setEnabled(false);
//
//                    //turn = 1;
//                    mDb.child("rooms").child(player1.getName()).child("lastTurn").setValue(turn);
//                }



            }
        });



        addLastShotListener();




        // Listen to incoming messages
        //addBoardTouchListener();

    }


    public void addLastShotListener() {

        roomRef = database.getReference("rooms/" + roomName);


//        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                roomRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Check for just changes in lastTurn, lastShotX, lastShotY
                        if(snapshot.child("lastTurn").exists() &&
                                snapshot.child("lastShotX").exists() &&
                                snapshot.child("lastShotY").exists()){

                            // For player1 receiving shot from player2
                            if(host == 1) {
                                if(snapshot.child("lastTurn").getValue(Integer.class).equals(2)){

                                    //boardView.setBoard(playerBoard);
                                    boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                            snapshot.child("lastShotY").getValue(Integer.class));
                                    boardView.displayBoardsShips(false);
                                    boardView.setClickable(true);

//                        shotBoard.setBoard(player1board);
//                        shotBoard.displayBoardsShips(true);
//                        shotBoard.setEnabled(false);
                                }
                            }
                            // For player2 receiving shot from player1
                            else if(host == 2) {
                                if(snapshot.child("lastTurn").getValue(Integer.class).equals(1)) {

                                    //boardView.setBoard(player2Board);


                                    boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                            snapshot.child("lastShotY").getValue(Integer.class));
                                    boardView.displayBoardsShips(false);
                                    boardView.setClickable(true);
                                }

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // Check for just changes in lastTurn, lastShotX, lastShotY
                if(snapshot.child("lastTurn").exists() &&
                        snapshot.child("lastShotX").exists() &&
                            snapshot.child("lastShotY").exists()){

                    // For player1 receiving shot from player2
                    if(host == 1) {
                        if(snapshot.child("lastTurn").getValue(Integer.class).equals(2)){

                            //boardView.setBoard(playerBoard);
                            boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                    snapshot.child("lastShotY").getValue(Integer.class));
                            boardView.displayBoardsShips(false);
                            boardView.setClickable(true);

//                        shotBoard.setBoard(player1board);
//                        shotBoard.displayBoardsShips(true);
//                        shotBoard.setEnabled(false);
                        }
                    }
                    // For player2 receiving shot from player1
                    else if(host == 2) {
                        if(snapshot.child("lastTurn").getValue(Integer.class).equals(1)) {

                            //boardView.setBoard(player2Board);


                            boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                    snapshot.child("lastShotY").getValue(Integer.class));
                            boardView.displayBoardsShips(false);
                            boardView.setClickable(true);
                        }

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}