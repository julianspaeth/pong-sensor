package com.freelancer.spaethju.pongsensorgame;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class PlayActivity extends Activity {

    private PongView pongView;
    private static final String TAG = "Play";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Initialize pongView and set it as the view
        pongView = new PongView(this, size.x, size.y);
        setContentView(pongView);
        Log.i(TAG, "Pong View initialized");

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Start game");
        // Tell the pongView resume method to execute
        pongView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Pause game");
        // Tell the pongView pause method to execute
        pongView.pause();
    }
}
