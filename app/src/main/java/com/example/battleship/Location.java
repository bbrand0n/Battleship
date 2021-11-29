package com.example.battleship;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Location implements Parcelable {

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

    protected Location(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        isHit = in.readByte() != 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeByte((byte) (isHit ? 1 : 0));
    }
}
