package com.example.zooseeker10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static boolean isBriefDirections = false;
    private Switch toggle;

    /**
     * Citation:
     * https://stackoverflow.com/questions/10576307/android-how-do-i-correctly-get-the-value-from-a-switch
     * Android How Do I Correctly Get The Value From A Switch
     * May 27th, 2022
     * Used mainly as a template for checking the state of a switch button to update directions type properly
     * D.J
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toggle = findViewById(R.id.directions_type_switch);
        toggle.setChecked(isBriefDirections);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isBriefDirections = isChecked;
            }
        });
    }

    /**
     * Citation:
     * https://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
     * Sending the data back to main activity in android
     * May 27th, 2022
     * Used mainly as a source of info and template to finish an activity with a return result to the calling activity
     * D.J
     */
    public void onBackButtonClicked(View view){
        Intent intent = getIntent();
        intent.putExtra("key", isBriefDirections);
        setResult(RESULT_OK, intent);
        Log.d("Settings Activity: ", "Finished");
        this.finish();
    }
}
