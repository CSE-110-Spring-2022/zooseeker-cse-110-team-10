package com.example.zooseeker10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class ReplanPrompt{
    private Activity activity;

    public ReplanPrompt(Activity activity){
        this.activity = activity;
    }

    public void showPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("You are off-track!");
        builder.setMessage("Do you want to replan?");
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DirectionsActivity.callReplan = true;
                dialogInterface.cancel();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
