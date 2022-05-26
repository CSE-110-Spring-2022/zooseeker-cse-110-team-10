package com.example.zooseeker10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private boolean directionsType=false;
    private Switch toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toggle=findViewById(R.id.directions_type_switch);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    directionsType=true;
                    Log.d("Directions Type", "True");
                }
                else{
                    directionsType=false;
                    Log.d("Directions Type", "False");
                }
            }
        });
    }

    public void onBackButtonClicked(View view){
        Intent intent = getIntent();
        intent.putExtra("key", directionsType);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
