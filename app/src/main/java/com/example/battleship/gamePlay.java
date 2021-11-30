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
    Board player1Board, player2Board, board;
    String roomName, player1Coord, player2Coord;
    ValueEventListener shotListener;
    FirebaseDatabase database;
    DatabaseReference mDb, roomRef, playerRef;

    //000000000000000000000000000000000000000000000000000000000000000000
    public static List<Integer> spX = new ArrayList<Integer>();
    public static List<Integer> spY = new ArrayList<Integer>();


    public static List<Integer> opX = new ArrayList<Integer>();
    public static List<Integer> opY = new ArrayList<Integer>();

    public static String playerCoor = new String("");

    String[] sa = new String[17];
    //000000000000000000000000000000000000000000000000000000000000000000


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

        for (int k = 0; k < 17; k++)
        {
            if (k < 16) {playerCoor = playerCoor + String.valueOf(spX.get(k)) + ",";}
            else{       playerCoor = playerCoor + String.valueOf(spX.get(k));}
        }

        playerCoor = playerCoor+"!";

        for (int k = 0; k < 17; k++){
            if (k < 16) {playerCoor = playerCoor + String.valueOf(spY.get(k)) + ",";}
            else{       playerCoor = playerCoor + String.valueOf(spY.get(k));}
        }
        if (host == 1){
            mDb.child("rooms").child(roomName).child("playerCoor1").setValue(playerCoor);
        }
        if (host == 2){
            mDb.child("rooms").child(roomName).child("playerCoor2").setValue(playerCoor);
        }



        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                //if(host == 1){

                mDb.child("rooms").child(roomName).child("lastTurn").setValue(host);
                mDb.child("rooms").child(roomName).child("lastShotX").setValue(x);
                mDb.child("rooms").child(roomName).child("lastShotY").setValue(y);
                boardView.setClickable(false);

                boardView.displayBoardsShips(true);


            }
        });



        // Listen to incoming shots
        addLastShotListener();


    }


    public void addLastShotListener() {

        roomRef = database.getReference("rooms/" + roomName);



                roomRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        // Get player coords from class
                        player1Coord = snapshot.child("playerCoor1").getValue(String.class);
                        player2Coord = snapshot.child("playerCoor2").getValue(String.class);

                        System.out.println("\n===================================");
                        System.out.println("Player 1 Coords: " + player1Coord);
                        System.out.println("Player 2 Coords: " + player2Coord);
                        System.out.println("\n===================================");

                        System.out.println("\n+++++++++++++++++++++++++++++++++++");
                        System.out.println("Player 1 Coords: " + player1Coord);
                        System.out.println("Player 2 Coords: " + player2Coord);
                        System.out.println("\n+++++++++++++++++++++++++++++++++==");

                        if ((host == 1) && (player2Coord != null)){
                            sa = player2Coord.split("!");
                            sa = sa[0].split(",");

                            for (int u = 0; u < sa.length; u++)
                            {
                                opX.add(new Integer(Integer.parseInt(sa[u])));
                            }

                            sa = player2Coord.split("!");
                            sa = sa[1].split(",");

                            for (int u = 0; u < sa.length; u++)
                            {
                                opY.add(new Integer(Integer.parseInt(sa[u])));
                            }
                        }

                        if ((host == 2) && (player1Coord != null)){
                            sa = player1Coord.split("!");
                            sa = sa[0].split(",");

                            for (int u = 0; u < sa.length; u++)
                            {
                                opX.add(new Integer(Integer.parseInt(sa[u])));
                            }

                            sa = player1Coord.split("!");
                            sa = sa[1].split(",");

                            for (int u = 0; u < sa.length; u++)
                            {
                                opY.add(new Integer(Integer.parseInt(sa[u])));
                            }
                        }

                        // Check for just changes in lastTurn, lastShotX, lastShotY
                        if (snapshot.child("lastTurn").exists() &&
                                snapshot.child("lastShotX").exists() &&
                                snapshot.child("lastShotY").exists()) {

                            // For player1 receiving shot from player2
                            if (host == 1) {
                                if (snapshot.child("lastTurn").getValue(Integer.class).equals(2)) {


                                    boardView.notifyBoardTouch(snapshot.child("lastShotX").getValue(Integer.class),
                                            snapshot.child("lastShotY").getValue(Integer.class));
                                    boardView.displayBoardsShips(false);
                                    boardView.setClickable(true);


                                }
                            }
                            // For player2 receiving shot from player1
                            else if (host == 2) {
                                if (snapshot.child("lastTurn").getValue(Integer.class).equals(1)) {


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