package com.freelancer.spaethju.pongsensorgame;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class PlayActivity extends Activity {

    PongView pongView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Create Play activity");
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
        System.out.println("Pong view initialized");
    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume");
        // Tell the pongView resume method to execute
        pongView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pause");
        // Tell the pongView pause method to execute
        pongView.pause();
    }
}
