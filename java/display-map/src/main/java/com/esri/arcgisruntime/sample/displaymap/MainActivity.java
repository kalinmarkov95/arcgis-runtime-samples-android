package com.esri.arcgisruntime.sample.displaymap;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.Widgets.Slider;
import com.esri.arcgisruntime.sample.displaymap.location.LocationService;
import com.esri.arcgisruntime.toolkit.compass.Compass;
import com.esri.arcgisruntime.toolkit.scalebar.Scalebar;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity {

    public static MapView mapView;

    private SceneView sceneView;

    public static LocationDisplay locationDisplay;

    private FeatureLayerHandler featureLayerHandler;

    private int screenHeight;

    private int screenWidth;

    Intent locationServiceIntent;

    private LocationService locationService;

    private final int REQUEST_LOCATION_PERMISSION = 1;

    public static Context ctx;

    public Context getCtx() {

        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        this.screenHeight = metrics.heightPixels;
        this.screenWidth = metrics.widthPixels;

        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);

        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud7105386839,none,NKMFA0PL4S631R1DF239");

        displayBaseMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD, 41.76122, 23.44046, 1000000);
        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.startAsync();

        String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

        featureLayerHandler = new FeatureLayerHandler(this, mapView, sceneView, findViewById(R.id.showHideLegendButton));
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(pirinNationalParkBoundaryURL), mapView);

        Slider transparencySlider = new Slider(this, mapView, featureLayerHandler);
        transparencySlider.createSlider(screenWidth, screenHeight);

        Compass compass = findViewById(R.id.compass);
        compass.bindTo(mapView);

        Scalebar scalebar = findViewById(R.id.scalebar);
        scalebar.bindTo(mapView);

        featureLayerHandler.displayChangeFeatureLayerIcon(
                findViewById(R.id.changeFeatureLayerIcon),
                compass,
                scalebar);

        requestLocationPermission();
        requestBackgroundLocationPermission();

        locationService = new LocationService(getCtx());
        locationServiceIntent = new Intent(getCtx(), locationService.getClass());
        if (!isMyServiceRunning(locationService.getClass())) {

            startService(locationServiceIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (EasyPermissions.hasPermissions(this, perms)) {

            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestBackgroundLocationPermission() {

        String[] perms = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {

            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            System.out.println("PERMISSION ALREDY GRANYTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {

                System.out.println("Location service is running!");
                return true;
            }
        }

        System.out.println("Location service is not running!");
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        TextView avalancheDangerLabel = new TextView(this);
        avalancheDangerLabel.setText("Изключен GPS");
        avalancheDangerLabel.setTextColor(Color.GREEN);
        avalancheDangerLabel.setPadding(0, 0, 50, 0);
        avalancheDangerLabel.setTypeface(null, Typeface.BOLD);
        avalancheDangerLabel.setTextSize(14);
        avalancheDangerLabel.setId(0);
        menu.add(0, 0, 1, "1").setActionView(avalancheDangerLabel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
        sceneView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
        sceneView.resume();
    }

    @Override
    protected void onDestroy() {

        System.out.println("The application was destoyed!!!");
        stopService(locationServiceIntent);
        mapView.dispose();
        sceneView.dispose();
        super.onDestroy();
    }

    private void displayBaseMap(
            BasemapStyle basemapStyle,
            double initialLat,
            double initialLong,
            int scale) {

        mapView = findViewById(R.id.mapView);
        sceneView = findViewById(R.id.sceneView);
        mapView.setVisibility(View.VISIBLE);

        ArcGISMap map = new ArcGISMap(basemapStyle);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(initialLat, initialLong, scale));

        ArcGISTiledElevationSource elevationSource
                = new ArcGISTiledElevationSource("https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");

        ArcGISScene scene = new ArcGISScene(basemapStyle);
        scene.getBaseSurface().getElevationSources().add(elevationSource);
        sceneView.setScene(scene);
        sceneView.setVisibility(View.INVISIBLE);

        // on viewpoint change synchronize viewpoints
        mapView.addViewpointChangedListener(viewpointChangedEvent -> synchronizeViewpoints(mapView, sceneView));
        sceneView.addViewpointChangedListener(viewpointChangedEvent -> synchronizeViewpoints(sceneView, mapView));
    }

    /**
     * Synchronizes the viewpoint across GeoViews when the user is navigating.
     */
    private static void synchronizeViewpoints(GeoView navigatingGeoView, GeoView geoViewToSync) {

        if (navigatingGeoView.isNavigating()) {

            Viewpoint navigatingViewpoint =
                    navigatingGeoView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);
            geoViewToSync.setViewpoint(navigatingViewpoint);
        }
    }

    public void setGPSOff(MenuItem item) {

        if (locationDisplay.isStarted()) {

            locationDisplay.stop();
        }
    }

    public void setGPSOn(MenuItem item) {

        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        if (!locationDisplay.isStarted()) {

            locationDisplay.startAsync();
        }
    }
}