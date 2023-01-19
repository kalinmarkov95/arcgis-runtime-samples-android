package com.esri.arcgisruntime.sample.displaymap.location;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class LocationService extends Service {

    private final String[] reqPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    public int counter = 0;

    //private static Context applicationContext;

   // Intent intent;

    //private FusedLocationProviderClient mClient;

    //private static LocationDisplay locationDisplay;

    public LocationService(Context applicationContext) {

        super();
        //this.applicationContext = applicationContext;
       // this.mClient = LocationServices.getFusedLocationProviderClient(applicationContext);
        //this.intent = intent;
        //this.locationDisplay = locationDisplay;
        System.out.println("Here I am!!!");
    }

    public LocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*DataWrapper wrapper = ((DataWrapper) intent.getSerializableExtra("locationDisplay"));
        LocationDisplay locationDisplay = wrapper.getLocationDisplay();
        Context applicationContext = wrapper.getApplicationContext();*/

        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

       /* super.onDestroy();
        System.out.println("On destroy!");
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);

        sendBroadcast(broadcastIntent);
        stoptimertask();*/

        System.out.println("The service was destroyed!!!");
        Intent broadcastIntent = new Intent(this, LocationBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;

    private TimerTask timerTask;

    long oldTime = 0;

    public void startTimer() {

        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        //timerTask = new LocationTimerTask(locationDisplay, applicationContext);

        //schedule the timer, to wake up every 10 seconds
        timer.schedule(timerTask, 1000, 5000); //
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {

                System.out.println("The thread is running and the counter is equal to " + counter);
                counter++;

                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.ctx, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.ctx, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck3 = ContextCompat.checkSelfPermission(MainActivity.ctx, reqPermissions[2]) ==
                        PackageManager.PERMISSION_GRANTED;

                System.out.println(permissionCheck1);
                System.out.println(permissionCheck2);
                System.out.println(permissionCheck3);

                Log.i("LOGGER", "Is location display started: " + MainActivity.locationDisplay.isStarted());
                System.out.println(MainActivity.ctx);
                WorkManager mWorkManager = WorkManager.getInstance(MainActivity.ctx);

                mWorkManager.enqueue(OneTimeWorkRequest.from(AvalancheWarningWorker.class));

                /*Point screenPoint = MainActivity.mapView.locationToScreen(MainActivity.locationDisplay.getMapLocation());
                Log.i("LOGGER", Integer.toString(screenPoint.x));
                Log.i("LOGGER", Integer.toString(screenPoint.y));

                ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture = MainActivity.mapView
                        .identifyLayerAsync(FeatureLayerHandler.todorkaATES, screenPoint, 0.01, false, -1);

                identifyLayerResultFuture.addDoneListener(() -> {

                    try {

                        IdentifyLayerResult identifyLayerResult = identifyLayerResultFuture.get();

                        List<GeoElement> elements = identifyLayerResult.getElements();
                        if (elements.size() == 0) {

                            Log.i("LOGGER", "Няма лавинни данни!");
                        } else {

                            Log.i("LOGGER", "IN THE ELSE");
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
                });*/

                /*LocationRequest locationRequest = new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 1000)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(1000)
                        .setMaxUpdateDelayMillis(1000)
                        .build();

                Intent intent = new Intent(getApplicationContext(), LocationBroadcastReceiver2.class);
                intent.setAction(LocationBroadcastReceiver2.ACTION_PROCESS_UPDATE);
                startService(intent);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                getClient().requestLocationUpdates(locationRequest, pendingIntent);
                stopService(intent);*/
            };

            private void setTextAccordingToDangerLevel(int newAvalancheDanger) {

                switch (newAvalancheDanger) {

                    case 1:
                        Log.i("LOGGER", "На безопасно място");
                        break;

                    case 2:
                        Log.i("LOGGER", "Внимание");
                        break;

                    case 3:
                        Log.i("LOGGER","Голям риск!");
                        //vibrateAndSoundAlarm();
                        break;

                    default:
                        Log.i("LOGGER","BREAK");
                        break;
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
        };
    }

   /* private FusedLocationProviderClient getClient() {

        mClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        return mClient;
    }

    private LocationDisplay getLocationDisplay() {

        return locationDisplay;
    }*/

    /*private PendingIntent getPendingIntent() {

        Intent intent = new Intent(this, LocationBroadcastReceiver2.class);
        intent.setAction(LocationBroadcastReceiver2.ACTION_PROCESS_UPDATE);
        startService(intent);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }*/

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}