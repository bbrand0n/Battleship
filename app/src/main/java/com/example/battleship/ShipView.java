package com.example.battleship;

import android.widget.ImageView;

public class ShipView {

    //drawn images that each ship uses
    private ImageView shipImage;

    //type of ship (ex: battleship)
    private Ship model;


    private boolean isSelected = false;

    //new ship
    ShipView(ImageView image, Ship newShip){
        shipImage = image;
        model = newShip;
    }


    boolean isSelected(){ return isSelected;}

    //which ship is selected
    void setSelected(boolean selected){
        isSelected = selected;
    }


    ImageView getShipImage(){
        return shipImage;
    }
    public Ship getShip(){
        return model;
    }

}
