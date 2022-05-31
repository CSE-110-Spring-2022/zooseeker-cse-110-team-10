package com.example.zooseeker10;
import android.app.AlertDialog;

public class ReplanMessageDisplay {
    private final AlertDialog.Builder builder;
    private final DirectionsActivity activity;

    public ReplanMessageDisplay(DirectionsActivity activity) {
        this.activity = activity;

        this.builder = new AlertDialog.Builder(activity);
        this.builder.setTitle("Your route has been replanned");
        builder
                .setNeutralButton("Ok", (dialogInterface, i) -> {
                    activity.onReplanRequested();
                    dialogInterface.cancel();
                });
        builder.setCancelable(true);
    }

    public void showPrompt() {
        builder.create().show();
    }
}
