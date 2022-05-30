package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Source: codecap.org/create-a-splash-screen-welcom-screen-in-android-studio
 *
 * This class is used to switch to the activity stored on disk
 */
public class TrampolineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trampoline);

        // Garbage way of passing the activity. Refactor later.
        Globals.State.activity = this;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent homeIntent = StateManager.loadIntentFromFile(TrampolineActivity.this);
                startActivity(homeIntent);
                finish();
            }
        }, Globals.Debug.SPLASH_DELAY);
    }
}