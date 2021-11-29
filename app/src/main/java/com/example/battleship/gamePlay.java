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
    int turn = 1, host  ;
    BoardView  boardView, playerBoardView;
    Board playerBoard, board;
    String roomName;
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
        //game = i.getParcelableExtra("game");
        //game = i.getParcelableExtra("game");
        player = i.getParcelableExtra("player");
        //player1 = game.player1;
        //player2 = game.player2;
        //playerBoard = i.getParcelableExtra("board");
        //player.setBoard(playerBoard);
        //playerBoard = player.getBoard();
        roomName = i.getStringExtra("roomName");
        boardView = (BoardView) findViewById(R.id.playBoard);
        //board = new Board(10);


        if(roomName.equals(player.getName())) {
            host = 1;
            //player = player1;
            playerBoard = player.getBoard();
            boardView.setBoard(player.getBoard());
            boardView.setEnabled(true);
            //playBoard;
        }
        else {
            host = 2;
            //player = player2;
            playerBoard = player.getBoard();
            boardView.setBoard(player.getBoard());
            //boardView.displayBoardsShips(true);
            boardView.setEnabled(false);
        }


//        player1board = player1.getBoard();
//        player2board = player2.getBoard();

        //board = new BoardView(getApplicationContext());



        // Listen to incoming messages
        addBoardTouchListener();
        //board = new BoardView(getApplicationContext());
        //addLastShotListener();



    }

    public void addBoardTouchListener() {
        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                //if(host == 1){

                    mDb.child("rooms").child(roomName).child("lastTurn").setValue(host);
                    mDb.child("rooms").child(roomName).child("lastShotX").setValue(x);
                    mDb.child("rooms").child(roomName).child("lastShotY").setValue(y);
                    boardView.setEnabled(false);
                    boardView.setBoard(player.getBoard());
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

                addLastShotListener();

            }
        });
    }

    public void addLastShotListener() {

        roomRef = database.getReference("rooms/" + roomName);

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println("\n99999999999999999999999999");
                //System.out.println( snapshot.child() + " " + player1.getName());
                System.out.println("99999999999999999999999999");

                if(host == 1) {
                    if(snapshot.child("lastTurn").getValue(Integer.class).equals(2)){

                        boardView.setBoard(playerBoard);
                        boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                snapshot.child("lastShotY").getValue(Integer.class));



//                        shotBoard.setBoard(player1board);
//                        shotBoard.displayBoardsShips(true);
//                        shotBoard.setEnabled(false);
                    }
                }

                else {
                    if(snapshot.child("lastTurn").getValue(Integer.class).equals(1)) {


                        boardView.setBoard(playerBoard);

                        boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                snapshot.child("lastShotY").getValue(Integer.class));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}