package com.example.battleship;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {


    // Attributes
    public Player player1;
    public Player player2;
    public Boolean isOpen;
    public String winner;


    // Empty constuctor
    public Game() {
        this.player1 = null;
        this.player2 = null;
        this.isOpen = true;
        this.winner = null;
    }


    // 1 player constructor
    public Game(Player player1) {
        this.player1 = player1;
        this.player2 = new Player("");
        this.isOpen = true;
        this.winner = "";
    }


    // 2 player constructor
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.isOpen = false;
        this.winner = "";
    }




    public Player    getPlayer1()      { return this.player1; }
    public Player    getPlayer2()      { return this.player2; }
    public Boolean   getOpen()         { return this.isOpen; }
    public String    getWinner()       { return this.winner; }


    public void    setPlayer1(Player player)      {  this.player1 = player; }
    public void    setPlayer2(Player player)      {  this.player2 = player; }
    public void    setOpen(Boolean status)         { this.isOpen = status; }
    public void    setWinner(String winner)       {  this.winner = winner; }


    // ---------- PARCEL STUFF TO SHARE BETWEEN ACTIVITIES ---------
    protected Game(Parcel in) {
        player1 = in.readParcelable(Player.class.getClassLoader());
        player2 = in.readParcelable(Player.class.getClassLoader());
        byte tmpIsOpen = in.readByte();
        isOpen = tmpIsOpen == 0 ? null : tmpIsOpen == 1;
        winner = in.readString();
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(player1, flags);
        dest.writeParcelable(player2, flags);
        dest.writeByte((byte) (isOpen == null ? 0 : isOpen ? 1 : 2));
        dest.writeString(winner);
    }
}
