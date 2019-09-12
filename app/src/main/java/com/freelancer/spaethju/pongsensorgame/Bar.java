package com.freelancer.spaethju.pongsensorgame;

import android.graphics.RectF;

public class Bar {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our barPlayer will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our bat
    private float coordX;

    // Y is the top coordinate
    private float coordY;

    private float initX, initY;

    // This will hold the pixels per second speed that
    // the bat will move
    private float speed;

    // Which ways can the bat move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the bat moving and in which direction
    private int barMoving = STOPPED;

    // The screen length and width in pixels
    private int screenX;
    private int screenY;

    // This is the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Bar(int x, int y, String position) {

        screenX = x;
        screenY = y;

        // 1/6 screen width wide
        length = screenX/8f;

        // 1/25 screen height high
        height = screenY/40f;



        if (position.equals("bottom")) {
            // Start barPlayer
            coordX = screenX/2f;
            coordY = screenY - 100;
            rect = new RectF(coordX, coordY, coordX + length, coordY + height);
        }
        if (position.equals("top")) {
            // Start computerPlayer
            coordX = screenX/2f;
            coordY = 100;
            rect = new RectF(coordX, coordY-height, coordX + length, coordY);
        }

        initX = coordX;
        initY = coordY;

        // How fast is the bat in pixels per second
        speed = screenX;
        // Cover entire screen in 1 second
    }

    public void reset() {
        rect = new RectF(initX, initY-height, initX + length, initY);
    }

    // This is a getter method to make the rectangle that
    // defines our bat available in PongView class
    public RectF getRect(){
        return rect;
    }

    // This method will be used to change/set if the bat is going
    // left, right or nowhere
    public void setMovementState(int state){
        barMoving = state;
    }

    public void moveLeft() {
        barMoving = LEFT;
    }

    public void moveRight() {
        barMoving = RIGHT;
    }

    public void hold() {
        barMoving = STOPPED;
    }

    // This update method will be called from update in PongView
    // It determines if the Bat needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps){

        if(barMoving == LEFT){
            coordX = coordX - speed / fps;
        }

        if(barMoving == RIGHT){
            coordX = coordX + speed / fps;
        }

        // Make sure it's not leaving screen
        if(rect.left < 5){ coordX = 5; } if(rect.right-5 > screenX){
            coordX = screenX -
                    // The width of the Bat
                    (rect.right - rect.left + 10);
        }

        // Update the Bat graphics
        rect.left = coordX;
        rect.right = coordX + length;

    }

    public float getCoordX() {
        return coordX;
    }

    public float getCoordY() {
        return coordY;
    }
}
