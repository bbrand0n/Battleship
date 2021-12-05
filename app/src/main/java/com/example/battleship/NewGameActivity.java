package com.example.battleship;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NewGameActivity extends AppCompatActivity {


    final private int PLAYER1_TURN = 1;
    final private int PLAYER2_TURN = 2;
    private BoardView boardView;
    public static Board playerBoard, player1Board, player2Board;
    private ShipView shipDragged = null;
    private List<ShipView> fleetView = new LinkedList<>();
    private Button shipsPlaced;
    private TextView player1name, player2name;
    private Board opponentBoard = null;
    public static boolean donePlacingShips = false;
    private int turn = 0;
    ValueEventListener boardListener, roomListener;
    Player player1 = new Player(), player2 = new Player(), player;
    String roomName = "";
    Game game;
    FirebaseDatabase database;
    DatabaseReference mDb, roomRef, playerRef;

    //000000000000000000000000000000000000000000000000000000000000000000
    public static List<Integer> spX = new ArrayList<Integer>();
    public static List<Integer> spY = new ArrayList<Integer>();
    //000000000000000000000000000000000000000000000000000000000000000000

    public static List<Location> locations = new ArrayList<>();

    private Thread readMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Get database reference
        database = FirebaseDatabase.getInstance();
        mDb = database.getReference();


        // Get player info passed through intent
        Intent i = getIntent();
        player = i.getParcelableExtra("player");
        roomName = i.getStringExtra("roomName");


        // Set player 1 if they created the room
        if(roomName.equals(player.getName())) {
            player1 = player;
            turn = PLAYER1_TURN;
        }
        else {
            player2 = player;
            turn = PLAYER2_TURN;
        }


        // Content is what it shows
        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_new_game, null);
        setContentView(layout);


        // Views
        boardView = findViewById(R.id.shipBoard);
        playerBoard = new Board(10);
        boardView.setBoard(playerBoard);
        boardView.displayBoardsShips(true);
        shipsPlaced = (Button) findViewById(R.id.shipsPlaced);


        // Ship images
        ImageView destroyer =   findViewById(R.id.destroyer);
        ImageView cruiser =     findViewById(R.id.cruiser);
        ImageView submarine =   findViewById(R.id.submarine);
        ImageView battleship =  findViewById(R.id.battleship);
        ImageView carrier =     findViewById(R.id.carrier);


        // Add fleet
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

        // Room listener for when player 2 joins
        addRoomListener();


        shipsPlaced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shipsPlaced.setText("Waiting...");

                if(turn == PLAYER1_TURN) {

                    playerRef = database.getReference("rooms/" + roomName );
                    addBoardListener();
                    player2Board = playerBoard;
                    game.player1.setBoard(playerBoard);
                    game.player1.setReady(true);
                    Map<String, Object> p1 = new HashMap<>();
                    p1.put("ready", player1.getReady());
                    mDb.child("rooms").child(roomName).child("player1").updateChildren(p1);



                }
                else if(turn == PLAYER2_TURN) {

                    playerRef = database.getReference("rooms/" + roomName );
                    addBoardListener();
                    player2Board = playerBoard;
                    game.player2.setBoard(playerBoard);
                    game.player2.setReady(true);
                    Map<String, Object> p2 = new HashMap<>();
                    p2.put("ready", player2.getReady());
                    mDb.child("rooms").child(roomName).child("player2").updateChildren(p2);



                }


            }
        });
    }



    public void addBoardListener() {

        playerRef.addValueEventListener(boardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // If both players are ready, start game
                if(snapshot.child("player2").child("ready").getValue(Boolean.class).equals(true) &&
                        snapshot.child("player1").child("ready").getValue(Boolean.class).equals(true)) {

                    // Create intent
                    donePlacingShips = true;
                    Intent i = new Intent(getApplicationContext(), gamePlay.class);
                    i.putExtra("player", player);
                    i.putExtra("board", playerBoard);
                    i.putExtra("roomName", roomName);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }





    public void addRoomListener() {

        roomRef = database.getReference("rooms/" + roomName );
        roomRef.addValueEventListener(roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Make sure the data change was player 2 joining
                    if(!dataSnapshot.child("player2").child("name").getValue(String.class).equals("")){

                        // Set isOpen to false when player 2 joins
                        mDb.child("rooms/" + roomName + "/isOpen").setValue(false);

                        // Create player objects from database
                        player2 = dataSnapshot.child("player2").getValue(Player.class);
                        player1 = dataSnapshot.child("player1").getValue(Player.class);

                        // Create game object from players
                        game = new Game(player1, player2);


                        System.out.println("\n++++++++++++++++++++++++++++++++++++");
                        System.out.println( player2.getName() + " " + player1.getName());
                        System.out.println("++++++++++++++++++++++++++++++++++++");

                        // TODO: put board listener in here
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //how to drag and place ship
    public void setBoardDragListener(final BoardView boardView, final Board board) {
        boardView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent ship) {
                //most cases do not matter until drop (youtube tutorial help)
                switch (ship.getAction()) {
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

                        //variables that matter on drop
                        float x = ship.getX();
                        float y = ship.getY();
                        int width;
                        int height;

                        if (!shipDragged.getShip().getDir()) {
                            width = shipDragged.getShipImage().getHeight();
                            height = shipDragged.getShipImage().getWidth();

                        } else {
                            width = shipDragged.getShipImage().getWidth();
                            height = shipDragged.getShipImage().getHeight();
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

                        if (!board.placeShip(shipDragged.getShip(), xGrid, yGrid, shipDragged.getShip().getDir())) {
                            return true;
                        }

                        if (!shipDragged.getShip().getDir()) {
                            shipDragged.getShipImage().setX(v.getX() + (xGrid * (v.getWidth() / 10)) - (height / 2) + (width / 2));
                            shipDragged.getShipImage().setY(v.getY() + (yGrid * (v.getHeight() / 10)) + (height / 2) - (width / 2));

                        } else {
                            shipDragged.getShipImage().setX(v.getX() + (xGrid * (v.getWidth() / 10)));
                            shipDragged.getShipImage().setY(v.getY() + (yGrid * (v.getHeight() / 10)));
                        }


                        boardView.invalidate();

                    default:
                        break;
                }
                if(allShipsPlaced() == true) shipsPlaced.setEnabled(true);
                return true;
            }
        });
    }

    public boolean allShipsPlaced() {
        for (ShipView ship : fleetView) {
            if (ship.getShip() == null) {
                return false;
            }
        }
        return true;
    }


    //followed through tutorial
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
                    shipDragged = shipView;
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

    // ----------- REMOVE LISTENER TO PREVENT CONSTANT NEW ACTIVITIES ---------
    @Override
    public void onPause() {
        super.onPause();
        roomRef.removeEventListener(boardListener);
        roomRef.removeEventListener(roomListener);
    }

    //trying to scale image
    private void setShipImage(final ShipView shipView) {
        setImageScaling(shipView.getShipImage());
        setTouchListener(shipView);
    }




}