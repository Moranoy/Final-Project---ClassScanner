package com.example.galbenabu1.classscanner.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.galbenabu1.classscanner.R;


public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 3; // 7 seconds of splash madness
    private final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.e(TAG, "SplashScreenActivity << OnCreate");

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        Log.e(TAG, "SplashScreenActivity >> OnCreate");
    }
}
