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
    ValueEventListener listener;
    FirebaseDatabase database;
    DatabaseReference mDb, roomRef, playerRef;

    //000000000000000000000000000000000000000000000000000000000000000000
    public static List<Integer> spX = new ArrayList<Integer>();
    public static List<Integer> spY = new ArrayList<Integer>();


    public static List<Integer> opX = new ArrayList<Integer>();
    public static List<Integer> opY = new ArrayList<Integer>();

    public static String playerCoor = new String("");

    String[] sa = new String[18];
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



        if(roomName.equals(player.getName())) {

            // For player 1
            host = 1;
            player1Coord = playerCoor;

        }
        else {
            // This is if player 2 is currently playing
            host = 2;
            player2Coord = playerCoor;

            // Initially set touchable to false (player 1s turn first)
            boardView.setTouchable(false);
            boardView.displayBoardsShips(true);

        }


        // Get other players coords
        getOtherCoords();


        // Upload player 1 coords to database
        if (host == 1)
            mDb.child("rooms").child(roomName).child("player1").child("playerCoor1").setValue(playerCoor);

        // Upload player 2 coords to database
        if (host == 2)
            mDb.child("rooms").child(roomName).child("player2").child("playerCoor2").setValue(playerCoor);


        // Listener for board touches
        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {


                // Make sure board is touchable
                if(!boardView.isTouchable()) return;


                // Upload last shot info
                mDb.child("rooms").child(roomName).child("lastTurn").setValue(host);
                mDb.child("rooms").child(roomName).child("lastShotX").setValue(x);
                mDb.child("rooms").child(roomName).child("lastShotY").setValue(y);

                // Set board untouchable
                boardView.setTouchable(false);
                boardView.displayBoardsShips(true);

            }
        });





    }


    public void getOtherCoords() {

        // Set reference to database
        roomRef = database.getReference("rooms/" + roomName);


        // Add listener for database
        roomRef.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Get player 1 coords from database
                if(host == 1)
                    player2Coord = snapshot.child("player2").child("playerCoor2").getValue(String.class);

                // Get player 2 coords from database
                else if(host == 2)
                    player1Coord = snapshot.child("player1").child("playerCoor1").getValue(String.class);



                System.out.println("\n===================================");
                System.out.println("Player 1 Coords: " + player1Coord);
                System.out.println("Player 2 Coords: " + player2Coord);
                System.out.println("\n===================================");

                System.out.println("\n+++++++++++++++++++++++++++++++++++");
                System.out.println("Player 1 Coords: " + player1Coord);
                System.out.println("Player 2 Coords: " + player2Coord);
                System.out.println("\n+++++++++++++++++++++++++++++++++==");


                // Retrieve player 2 ship places from database
                if ((host == 1) && (player2Coord != null)) {
                    sa = player2Coord.split("!");
                    sa = sa[0].split(",");

                    for (int u = 0; u < 17; u++) {
                        opX.add(new Integer(Integer.parseInt(sa[u])));
                    }

                    sa = player2Coord.split("!");
                    sa = sa[1].split(",");

                    for (int u = 0; u < 17; u++) {
                        opY.add(new Integer(Integer.parseInt(sa[u])));
                    }
                }


                // Retrieve player 2 ship places from database
                if ((host == 2) && (player1Coord != null)) {

                    // X
                    sa = player1Coord.split("!");
                    sa = sa[0].split(",");
                    for (int u = 0; u < 17; u++)
                        opX.add(new Integer(Integer.parseInt(sa[u])));

                    // Y
                    sa = player1Coord.split("!");
                    sa = sa[1].split(",");
                    for (int u = 0; u < 17; u++)
                        opY.add(new Integer(Integer.parseInt(sa[u])));

                }

                // Add shot listener
                addLastShotListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    public void addLastShotListener() {

        // Remove previous listener
        if(player1Coord != null && player2Coord != null) {
            roomRef.removeEventListener(listener);
        }


        // Set room reference
        roomRef = database.getReference("rooms/" + roomName);


        // Add listener for last shot
                roomRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        // Check for just changes in lastTurn, lastShotX, lastShotY
                        if (snapshot.child("lastTurn").exists() &&
                                snapshot.child("lastShotX").exists() &&
                                snapshot.child("lastShotY").exists()) {

                            // For player1 receiving shot from player2
                            if (host == 1) {
                                if (snapshot.child("lastTurn").getValue(Integer.class).equals(2)) {

                                    // Get last shot from database
                                    int x = snapshot.child("lastShotX").getValue(Integer.class);
                                    int y = snapshot.child("lastShotY").getValue(Integer.class);


                                    // See if last shot was a hit
                                    for (int i = 0; i < gamePlay.opX.size(); i++)
                                    {
                                        int bx = gamePlay.spX.get(i);
                                        int by = gamePlay.spY.get(i);

                                        // Paint board if hit
                                        if (((bx == x) && (by == y)) && ((boardView.paintBoard[y][x]) == -1)){

                                            boardView.paintBoard[y][x] = boardView.paintHit;
                                            break;
                                        }
                                    }

                                    // Enable touch
                                    boardView.setTouchable(true);
                                    boardView.displayBoardsShips(false);
                                }
                            }


                            // For player2 receiving shot from player1
                            else if (host == 2) {
                                if (snapshot.child("lastTurn").getValue(Integer.class).equals(1)) {

                                    // Get last shot from database
                                    int x = snapshot.child("lastShotX").getValue(Integer.class);
                                    int y = snapshot.child("lastShotY").getValue(Integer.class);


                                    // See if last shot was a hit
                                    for (int i = 0; i < gamePlay.opX.size(); i++)
                                    {
                                        int bx = gamePlay.spX.get(i);
                                        int by = gamePlay.spY.get(i);

                                        // Paint board if hit
                                        if (((bx == x) && (by == y)) && ((boardView.paintBoard[y][x]) < 1)){

                                            boardView.paintBoard[y][x] = boardView.paintHit;
                                            break;
                                        }

                                    }

                                    // Enable touch
                                    boardView.setTouchable(true);
                                    boardView.displayBoardsShips(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }});
            }
}