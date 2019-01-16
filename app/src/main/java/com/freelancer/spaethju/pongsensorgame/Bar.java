package com.freelancer.spaethju.pongsensorgame;

import android.graphics.RectF;

public class Bar {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our bar will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our bat
    private float coordX;

    // Y is the top coordinate
    private float coordY;

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
    public Bar(int x, int y) {

        screenX = x;
        screenY = y;

        System.out.println("Bar:");
        System.out.println(screenX);
        System.out.println(screenY);

        // 1/6 screen width wide
        length = screenX/6f;

        // 1/25 screen height high
        height = screenY/40f;

        // Start bar in roughly the sceen centre
        coordX = screenX/2f;
        coordY = screenY-50;

        rect = new RectF(coordX, coordY, coordX + length, coordY - height);

        // How fast is the bat in pixels per second
        speed = screenX;
        // Cover entire screen in 1 second
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
        if(rect.left < 0){ coordX = 0; } if(rect.right > screenX){
            coordX = screenX -
                    // The width of the Bat
                    (rect.right - rect.left);
        }

        // Update the Bat graphics
        rect.left = coordX;
        rect.right = coordX + length;
    }

}