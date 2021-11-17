package com.example.battleship;

public class Player {

    // Attributes
    String name;
    int shotsFired = 0;
    int shotsHit = 0;

    // Constructor
    public Player() {}

    // Constructor
    public Player(String name){
        this.name = name;
    }

    public String getName() { return this.name; }
    public int getShots() { return this.shotsFired; }
    public int getHits() { return this.shotsHit; }
}

