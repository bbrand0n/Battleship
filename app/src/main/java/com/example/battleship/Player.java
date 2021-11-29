package com.example.battleship;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

public class Player implements Parcelable {


    // Attributes
    public String name;
    public DocumentReference docRef;
    public int shotsFired;
    public int shotsHit;
    public Boolean ready;
    public Board board;

    // Constructor
    public Player() {
        this.name = "";
        this.docRef = null;
        this.shotsHit = 0;
        this.shotsFired = 0;
        this.board = null;
        this.ready = false;
    }


    // Constructor
    public Player(String name){
        this.name = name;
        this.docRef = null;
        this.shotsHit = 0;
        this.shotsFired = 0;
        this.board = null;
        this.ready = false;

    }




    // Getters
    public String   getName()    { return this.name; }
    public int      getShots()   { return this.shotsFired; }
    public int      getHits()    { return this.shotsHit; }
    public Board    getBoard()   { return this.board; }
    public Boolean  getReady()   { return this.ready; }


    // Setters
    public DocumentReference setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
        return this.docRef;
    }

    public void setBoard(Board board)   { this.board = board; }
    public void setReady(Boolean ready) { this.ready = ready; }




    // -------- PARCEL STUFF TO SEND OBJECTS BETWEEN ACTIVITIES ----------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(shotsFired);
        dest.writeInt(shotsHit);
        dest.writeByte((byte) (ready == null ? 0 : ready ? 1 : 2));
        dest.writeParcelable( board, flags);
    }

    protected Player(Parcel in) {
        name = in.readString();
        shotsFired = in.readInt();
        shotsHit = in.readInt();
        byte tmpReady = in.readByte();
        ready = tmpReady == 0 ? null : tmpReady == 1;
        board = in.readParcelable(Board.class.getClassLoader());
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}

