package com.esri.arcgisruntime.sample.displaymap;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.LegendInfo;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.legend.Legend;
import com.esri.arcgisruntime.sample.displaymap.legend.LegendItem;
import com.esri.arcgisruntime.sample.displaymap.location.LocationDisplayer;
import com.esri.arcgisruntime.symbology.Symbol;

import java.util.ArrayList;
import java.util.List;

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
        displayBaseMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD, 41.76122, 23.44046, 50000);
        locationDisplay = mapView.getLocationDisplay();
        spinner = findViewById(R.id.spinner);

        String echmishteSlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope1/FeatureServer/0";
        String echmishteBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishte_boundary/FeatureServer/0";
        String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

        FeatureLayer echmishteSlope = displayFeatureLayer(echmishteSlopeURL);
        createLegendButton(echmishteSlope);

        displayFeatureLayer(echmishteBoundaryURL);
        displayFeatureLayer(pirinNationalParkBoundaryURL);

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

    private void createLegendButton(FeatureLayer featureLayer) {

        TextView textView = new TextView(this);
        textView.setText("Покажи/скрий легенда");
        textView.setTextSize(15);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.BLACK);

        Legend legend = createLegendForLayer(featureLayer);

        textView.setOnClickListener(view -> {

            if(legend.isShown()) {

                mapView.removeView(legend);
            } else {

                mapView.addView(legend);
            }
        });
        mapView.addView(textView);
    }

    private Legend createLegendForLayer(FeatureLayer featureLayer) {

        ListenableFuture<List<LegendInfo>> legendInfoFuture = featureLayer.fetchLegendInfosAsync();
        List<LegendItem> legendItems = new ArrayList<>();

        try {

            List<LegendInfo> legendInfoList = legendInfoFuture.get();

            for(LegendInfo legendInfo : legendInfoList) {

                Symbol legendSymbol = legendInfo.getSymbol();
                ListenableFuture<Bitmap> symbolSwatch = legendSymbol.createSwatchAsync(MainActivity.this, Color.BLUE);
                legendItems.add(new LegendItem(legendInfo.getName(), symbolSwatch.get()));
            }

            return new Legend(this, "Лавинна\nОпасност", legendItems, 0, 90);
        } catch (Exception e) {

            // TODO - handle this exception in some way
            return null;
        }
    }
}

