package com.esri.arcgisruntime.sample.displaymap.location;

import android.content.Context;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.MainActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AvalancheWarningWorker extends Worker {

    public AvalancheWarningWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i("LOGGER", "Calling the doWork method");

        Point screenPoint =
                MainActivity.mapView.locationToScreen(MainActivity.locationDisplay.getMapLocation());

        Log.i("LOGGER", Integer.toString(screenPoint.x));
        Log.i("LOGGER", Integer.toString(screenPoint.y));

        ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture =
                MainActivity.mapView.identifyLayerAsync(FeatureLayerHandler.todorkaATES, screenPoint, 0.01, false, -1);

        while(!identifyLayerResultFuture.isDone()) {

            System.out.println("NOT DONE!!!!!!!!!!!!!!!!!!!!!");
        }

       /* while (true) {

            Log.i("LOGGER", "INFINITE WHILE LOOP");
            System.out.println(identifyLayerResultFuture.isDone());

            try {

                Log.i("LOGGER", "killington");
                System.out.println(identifyLayerResultFuture.get());
                if (identifyLayerResultFuture.get() != null) {

                    System.out.println(identifyLayerResultFuture.isDone());

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

                    break;
                }
            } catch (ExecutionException | InterruptedException e) {

                // TODO - improve error handling
                Log.i("LOGGER", "ERROR OCCURRED");
                break;
            }
        }*/

        return Result.success();
    }

       /* identifyLayerResultFuture.addDoneListener(() -> {

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
        });
*/
        //return Result.success();
   // }

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

    /*private void vibrateAndSoundAlarm() {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {

            v.vibrate(500);
        }
    }*/
}
