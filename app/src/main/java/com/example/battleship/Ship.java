package com.example.battleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ship implements Serializable {

    //attributes of ship
    public String name;
    private int size;

   //variables to keep track of point
   // will maybe help with database score tracking
    public boolean isSunk = false;
    private int amountShot = 0;

    //if ture then the ship would be horizontal, vertical if false
    private boolean dir = true;

    //list of coordinates/locations of ships placed
    // search for coordinate when opponent picks a cell
    public List<Location> placed = new ArrayList<>();

    public Ship(int size){
        this.size = size;
        name = " ";
    }
    Ship(String name, int size){
        this.name = name;
        this.size = size;
    }


    int getSize(){
        return size;
    }
    public String getName(){
        return name;
    }


    //sets the location where the ship is on, helps place ships
    void placeShip(List<Location> places){
        placed = places;
    }

    // number of times ship has been shot
    public int getAmountShot(){
        updateAmountShot();
        return amountShot;
    }

    //update number of shots
    private void updateAmountShot(){
        amountShot = 0;
        for (Location place : placed) {
            if(place.isHit()) {
                amountShot++;
            }
        }
    }

    //change ship direction
    void setDir(boolean newDir){
        dir = newDir;
    }
    boolean getDir(){
        return dir;
    }

    //return true when ship is on board
    boolean isPlaced(){
        return !placed.isEmpty();
    }

    //if ship has sunk, return true
    boolean isShipSunk(){

        //If field isSunk is true, then ship is sunk, otherwise check if ship is sunk by checking the places
        if(isSunk){
            return true;
        }
        //Return false if ship hasn't been placed
        if(placed == null){
            return false;
        }
        updateAmountShot();

        isSunk = (size <= amountShot);
        return isSunk;
    }


    List<Location> getPlacement(){
        return placed;
    }

    //TODO places should also remove the ship?
    void removeShip(){

        // 000000000000000000000000000000000000000000000000000000000000000000000000
        for(int i = 0; i < placed.size(); i++) {
            for (int k = 0; k < NewGameActivity.spX.size(); k++) {
                int bx = NewGameActivity.spX.get(k);
                int by = NewGameActivity.spY.get(k);

                if ((bx == placed.get(i).getX()) && (by == placed.get(i).getY())) {
                    NewGameActivity.spX.remove(k);
                    NewGameActivity.spY.remove(k);
                    break;
                }
            }
        }
        //000000000000000000000000000000000000000000000000000000000000000000000000000

        placed.clear();
    }
}