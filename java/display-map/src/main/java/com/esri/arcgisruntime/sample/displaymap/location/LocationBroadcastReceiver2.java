package com.esri.arcgisruntime.sample.displaymap.location;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationResult;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class LocationBroadcastReceiver2 extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE =
            "com.esri.arcgisruntime.sample.displaymap.location.UPDATE_LOCATION";

    private final String[] reqPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            boolean permissionCheck1 = ContextCompat.checkSelfPermission(context, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck2 = ContextCompat.checkSelfPermission(context, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck3 = ContextCompat.checkSelfPermission(context, reqPermissions[2]) ==
                    PackageManager.PERMISSION_GRANTED;

            System.out.println(permissionCheck1);
            System.out.println(permissionCheck2);
            System.out.println(permissionCheck3);

            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)) {

                LocationResult locationResult = LocationResult.extractResult(intent);

                if (locationResult != null) {

                    Location location = locationResult.getLastLocation();
                    String locationString = new StringBuilder("" + location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();
                    try {

                        System.out.println(locationString);

                    } catch (Exception e) {

                        Toast.makeText(context, locationString, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
