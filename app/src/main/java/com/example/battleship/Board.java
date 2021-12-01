package com.example.battleship;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Board implements Parcelable {

    public int size;
    private Location[][] board = null;
    //public List<Location>sp;

    //where ship has been shot
    private int placesShot = 0;

//    public Board(){
//      this(10);
//    }

    public Board(int size) {
        this.size = size;
        board = new Location[size()][size()];
        createBoard(board);
    }

    public Board(int size, List<Integer> opX, List<Integer> opY){
        this.size = size;
        board = new Location[size()][size()];
        createBoard(board);

        for(int x : opX) {
            for(int y : opY) {



            }
        }


    }



    protected Board(Parcel in) {
        size = in.readInt();
        placesShot = in.readInt();
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

    //create board and initialize array
    private void createBoard(Location[][] board){

        for(int y = 0; y < board.length; y++){
            for(int x = 0; x < board[0].length; x++){
                board[y][x] = new Location(x, y);
            }
        }
    }






    //place ship
    boolean placeShip(Ship ship, int x, int y, boolean dir){

        if(ship == null){
            return false;
        }

        removeShip(ship);

        List<Location> shipPlaces = new ArrayList<Location>();
        Location place;

        //Goes through places where ship will be placed.
        for(int i = 0; i < ship.getSize(); i++){
            if(dir){
                //if direction is true, then ship is horizontal
                place = placeAt(x+i, y);
            }
            else{
                //ship is vertical
                place = placeAt(x, y+i);
            }

            //If place was invalid or already had a ship, returns false and doesn't place ship
            if(place == null || place.hasShip()) {
                return false;
            }

            //If was a valid place then adds to list of places, and looks through other places
            shipPlaces.add(place);

        }


        for(Location placeWithShip: shipPlaces){
            placeWithShip.setShip(ship);
        }

        ship.setDir(dir);
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

                    // 000000000000000000000000000000000000000000000000000000000000000000000000
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
                    //000000000000000000000000000000000000000000000000000000000000000000000000000

                    board[i][j].removeShip();
                }
            }
        }
        ship.removeShip();
    }


    Location placeAt(int x, int y){
        if(board == null || isOutOfBounds(x,y) || board[y][x] == null){
            return null;
        }

        return board[y][x];
    }

    //hits coordinate given by player
    boolean hit(int x, int y){
        Location placeToHit = new Location(x, y);
        if(placeToHit == null){
            return false;
        }
        //If place hasn't been hit before, then hits the place.
        if(!placeToHit.isHit()){
            placesShot++;
            placeToHit.hit();
            return true;
        }
        return false;
    }

    //returns true if out of bounds
    boolean isOutOfBounds(int x, int y){
        return x >= size() || y >= size() || x < 0 || y < 0;
    }

    //amount of times board has been hot
    int numOfShots(){
        return placesShot;
    }
    int size() {
        return size;
    }


    //return places that have a ship and are hit
    List<Location> getShipHitPlaces() {

        List<Location> boardPlaces = getPlaces();
        List<Location> shipHitPlaces = new ArrayList<Location>();

        for (Location place : boardPlaces) {
            if (place.isHit() && place.hasShip()) {
                shipHitPlaces.add(place);
            }
        }
        return shipHitPlaces;
    }

    //returns the locations of the ships on the board
    private List<Location> getPlaces(){
        List<Location> boardPlaces = new LinkedList<Location>();
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                boardPlaces.add(board[i][j]);
            }
        }
        return boardPlaces;
    }

    ///true if all sunk
    boolean isAllSunk(){
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                Location place = board[i][j];
                if(place.hasShip() && !place.isHit()){
                    return false;
                }
            }
        }
        return true;
    }

    //true if all ships are hti
    boolean isAllHit(){
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                if(!board[i][j].isHit()){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString(){
        String boardString = "";
        if(board == null){
            return "Board is null";
        }
        for(int i = 0; i < board[0].length; i++){
            for(int j = 0; j < board.length; j++){
                Location place = board[i][j];
                Ship ghost = place.getShip();

                if(ghost != null){
                    String shipType = ghost.getName();
                    if(shipType.contains("aircraftcarrier"))
                        boardString += "5";
                    else if(shipType.contains("battleship"))
                        boardString += "4";
                    else if(shipType.contains("submarine"))
                        boardString += "3";
                    else if(shipType.contains("frigate"))
                        boardString += "2";
                    else //Sweeper
                        boardString += "1";
                }
                //empty place
                else{
                    boardString += "0";
                }
            }
        }

        return boardString;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size);
        dest.writeInt(placesShot);
    }
}
