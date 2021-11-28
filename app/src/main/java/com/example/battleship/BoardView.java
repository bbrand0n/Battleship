package com.example.battleship;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends View {

    //000000000000000000000000000000000000000000000000000000000000000000000000000
    private final Paint paintMarker = new Paint();
    private int[] paintRow = new int[1000];
    private int[] paintCol = new int[1000];
    private boolean[] paintHit = new boolean[1000];
    private int paintRCUsed = -1;
    //000000000000000000000000000000000000000000000000000000000000000000000000000

    //custom view for board based off tutorial
    public interface BoardTouchListener { void onTouch(int x, int y);}

    //listen when board is touched
    private final List<BoardTouchListener> listeners = new ArrayList<>();

    //board color - online chart for colors
    private final int boardColor = Color.rgb(102, 163, 255);

    //paints board
    private final Paint boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        boardPaint.setColor(boardColor);
    }

    //gridline colors
    private final int boardLineColor = Color.BLACK;

    //paints grid line with width
    private final Paint boardLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        boardLinePaint.setColor(boardLineColor);
        boardLinePaint.setStrokeWidth(2);
    }

    //if true display ship on the board
    private boolean displayShips = false;

    //board to display by custom view
    private Board board;
    private int boardSize = 10;

    //create a new board with context to view
    public BoardView(Context context) {
        super(context);
        initRC();
    }

    //create board with attributes
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRC();
    }
    //000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    private void initRC(){
        for (int i = 0; i < 1000; i++){
            paintRow[i] = -1;
            paintCol[i] = -1;
            paintHit[i] = false;
        }
    }
    //000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    //create the board view with attributes
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //set view to display custom board
    public void setBoard(Board board) {
        this.board = board;
        this.boardSize = board.size();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
        if( event.getAction() == MotionEvent.ACTION_DOWN){
            System.out.println(event.getX() + " " + event.getY());
            return onTouchEvent2(event);
        }
        //000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int xy = locatePlace(event.getX(), event.getY());
                if (xy >= 0) {
                    notifyBoardTouch(xy / 100, xy % 100);
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    //draw the 2d board
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawShotPlaces(canvas);
        if(displayShips){
            drawShips(canvas);
        }
        drawShipHitPlaces(canvas);

        //0000000000000000000000000000000000000000000000000000000000000000000000000000000
        paintMarker.setStyle(Paint.Style.STROKE);
        paintMarker.setAntiAlias(true);
        paintMarker.setStrokeWidth(10);

        drawMarkers(canvas);
        //00000000000000000000000000000000000000000000000000000000000000000000000000000000

    }

    //draw any ships that are on the board
    private void drawShips(Canvas canvas){

        for(int x = 0; x < boardSize; x++){
            for(int y = 0; y < boardSize; y++){
                if(board.placeAt(x, y).hasShip()){
                    drawSquare(canvas, Color.argb(215, 255,255,255), x, y);
                }
            }
        }
    }
    //draw all the cells/locations of board
    private void drawShotPlaces(Canvas canvas) {
        // check the state of each place of the board and draw it.
        if(board == null){
            return;
        }
        for(int x = 0; x < boardSize; x++){
            for(int y = 0; y < boardSize; y++){
                if(board.placeAt(x, y).isHit()){
                    drawSquare(canvas, Color.RED, x, y);
                }
            }
        }
    }

    public void drawSquare(Canvas canvas, int color, int x, int y){
        boardPaint.setColor(color);
        int length = 98;
        float viewSize = maxCoord();
        float tileSize = viewSize / 10;  //10 Is how many tiles there are
        float offSet = 8;
        canvas.drawRect((tileSize* x) + offSet, (tileSize*y) + offSet, ((tileSize * x)+tileSize) - offSet, (((viewSize/10) * y)+tileSize) - offSet, boardPaint);
    }

    //draw a square over hits
    public void drawShipHitPlaces(Canvas canvas){
        if(board == null){
            return;
        }
        List<Location> shipHitPlaces = board.getShipHitPlaces();
        for(Location places : shipHitPlaces){
            drawSquare(canvas, Color.GREEN, places.getX(), places.getY());
        }

    }

    //draw horizontal and vertical grid lines
    private void drawGrid(Canvas canvas) {
        final float maxCoord = maxCoord();
        final float placeSize = lineGap();
        boardPaint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
        for (int i = 0; i < numOfLines(); i++) {
            float xy = i * placeSize;
            canvas.drawLine(0, xy, maxCoord, xy, boardLinePaint); // horizontal line
            canvas.drawLine(xy, 0, xy, maxCoord, boardLinePaint); // vertical line
        }
    }

    //calc gap of lines
    protected float lineGap() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) boardSize;
    }

    //number of lines
    private int numOfLines() {
        return boardSize + 1;
    }

    //max coordinates
    protected float maxCoord() {
        return lineGap() * (numOfLines() - 1);
    }


    public int locatePlace(float x, float y) {
        if (x <= maxCoord() && y <= maxCoord()) {
            final float placeSize = lineGap();
            int ix = (int) (x / placeSize);
            int iy = (int) (y / placeSize);
            return ix * 100 + iy;
        }
        return -1;
    }

    //listener stuff

    public void addBoardTouchListener(BoardTouchListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }


    public void removeBoardTouchListener(BoardTouchListener listener) {
        listeners.remove(listener);
    }


    private void notifyBoardTouch(int x, int y) {
        for (BoardTouchListener listener: listeners) {
            listener.onTouch(x, y);
        }
    }

    public void displayBoardsShips(boolean display){
        displayShips = display;
    }

    //000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    public boolean onTouchEvent2(MotionEvent event){
        if (!NewGameActivity.donePlacingShips) {return false;}

        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        int w = (this.getWidth()/10);
        System.out.println("board size : " + NewGameActivity.spX.size());

        if(action == MotionEvent.ACTION_DOWN){
            int row = (int)Math.ceil(y/w);
            int col = (int)Math.ceil(x/w);

            System.out.println("click: " + row + "," + col + " width: " + w);

            paintRCUsed++;

            for (int i = 0; i < NewGameActivity.spX.size(); i++)
            {
                int bx = NewGameActivity.spX.get(i);
                int by = NewGameActivity.spY.get(i);

                if ((bx == col - 1) && (by == row - 1)){
                    paintHit[paintRCUsed] = true;
                    break;
                }
            }

            paintRow[paintRCUsed] = row - 1;
            paintCol[paintRCUsed] = col - 1;
            invalidate();

            return true;
        }
        return false;
    }

    private void drawMarkers(Canvas canvas){
        for ( int i = 0; i < 1000; i++){
            if (paintRow[i] > -1) {
                if (paintHit[i]){
                    drawHit(canvas, paintRow[i], paintCol[i]);
                }
                else{drawMiss(canvas, paintRow[i], paintCol[i]);}
            }
        }
    }

    private void drawMiss(Canvas canvas, int row, int col){
        paintMarker.setColor(Color.WHITE);

        canvas.drawLine((float)((col+1)*getWidth()/10 - getWidth()/10*0.2),
                (float)(row*getWidth()/10 + getWidth()/10*0.2),
                (float)(col*getWidth()/10 + getWidth()/10*0.2),
                (float)((row+1)*getWidth()/10 - getWidth()/10*0.2),
                paintMarker);
        canvas.drawLine((float)(col*getWidth()/10 + getWidth()/10*0.2),
                (float)(row*getWidth()/10 + getWidth()/10*0.2),
                (float)((col+1)*getWidth()/10 - getWidth()/10*0.2),
                (float)((row+1)*getWidth()/10 - getWidth()/10*0.2),
                paintMarker);

        System.out.println("MISS: " + row + "," + col);
    }

    private void drawHit(Canvas canvas, int row, int col){
        paintMarker.setColor(Color.RED);

        canvas.drawOval((float)(col*getWidth()/10 + getWidth()/10*0.2),
                (float)(row*getWidth()/10 + getWidth()/10*0.2),
                (float)((col*getWidth()/10 + getWidth()/10) - getWidth()/10*0.2),
                (float)((row*getWidth()/10 + getWidth()/10) - getWidth()/10*0.2),
                paintMarker);

        System.out.println("HIT: " + row + "," + col);
    }
    //000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

}
