package com.esri.arcgisruntime.sample.displaymap.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.mapping.view.LocationDisplay;

import java.util.TimerTask;

public class LocationTimerTask extends TimerTask {

    private final String[] reqPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    private LocationDisplay locationDisplay;

    private int counter;

    Context applicationContext;

    public LocationTimerTask(LocationDisplay locationDisplay, Context applicationContext) {

        this.locationDisplay = locationDisplay;
        this.applicationContext = applicationContext;
        counter = 0;
    }

    @Override
    public void run() {

        System.out.println("The thread is running and the counter is equal to " + counter);
        counter++;

        boolean permissionCheck1 = ContextCompat.checkSelfPermission(this.applicationContext, reqPermissions[0]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck2 = ContextCompat.checkSelfPermission(this.applicationContext, reqPermissions[1]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck3 = ContextCompat.checkSelfPermission(this.applicationContext, reqPermissions[2]) ==
                PackageManager.PERMISSION_GRANTED;

        System.out.println(permissionCheck1);
        System.out.println(permissionCheck2);
        System.out.println(permissionCheck3);

        System.out.println("Is location display started: " + this.locationDisplay.isStarted());
    }
}
