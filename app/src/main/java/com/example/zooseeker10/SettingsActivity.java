package com.example.zooseeker10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private boolean directionsType;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onDirectionsTypeSwitchClicked(View view){
        directionsType=true;
    }

    public void onBackButtonClicked(View view){
        Intent intent = getIntent();
        intent.putExtra("key", directionsType);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
