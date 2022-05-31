package com.example.zooseeker10;

import android.app.AlertDialog;

public class ReplanPrompt {
    private final AlertDialog.Builder builder;
    private final DirectionsActivity activity;
    private boolean suppressPrompt;

    public ReplanPrompt(DirectionsActivity activity){
        this.activity = activity;

        this.builder = new AlertDialog.Builder(activity);
        this.builder.setTitle("You are off-track!");
        builder.setMessage("Do you want to replan?");
        builder.setCancelable(true);
        builder
                .setPositiveButton("Replan", (dialogInterface, i) -> {
                    activity.onReplanRequested();
                    dialogInterface.cancel();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });

        this.suppressPrompt = false;
    }

    public void showPrompt() {
        if (!this.suppressPrompt) {
            builder.create().show();
            this.suppressPrompt = true;
        }
    }

    public void enablePrompt() {
        this.suppressPrompt = false;
    }
}
