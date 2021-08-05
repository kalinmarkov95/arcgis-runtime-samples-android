package com.esri.arcgisruntime.sample.displaymap;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.TransparencySlider.Slider;
import com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem.LocationChangeListener;
import com.esri.arcgisruntime.sample.displaymap.location.LocationDisplayer;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    private LocationDisplay locationDisplay;

    private Spinner spinner;

    private TextView avalancheDangerLabel;

    private LocationDisplayer locationDisplayer;

    private FeatureLayerHandler featureLayerHandler;

    public MainActivity() {

        this.locationDisplayer = new LocationDisplayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        avalancheDangerLabel = new TextView(this);
        avalancheDangerLabel.setText("Изключен GPS");
        avalancheDangerLabel.setTextColor(Color.WHITE);
        avalancheDangerLabel.setPadding(5, 0, 100, 0);
        avalancheDangerLabel.setTypeface(null, Typeface.BOLD);
        avalancheDangerLabel.setTextSize(14);
        avalancheDangerLabel.setId(0);
        menu.add(0, 0, 1, "1").setActionView(avalancheDangerLabel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        LocationChangeListener locationChangeListener = new LocationChangeListener(mapView, locationDisplay, this);
        locationChangeListener.alertIfLocationInAvalancheTerrain(featureLayerHandler.getAvalancheLayer(), avalancheDangerLabel);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("Main map");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);
        displayBaseMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD, 41.76122, 23.44046, 1000000);
        locationDisplay = mapView.getLocationDisplay();
        spinner = findViewById(R.id.spinner);

        String echmishteBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishte_boundary/FeatureServer/0";
        String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

        featureLayerHandler = new FeatureLayerHandler(this, mapView, locationDisplay);
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(echmishteBoundaryURL));
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(pirinNationalParkBoundaryURL));

        Slider transparencySlider = new Slider(this, mapView, featureLayerHandler);
        transparencySlider.createSlider();

        featureLayerHandler.displayChangeFeatureLayerIcon();

        locationDisplayer.displayGPSServices(locationDisplay, spinner, this, mapView);
        //LocationChangeListener locationChangeListener = new LocationChangeListener(mapView, locationDisplay, this);
        //locationChangeListener.alertIfLocationInAvalancheTerrain(featureLayerHandler.getAvalancheLayer(), avalancheDangerLabel);

        /*LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.warning_text_layout, null);
        ((LinearLayout) v).setGravity(Gravity.RIGHT);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);*/
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

