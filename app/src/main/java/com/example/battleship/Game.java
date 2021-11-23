package com.example.battleship;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Date;

public class Game {

    public Player player1;
    public Player player2;
    public Timestamp created;
    public Boolean isOpen;
    public Player winner;

    public Game() {

        this.player1 = null;
        this.player2 = null;
        this.created = new Timestamp(new Date());
        this.isOpen = true;
        this.winner = null;
    }

    public Game(Player player1) {

        this.player1 = player1;
        this.player2 = null;
        this.created = new Timestamp(new Date());
        this.isOpen = true;
        this.winner = null;
    }

    public Player    getPlayer1()      { return this.player1; }
    public Player    getPlayer2()      { return this.player2; }
    public Timestamp getCreated()      { return this.created; }
    public Boolean   getOpen()         { return this.isOpen; }
    public Player    getWinner()       { return this.winner; }
}
