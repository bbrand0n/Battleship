package com.example.battleship;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Board implements Parcelable {

    public int size;
    private Location[][] board = null;

    //where ship has been shot
   private int shipHits = 0;


    public Board(int size) {
        this.size = size;
        board = new Location[size()][size()];

        //creation of board
        for(int y = 0; y < board.length; y++){
            for(int x = 0; x < board[0].length; x++){
                board[y][x] = new Location(x, y);
            }
        }
    }

    public Board(int size, List<Integer> opX, List<Integer> opY){
        this.size = size;

        //creation of board
        board = new Location[size()][size()];
        for(int y = 0; y < board.length; y++){
            for(int x = 0; x < board[0].length; x++){
                board[y][x] = new Location(x, y);
            }
        }

        for(int x : opX) {
            for(int y : opY) {



            }
        }


    }


    protected Board(Parcel in) {
        size = in.readInt();
        shipHits = in.readInt();
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };


    //place ship
    boolean placeShip(Ship ship, int x, int y, boolean direction){
        if(ship == null){
            return false;
        }

        removeShip(ship);

        List<Location> shipPlaces = new ArrayList<Location>();
        Location place;


        for(int i = 0; i < ship.getSize(); i++){
            if(direction){
                //horizontal
                place = placeAt(x+i, y);
            }
            else{
                //ship is vertical
                place = placeAt(x, y+i);
            }
        }

        ship.setDirection(direction);
        ship.placeShip(shipPlaces);

        // 0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

        List<Location>sp = shipPlaces;

        for (int i = 0; i < sp.size(); i++)
        {
            gamePlay.spX.add(new Integer(sp.get(i).getX()));
            gamePlay.spY.add(new Integer(sp.get(i).getY()));
            System.out.println(sp.get(i).getX() + " "
                    + sp.get(i).getY() + " "
                    + sp.get(i).isHit() + " "
                    + sp.get(i).getShip().getName() + " ");
        }
        System.out.println("__________________");
        for (int i = 0; i < gamePlay.spX.size(); i++) {
            System.out.println(gamePlay.spX.get(i) + " " + gamePlay.spY.get(i));
        }
        // 0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

        return true;
    }

    //remove all ships from board
    private void removeShip(Ship ship){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j].hasShip(ship)){

                    for (int k = 0; k < gamePlay.spX.size(); k++)
                    {
                        int bx = gamePlay.spX.get(k);
                        int by = gamePlay.spY.get(k);

                        if ((bx == j) && (by == i)){
                            gamePlay.spX.remove(k);
                            gamePlay.spY.remove(k);
                            break;
                        }
                    }
                }
            }
        }
    }


    Location placeAt(int x, int y){
        if(board == null || isOutOfBounds(x,y) || board[y][x] == null){
            return null;
        }

        return board[y][x];
    }


    //returns true if out of bounds
    boolean isOutOfBounds(int x, int y){
        return x >= size() || y >= size() || x < 0 || y < 0;
    }

    int size() {
        return size;
    }


    //return places that have a ship and are hit
    List<Location> getShipHitPlaces() {

        List<Location> shipHitPlaces = new ArrayList<Location>();
        return shipHitPlaces;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size);
        dest.writeInt(shipHits);
    }
}
