package com.example.battleship;

import java.io.Serializable;

public class Location implements Serializable {

    //xy coordinates
    private int x = 0;
    private int y = 0;

    //if location is hit, then true
    //will help with database potential
    private boolean isHit = false;

   //no ship of coordinate, will become 1 otherwise
    private Ship ship = null;


    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    boolean isHit(){
        return isHit;
    }

    //marks coordinate when hit
    void hit() {
        isHit = true;
    }


    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    //true if ship is placed on this location
    boolean hasShip() {
        return ship != null;
    }

    //does location have ship check
    boolean hasShip(Ship shipToCheck) {
        return ship == shipToCheck;
    }

    //remove or set ship
    void removeShip(){
        ship = null;
    }
    protected void setShip(Ship ship){
        this.ship = ship;
    }

    public Ship getShip(){
        return ship;
    }
}
