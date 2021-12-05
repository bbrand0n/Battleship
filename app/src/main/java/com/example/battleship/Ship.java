package com.example.battleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ship  {

    //attributes of ship
    public String name;
    private int size;

    //if ture then the ship would be horizontal, vertical if false
    private boolean dir = true;

    //list of coordinates/locations of ships placed
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

    //change ship direction
    void setDir(boolean newDir){
        dir = newDir;
    }
    boolean getDir(){
        return dir;
    }

    List<Location> getPlacement(){
        return placed;
    }

    //TODO places should also remove the ship?
    void removeShip(){

        // 000000000000000000000000000000000000000000000000000000000000000000000000
        for(int i = 0; i < placed.size(); i++) {
            for (int k = 0; k < gamePlay.spX.size(); k++) {
                int bx = gamePlay.spX.get(k);
                int by = gamePlay.spY.get(k);

                if ((bx == placed.get(i).getX()) && (by == placed.get(i).getY())) {
                    gamePlay.spX.remove(k);
                    gamePlay.spY.remove(k);
                    break;
                }
            }
        }
        //000000000000000000000000000000000000000000000000000000000000000000000000000

        placed.clear();
    }
}