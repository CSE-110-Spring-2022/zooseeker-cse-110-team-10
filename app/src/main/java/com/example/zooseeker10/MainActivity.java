package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int VOICE_ACTIVITY_REQUEST_CODE = 10;
    public static ArrayList<String> selectedExhibits = new ArrayList<>();
    Button planButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planButton = this.findViewById(R.id.plan_btn);
    }

    public void onSearchButtonClicked(View view) {
        EditText searchBarView = findViewById(R.id.search_bar_view);
        String searchQuery = searchBarView.getText().toString();

        if (!searchQuery.isEmpty()) {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("search_query", searchQuery);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        }
    }

    public void onPlanButtonClicked(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        intent.putStringArrayListExtra("exhibits", selectedExhibits);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String exhibitId = data.getStringExtra("exhibitId");
                selectExhibit(exhibitId);
            }
        }
        else if (requestCode == VOICE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> speechResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = speechResult.get(0);
                TextView searchDisplay = findViewById(R.id.search_bar_view);
                searchDisplay.setText(spokenText);
            }
        }
    }

    /* CITATION
    https://developer.android.com/training/wearables/user-input/voice
    Android Developers: Voice input
    Used for information (how to use RecognizerIntent with ACTION_RECOGNIZE_SPEECH)
    */
    public void onAudioButtonClicked(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search for exhibits");

        Log.d("MainActivity", String.valueOf(intent.resolveActivity(getPackageManager())));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VOICE_ACTIVITY_REQUEST_CODE);
        }
        else {
            Toast.makeText(this, "Your device is not compatible with speech recognition.", Toast.LENGTH_LONG).show();
        }
    }

    public void selectExhibit(String exhibitId) {
        if (selectedExhibits.isEmpty()) {
            planButton.setVisibility(View.VISIBLE);
        }
        if (!selectedExhibits.contains(exhibitId)) {
            selectedExhibits.add(exhibitId);
            Log.d("MainActivity", exhibitId);
        }
    }
}