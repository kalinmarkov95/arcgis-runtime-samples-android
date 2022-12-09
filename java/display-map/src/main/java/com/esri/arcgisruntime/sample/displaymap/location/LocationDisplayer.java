package com.esri.arcgisruntime.sample.displaymap.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.mapping.view.LocationDisplay;

public class LocationDisplayer {

    private final int requestCode = 2;

    private final String[] reqPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION };

    public void displayGPSServices(LocationDisplay locationDisplay, Activity mainActivity) {

        locationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            if (dataSourceStatusChangedEvent.isStarted()) {

                return;
            }

            if (dataSourceStatusChangedEvent.getError() == null) {

                return;
            }
            boolean permissionCheck1 = ContextCompat.checkSelfPermission(mainActivity, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck2 = ContextCompat.checkSelfPermission(mainActivity, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED;

            if (!(permissionCheck1 && permissionCheck2)) {

                ActivityCompat.requestPermissions(mainActivity, reqPermissions, requestCode);

            } else {

                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
                locationDisplay.stop();
            }
        });
    }
}
