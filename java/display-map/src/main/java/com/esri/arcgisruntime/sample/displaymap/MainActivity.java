package com.esri.arcgisruntime.sample.displaymap;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.Widgets.Slider;
import com.esri.arcgisruntime.sample.displaymap.location.DataWrapper;
import com.esri.arcgisruntime.sample.displaymap.location.LocationBroadcastReceiver;
import com.esri.arcgisruntime.sample.displaymap.location.LocationService;
import com.esri.arcgisruntime.sample.displaymap.location.LocationTimerTask;
import com.esri.arcgisruntime.toolkit.compass.Compass;
import com.esri.arcgisruntime.toolkit.scalebar.Scalebar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity {

    //static MainActivity instance;

   /* LocationRequest locationRequest;

   FusedLocationProviderClient fusedLocationProviderClient;*/

   /*public static MainActivity getInstance() {

        return instance;
   }*/

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

        //new LocationDisplayer().displayGPSServices(locationDisplay, this);

       /* boolean permissionCheck1 = ContextCompat.checkSelfPermission(this, reqPermissions[0]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck2 = ContextCompat.checkSelfPermission(this, reqPermissions[1]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck3 = ContextCompat.checkSelfPermission(this, reqPermissions[2]) ==
                PackageManager.PERMISSION_GRANTED;

        if (!(permissionCheck1 && permissionCheck2 && permissionCheck3)) {

           requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);

        } else {

            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
            locationDisplay.stop();
        }*/
        requestLocationPermission();
        requestBackgroundLocationPermission();

        locationService = new LocationService(getCtx());
        locationServiceIntent = new Intent(getCtx(), locationService.getClass());
        //DataWrapper dataWrapper = new DataWrapper(locationDisplay, getApplicationContext());

        //locationServiceIntent.putExtra("locationDisplay", dataWrapper);
        if (!isMyServiceRunning(locationService.getClass())) {

            startService(locationServiceIntent);
        }

        //instance = this;

      /*  Intent intent = new Intent(this, LocationService.class);
        startService(intent);*/

        //updateLocation();

       /* Dexter.withContext(this).
                withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(
                new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        Toast.makeText(MainActivity.this, "You must accept this location", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();*/
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

            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestBackgroundLocationPermission() {

       /* if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        }
*/
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

   /* private void updateLocation() {

        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }*/

    /*private PendingIntent getPendingIntent() {

        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        startService(intent);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }*/

    /*public void updateTextView(String value) {

        System.out.println("Location is: " + value);
    }*/

    /*private void buildLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }*/

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

        /*Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {

                if (locationDisplay.isStarted()) {

                    Point screenPoint = mapView.locationToScreen(locationDisplay.getMapLocation());
                    ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture = mapView
                            .identifyLayerAsync(FeatureLayerHandler.todorkaATES, screenPoint, 0.01, false, -1);

                    identifyLayerResultFuture.addDoneListener(() -> {

                        try {

                            IdentifyLayerResult identifyLayerResult = identifyLayerResultFuture.get();

                            List<GeoElement> elements = identifyLayerResult.getElements();
                            if (elements.size() == 0) {

                                updateLabel("Няма лавинни данни!", Color.GREEN);
                            } else {

                                for (GeoElement element : elements) {

                                    if (element instanceof Feature) {

                                        Feature feature = (Feature) element;
                                        int newAvalancheDanger = (int) feature.getAttributes().get("gridcode");

                                        setTextAccordingToDangerLevel(newAvalancheDanger);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {

                            // TODO - improve error handling
                        }
                    });
                } else {

                    updateLabel("Изключен GPS", Color.GREEN);
                }
            }

            private void setTextAccordingToDangerLevel(int newAvalancheDanger) {

                switch (newAvalancheDanger) {

                    case 1:
                        updateLabel("На безопасно място", Color.GREEN);
                        break;

                    case 2:
                        updateLabel("Внимание", Color.MAGENTA);
                        break;

                    case 3:
                        updateLabel("Голям риск!", Color.RED);
                        vibrateAndSoundAlarm();
                        break;

                    default:
                        break;
                }
            }

            private void updateLabel(String text, int color) {

                if (!avalancheDangerLabel.getText().equals(text) || avalancheDangerLabel.getTextColors().getDefaultColor() == color) {

                    runOnUiThread(

                            () -> {

                                menu.removeItem(0);
                                avalancheDangerLabel.setTextColor(color);
                                avalancheDangerLabel.setText(text);

                                if (text.equals("Внимание!") || text.equals("В опасност!") || text.equals("Огромен риск!")) {

                                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                                    anim.setDuration(500);
                                    anim.setStartOffset(20);
                                    anim.setRepeatMode(Animation.REVERSE);
                                    anim.setRepeatCount(Animation.INFINITE);
                                    avalancheDangerLabel.startAnimation(anim);
                                    avalancheDangerLabel.setTextSize(16);
                                } else {

                                    avalancheDangerLabel.clearAnimation();
                                    avalancheDangerLabel.setTextSize(14);
                                }
                                menu.add(0, 0, 1, "1").setActionView(avalancheDangerLabel).
                                        setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                            }
                    );
                }
            }

            private void vibrateAndSoundAlarm() {

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {

                    v.vibrate(500);
                }
            }
        }, 0, 2000);*/

        return true;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        System.out.println(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION));
        System.out.println(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION));
        System.out.println(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION));

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            locationDisplay.startAsync();
        } else {

            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            locationDisplay.stop();
        }
    }*/

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