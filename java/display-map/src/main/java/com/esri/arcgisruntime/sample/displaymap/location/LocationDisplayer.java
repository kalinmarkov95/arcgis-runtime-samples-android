package com.esri.arcgisruntime.sample.displaymap.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.sample.displaymap.R;
import com.esri.arcgisruntime.sample.displaymap.location.spinner.ItemData;
import com.esri.arcgisruntime.sample.displaymap.location.spinner.SpinnerAdapter;

import java.util.ArrayList;

public class LocationDisplayer {

    private final int requestCode = 2;

    private final String[] reqPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION };

    public void displayGPSServices(LocationDisplay locationDisplay, Spinner spinner, Activity mainActivity) {

        locationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            if (dataSourceStatusChangedEvent.isStarted())

                return;

            if (dataSourceStatusChangedEvent.getError() == null)

                return;

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
                spinner.setSelection(0, true);
            }
        });

        ArrayList<ItemData> gpsOptions = new ArrayList<>();
        gpsOptions.add(new ItemData("GPS спрян", R.drawable.locationdisplaydisabled));
        gpsOptions.add(new ItemData("GPS включен", R.drawable.locationdisplayon));
        gpsOptions.add(new ItemData("GPS центриран", R.drawable.locationdisplayrecenter));

        SpinnerAdapter adapter = new SpinnerAdapter(mainActivity, R.layout.spinner_layout, R.id.txt, gpsOptions);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        if (locationDisplay.isStarted()) {

                            locationDisplay.stop();
                        }
                        break;

                    case 1:
                        if (!locationDisplay.isStarted()) {

                            locationDisplay.startAsync();
                        }
                        break;

                    case 2:
                        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        if (!locationDisplay.isStarted()) {

                            locationDisplay.startAsync();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }
}
