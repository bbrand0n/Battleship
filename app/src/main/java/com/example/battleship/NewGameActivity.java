package com.example.battleship;

import static java.lang.Thread.sleep;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {


    private BoardView boardView;
    private Board playerBoard;
    private ShipView shipBeingDragged = null;
    private List<ShipView> fleetView = new LinkedList<>();
    private Button placeButton;
    private Board opponentBoard = null;
    public static boolean donePlacingShips = false;

    //000000000000000000000000000000000000000000000000000000000000000000
    public static List<Integer> spX = new ArrayList<Integer>();
    public static List<Integer> spY = new ArrayList<Integer>();
    //000000000000000000000000000000000000000000000000000000000000000000

    private Thread readMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //content is what it shows
        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_new_game, null);
        setContentView(layout);

        boardView = findViewById(R.id.shipBoard);
        playerBoard = new Board(10);
        boardView.setBoard(playerBoard);
        boardView.displayBoardsShips(true);

        placeButton = findViewById(R.id.shipsPlaced);


        ImageView destroyer = findViewById(R.id.destroyer);
        ImageView cruiser = findViewById(R.id.cruiser);
        ImageView submarine = findViewById(R.id.submarine);
        ImageView battleship = findViewById(R.id.battleship);
        ImageView carrier = findViewById(R.id.carrier);


        fleetView.add(new ShipView(destroyer, new Ship("destroyer", 2)));
        fleetView.add(new ShipView(cruiser, new Ship("cruiser", 3)));
        fleetView.add(new ShipView(submarine, new Ship("submarine", 3)));
        fleetView.add(new ShipView(battleship, new Ship("battleship", 4)));
        fleetView.add(new ShipView(carrier, new Ship("carrier", 5)));

        for (ShipView shipView : fleetView) {
            setShipImage(shipView);
        }

        setContentView(layout);
        setBoardDragListener(boardView, playerBoard);
        boardView.invalidate();

    }

    public void setBoardDragListener(final BoardView boardView, final Board board) {
        boardView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        break;

                    case DragEvent.ACTION_DROP:


                        float x = event.getX();
                        float y = event.getY();
                        int width;
                        int height;

                        if (!shipBeingDragged.getShip().getDir()) {
                            width = shipBeingDragged.getShipImage().getHeight();
                            height = shipBeingDragged.getShipImage().getWidth();

                        } else {
                            width = shipBeingDragged.getShipImage().getWidth();
                            height = shipBeingDragged.getShipImage().getHeight();
                        }

                        //x and y coordinates of top-left of image, relative to the board
                        float boardX = x - (width / 2);
                        float boardY = y - (height / 2);

                        int xy = boardView.locatePlace(boardX, boardY);
                        if (xy == -1) {
                            return true;
                        }
                        int xGrid = xy / 100;
                        int yGrid = xy % 100;

                        if (!board.placeShip(shipBeingDragged.getShip(), xGrid, yGrid, shipBeingDragged.getShip().getDir())) {
                            return true;
                        }

                        if (!shipBeingDragged.getShip().getDir()) {
                            shipBeingDragged.getShipImage().setX(v.getX() + (xGrid * (v.getWidth() / 10)) - (height / 2) + (width / 2));
                            shipBeingDragged.getShipImage().setY(v.getY() + (yGrid * (v.getHeight() / 10)) + (height / 2) - (width / 2));

                        } else {
                            shipBeingDragged.getShipImage().setX(v.getX() + (xGrid * (v.getWidth() / 10)));
                            shipBeingDragged.getShipImage().setY(v.getY() + (yGrid * (v.getHeight() / 10)));
                        }


                        boardView.invalidate();

                    default:
                        break;
                }
                return true;
            }
        });
    }

    public boolean allShipsPlaced() {
        for (ShipView ship : fleetView) {
            if (ship.getShip() == null) {
                return false;
            }

            if (!ship.getShip().isPlaced()) {
                return false;
            }
        }
        return true;
    }


    private void setTouchListener(ShipView shipView) {
        ImageView image = shipView.getShipImage();

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    ClipData data = ClipData.newPlainText("", "");


                    double rotationRad = Math.toRadians(image.getRotation());
                    final int w = (int) (image.getWidth() * image.getScaleX());
                    final int h = (int) (image.getHeight() * image.getScaleY());
                    double s = Math.abs(Math.sin(rotationRad));
                    double c = Math.abs(Math.cos(rotationRad));
                    final int width = (int) (w * c + h * s);
                    final int height = (int) (w * s + h * c);

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(image) {
                        @Override
                        public void onDrawShadow(Canvas canvas) {
                            canvas.scale(image.getScaleX(), image.getScaleY(), width / 2,
                                    height / 2);
                            canvas.rotate(image.getRotation(), width / 2, height / 2);
                            canvas.translate((width - image.getWidth()) / 2,
                                    (height - image.getHeight()) / 2);
                            super.onDrawShadow(canvas);
                        }

                        @Override
                        public void onProvideShadowMetrics(Point shadowSize,
                                                           Point shadowTouchPoint) {
                            shadowSize.set(width, height);
                            shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
                        }
                    };

                    image.startDrag(data, shadowBuilder, image, 0);
                    //image.setVisibility(View.INVISIBLE);
                    shipBeingDragged = shipView;
                    deselectAllShipViews();
                    select(shipView);

                    return true;
                } else {
                    return false;
                }
            }

        });
    }

    //rotate ship and place in the middle
    public void rotateButtonTapped(View v) {
        ShipView shipToRotate = findSelectedShip();
        if(shipToRotate != null) {
            rotateShip(shipToRotate);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        shipToRotate.getShipImage().setX(width / 3 + 10);
        shipToRotate.getShipImage().setY((height / 4) - 20);



        if (shipToRotate.getShip() != null) {
            for (Location place : shipToRotate.getShip().getPlacement()) {
                place.setShip(null);
            }
            shipToRotate.getShip().removeShip();
        }

        shipToRotate.getShipImage().setOnTouchListener(null);
        setTouchListener(shipToRotate); //Creates new touch listener to update the shadow builder
        boardView.invalidate();
    }



    //finds selected image
    private ShipView findSelectedShip() {
        for (ShipView shipView : fleetView) {
            if (shipView.isSelected()) {
                return shipView;
            }
        }
        return null;
    }

    //rotate ship
    private void rotateShip(ShipView shipToRotate) {
        if (shipToRotate.getShip().getDir()) {
            shipToRotate.getShipImage().setRotation(90);
            shipToRotate.getShip().setDir(false);
        } else {
            shipToRotate.getShipImage().setRotation(0);
            shipToRotate.getShip().setDir(true);
        }
    }

    //select image to rotate
    public void select(ShipView shipView) {
        shipView.setSelected(true);
        shipView.getShipImage().setBackgroundColor(Color.GREEN);
    }


    public void deselectAllShipViews() {
        for (ShipView shipView : fleetView) {
            shipView.setSelected(false);
            shipView.getShipImage().setBackgroundColor(Color.TRANSPARENT);
        }
    }

    //scale image to match board
    private void setImageScaling(ImageView image) {

        image.setAdjustViewBounds(true);

        ViewTreeObserver vto = image.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                image.setMaxHeight(boardView.getMeasuredHeight() / 10);
            }

        });
    }


    //trying to scale image
    private void setShipImage(final ShipView shipView) {
        setImageScaling(shipView.getShipImage());
        setTouchListener(shipView);
    }

    public void startGameTapped(View v){
        donePlacingShips = true;
        Intent i = new Intent(NewGameActivity.this, gamePlay.class);
        startActivity(i);




    }



}