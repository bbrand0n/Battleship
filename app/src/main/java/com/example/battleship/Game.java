package com.example.battleship;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Date;

public class Game {

    public String player1;
    public String player2;
    public Boolean isOpen;
    public String winner;

    public Game() {
        this.player1 = null;
        this.player2 = null;
        this.isOpen = true;
        this.winner = null;
    }

    public Game(String player1) {

        this.player1 = player1;
        this.player2 = "";
        this.isOpen = true;
        this.winner = "";
    }

    public String    getPlayer1()      { return this.player1; }
    public String    getPlayer2()      { return this.player2; }
    public Boolean   getOpen()         { return this.isOpen; }
    public String    getWinner()       { return this.winner; }
}
