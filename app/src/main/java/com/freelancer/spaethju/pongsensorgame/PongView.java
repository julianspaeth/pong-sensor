package com.freelancer.spaethju.pongsensorgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class PongView extends SurfaceView implements Runnable{

    // This is our thread
    Thread gameThread = null;

    // We need a SurfaceHolder object
    // We will see it in action in the draw method soon.
    SurfaceHolder surfaceHolder;

    // A boolean which we will set and unset
    // when the game is running- or not
    // It is volatile because it is accessed from inside and outside the thread
    volatile boolean playing;

    // Game is paused at the start
    boolean paused = true;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // The size of the screen in pixels
    int screenX;
    int screenY;

    // The players bat
    Bar bar;

    // A ball
    Ball ball;

    // For sound FX
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int explodeID = -1;

    // The score
    int score = 0;

    // Lives
    int lives = 3;

    public PongView(Context context, int x, int y) {

    /*
        The next line of code asks the
        SurfaceView class to set up our object.
    */
        super(context);

        // Set the screen width and height
        screenX = x;
        screenY = y;

        System.out.println("Pong View");
        System.out.println(screenX);
        System.out.println(screenY);

        // Initialize sur and paint objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // A new bat
        bar = new Bar(screenX, screenY);

        // Create a ball
        ball = new Ball(screenX, screenY);

    /*
        Instantiate our sound pool
        dependent upon which version
        of Android is present
    */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }


        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("explode.ogg");
            explodeID = sp.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        setupAndRestart();

    }

    public void setupAndRestart(){

        // Put the mBall back to the start
        ball.reset(screenX, screenY);

        // if game over reset scores and mLives
        if(lives == 0) {
            score = 0;
            lives = 3;
        }

    }

    //Code the overridden run method.
    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

        /*
            Calculate the FPS this frame
            We can then use the result to
            time animations in the update methods.
        */
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }

    }

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    public void update(){
        // Move the bar if required
        bar.update(fps);
        ball.update(fps);

        //Check for bar colliding with bar
        if(RectF.intersects(bar.getRect(), ball.getRect())) {
            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(bar.getRect().top - 2);

            score++;
            ball.increaseVelocity();

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }

        // Bounce the mBall back when it hits the bottom of screen
        if(ball.getRect().bottom > screenY){
            ball.reverseYVelocity();
            ball.clearObstacleY(screenY - 2);

            // Lose a life
            lives--;
            sp.play(loseLifeID, 1, 1, 0, 0, 1);

            if(lives == 0){
                paused = true;
                setupAndRestart();
            }
        }

        // Bounce the mBall back when it hits the top of screen
        if(ball.getRect().top < 0){
            ball.reverseYVelocity();
            ball.clearObstacleY(12);

            sp.play(beep2ID, 1, 1, 0, 0, 1);
        }

        // If the mBall hits left wall bounce
        if(ball.getRect().left < 0){
            ball.reverseXVelocity();
            ball.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the mBall hits right wall bounce
        if(ball.getRect().right > screenX){
            ball.reverseXVelocity();
            ball.clearObstacleX(screenX - 22);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }
    }

    //Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (surfaceHolder.getSurface().isValid()) {

            // Draw everything here

            // Lock the mCanvas ready to draw
            canvas = surfaceHolder.lockCanvas();

            // Clear the screen with my favorite color
            canvas.drawColor(Color.parseColor("#080705"));

            // Choose the brush color for drawing
            paint.setColor(Color.parseColor("#FFFFFA"));

            // Draw the mBat
            canvas.drawRect(bar.getRect(), paint);

            // Draw the mBall
            canvas.drawRect(ball.getRect(), paint);


            // Change the drawing color to white
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mScore
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

            // Draw everything to the screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    // If the Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If the Activity starts/restarts
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
// So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                // Is the touch on the right or left?
                if(motionEvent.getX() > screenX / 2){
                    bar.setMovementState(bar.RIGHT);
                }
                else{
                    bar.setMovementState(bar.LEFT);
                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                bar.setMovementState(bar.STOPPED);
                break;
        }
        return true;
    }
}
