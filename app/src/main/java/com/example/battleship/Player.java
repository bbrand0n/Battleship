package com.example.battleship;

import com.google.firebase.firestore.DocumentReference;

public class Player {

    // Attributes
    public String name;
    public DocumentReference docRef;
    public int shotsFired;
    public int shotsHit;

    // Constructor
    public Player() {}

    // Constructor
    public Player(String name){

        this.name = name;
        this.docRef = null;
        this.shotsHit = 0;
        this.shotsFired = 0;
    }

    // Getters
    public String   getName()    { return this.name; }
    public int      getShots()   { return this.shotsFired; }
    public int      getHits()    { return this.shotsHit; }

    // Setter
    public DocumentReference setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
        return this.docRef;
    }
}

