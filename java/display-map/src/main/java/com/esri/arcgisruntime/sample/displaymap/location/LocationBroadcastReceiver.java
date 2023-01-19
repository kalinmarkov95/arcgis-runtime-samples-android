package com.esri.arcgisruntime.sample.displaymap.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(LocationBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, LocationService.class));;
    }
}


/*
package com.esri.arcgisruntime.sample.displaymap.location;

import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.esri.arcgisruntime.sample.displaymap.MainActivity;
import com.google.android.gms.location.LocationResult;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    */
/*public static final String ACTION_PROCESS_UPDATE = "com.esri.arcgisruntime.sample.displaymap.location.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Restarting the location service by the broadcast receiver!");
        context.startService(new Intent(context, LocationService.class));;



    if (intent != null) {

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

                        //MainActivity.getInstance().updateTextView(locationString);

                    } catch (Exception e) {

                        Toast.makeText(context, locationString, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }*//*


    @Override
    public void onReceive(final Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            scheduleJob(context);
        } else {

            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {

        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context,
                JobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                // setOverrideDeadline runs it immediately - you must have at least one constraint
                // https://stackoverflow.com/questions/51064731/firing-jobservice-without-constraints
                .setOverrideDeadline(0)
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);
    }

}
*/
