package com.freelancer.spaethju.pongsensorgame;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    private RectF rect;
    private float veloX;
    private float veloY;
    private float width;
    private float height;

    public Ball(int screenX, int screenY){

        // Make the ball size relative to the screen resolution
        width = screenX/50f;
        height = width;

    /*
        Start the ball travelling straight up
        at a quarter of the screen height per second
    */
        veloY = screenY/4f;
        veloX = veloY;

        // Initialize the Rect that represents the ball
        rect = new RectF();

    }

    // Give access to the Rect
    public RectF getRect(){
        return rect;
    }

    // Change the position each frame
    public void update(long fps){
        rect.left = rect.left + (veloX / fps);
        rect.top = rect.top + (veloY / fps);
        rect.right = rect.left + width;
        rect.bottom = rect.top - height;
    }

    // Reverse the vertical heading
    public void reverseYVelocity(){
        veloY = -veloY;
    }

    // Reverse the horizontal heading
    public void reverseXVelocity(){
        veloX = -veloX;
    }

    public void setRandomXVelocity(){

        // Generate a random number either 0 or 1
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    // Speed up by 10%
    // A score of over 20 is quite difficult
    // Reduce or increase 10 to make this easier or harder
    public void increaseVelocity(){
        veloX = veloX + veloX / 10;
        veloY = veloY + veloY / 10;
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - height;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + width;
    }

    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + width;
        rect.bottom = y - 20 - height;
    }
}
