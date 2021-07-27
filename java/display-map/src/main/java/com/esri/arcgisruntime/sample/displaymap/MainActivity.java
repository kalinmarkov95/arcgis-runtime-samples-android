package com.esri.arcgisruntime.sample.displaymap;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.legend.LegendController;
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

        String echmishteSlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope1/FeatureServer/0";
        String echmishteBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishte_boundary/FeatureServer/0";
        String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

        displayFeatureLayer(echmishteSlopeURL);
        displayFeatureLayer(echmishteBoundaryURL);
        displayFeatureLayer(pirinNationalParkBoundaryURL);

        String pirin1SlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope11/FeatureServer/0";
        String pirin2SlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope2/FeatureServer/0";
        String pirin3SlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope3/FeatureServer/0";
        String pirin4SlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope4/FeatureServer/0";
        String pirin5SlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/dissolved5/FeatureServer/0";

        FeatureLayer pirin1 = displayFeatureLayer(pirin1SlopeURL);
        displayFeatureLayer(pirin2SlopeURL);
        displayFeatureLayer(pirin3SlopeURL);
        displayFeatureLayer(pirin4SlopeURL);
        displayFeatureLayer(pirin5SlopeURL);

        LegendController legendController = new LegendController(this);
        legendController.createLegendButton(mapView, pirin1, "Покажи/скрий легенда");

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

    private FeatureLayer displayFeatureLayer(String featureLayerURL) {

        ServiceFeatureTable featureTable = new ServiceFeatureTable(featureLayerURL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);
        mapView.getMap().getOperationalLayers().add(featureLayer);
        return featureLayer;
    }
}

