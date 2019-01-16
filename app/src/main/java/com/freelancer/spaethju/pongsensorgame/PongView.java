package com.freelancer.spaethju.pongsensorgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

    // The bar
    Bar barPlayer;
    Bar barComputer;

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
    int score_player = 0;
    int score_computer = 0;

    public PongView(Context context, int x, int y) {

    /*
        The next line of code asks the
        SurfaceView class to set up our object.
    */
        super(context);

        // Set the screen width and height
        screenX = x;
        screenY = y;


        // Initialize sur and paint objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // A new bat
        barPlayer = new Bar(screenX, screenY, "bottom");
        barComputer = new Bar(screenX, screenY, "top");
        barComputer.setMovementState(barComputer.LEFT);

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
        ball.reset(screenX/2, screenY/2);
        // if game over reset scores and mLives
        score_computer = 0;
        score_player = 0;

    }


    //Code the overridden run method.
    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

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

        //Check for bar colliding with barPlayer
        if(RectF.intersects(barPlayer.getRect(), ball.getRect())) {

            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(barPlayer.getRect().top - 2);

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }

        //Check for bar colliding with barComputer
        if(RectF.intersects(barComputer.getRect(), ball.getRect())) {

            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(barComputer.getRect().top + 2);

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }



        // Reset the ball when it hits the bottom of screen
        if(ball.getRect().bottom > screenY){
            score_computer++;

            ball.reset(screenX/2, screenY/2);
            sp.play(loseLifeID, 1, 1, 0, 0, 1);
        }

        if (barComputer.getRect().centerX() < 50) {
            barComputer.setMovementState(barComputer.RIGHT);
        }

        if (barComputer.getRect().centerX() > screenX - 50) {
            barComputer.setMovementState(barComputer.LEFT);
        }



        // Move the barPlayer if required
        barPlayer.update(fps);
        barComputer.update(fps);
        ball.update(fps);


        // Reset the ball when it hits the top of screen
        if(ball.getRect().top < 0){
            score_player++;

            ball.reset(screenX/2, screenY/2);
            ball.reverseYVelocity();
            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the ball hits left wall bounce
        if(ball.getRect().left < 0){
            ball.reverseXVelocity();
            ball.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the ball hits right wall bounce
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
            paint.setColor(Color.parseColor("#912F40"));
            // Draw the bars
            canvas.drawRect(barPlayer.getRect(), paint);
            paint.setColor(Color.parseColor("#FFFFFA"));
            canvas.drawRect(barComputer.getRect(), paint);

            paint.setColor(Color.parseColor("#FFFFFA"));
            // Draw the ball
            canvas.drawRect(ball.getRect(), paint);


            // Change the drawing color to white
            paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawLine(0, screenY/2-10, screenX, screenY/2-10, paint);
            // Draw the mScore
            paint.setTextSize(70);


            canvas.drawText(Integer.toString(score_computer), screenX - screenX/10, screenY/2 - screenY/25, paint);
            paint.setColor(Color.parseColor("#912F40"));
            canvas.drawText(Integer.toString(score_player), screenX - screenX/10, screenY/2 + screenY/25, paint);

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
                    barPlayer.setMovementState(barPlayer.RIGHT);
                }
                else{
                    barPlayer.setMovementState(barPlayer.LEFT);
                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                barPlayer.setMovementState(barPlayer.STOPPED);
                break;
        }
        return true;
    }
}
