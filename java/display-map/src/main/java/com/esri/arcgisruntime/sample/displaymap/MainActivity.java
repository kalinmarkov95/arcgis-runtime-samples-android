package com.esri.arcgisruntime.sample.displaymap;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.TransparencySlider.Slider;
import com.esri.arcgisruntime.sample.displaymap.location.LocationDisplayer;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    private LocationDisplay locationDisplay;

    private Spinner spinner;

    private LocationDisplayer locationDisplayer;

    public MainActivity() {

        this.locationDisplayer = new LocationDisplayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);
        displayBaseMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD, 41.76122, 23.44046, 1000000);
        locationDisplay = mapView.getLocationDisplay();
        spinner = findViewById(R.id.spinner);

        String echmishteBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishte_boundary/FeatureServer/0";
        String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

        FeatureLayerHandler featureLayerHandler = new FeatureLayerHandler(this, mapView);
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(echmishteBoundaryURL));
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(pirinNationalParkBoundaryURL));

        Slider transparencySlider = new Slider(this, mapView, featureLayerHandler);
        transparencySlider.createSlider();

        featureLayerHandler.displayChangeFeatureLayerIcon();

        locationDisplayer.displayGPSServices(locationDisplay, spinner, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            locationDisplay.startAsync();
        } else {

            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            spinner.setSelection(0, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
    }

    private void displayBaseMap(BasemapStyle basemapStyle, double initialLat, double initialLong, int scale) {

        mapView = findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(basemapStyle);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(initialLat, initialLong, scale));
    }
}

