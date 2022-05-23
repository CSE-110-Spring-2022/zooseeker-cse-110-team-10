package com.example.zooseeker10;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

public class LatLngs {
    @NonNull
    public static LatLng midpoint(LatLng l1, LatLng l2) {
        return new LatLng(
                (l1.latitude + l2.latitude) / 2,
                (l1.longitude + l2.longitude) / 2
        );
    }
}
