package com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;

public class LocationChangeListener {

    private MapView mapView;

    private LocationDisplay locationDisplay;

    private Context context;

    private AvalancheWarningLevel currentAvalancheDanger;

    public LocationChangeListener(MapView mapView, LocationDisplay locationDisplay, Context context) {

        this.mapView = mapView;
        this.locationDisplay = locationDisplay;
        this.context = context;
        this.currentAvalancheDanger = AvalancheWarningLevel.LOW;
    }

    public void alertIfLocationInAvalancheTerrain(FeatureLayer avalancheLayer, TextView avalancheDangerLabel) {

        locationDisplay.addLocationChangedListener(locationChangedEvent -> {

            if (locationDisplay.isStarted()) {

                Point screenPoint = mapView.locationToScreen(locationDisplay.getMapLocation());
                ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture = mapView
                        .identifyLayerAsync(avalancheLayer, screenPoint, 10, false, -1);

                identifyLayerResultFuture.addDoneListener(() -> {

                    try {

                        IdentifyLayerResult identifyLayerResult = identifyLayerResultFuture.get();

                        List<GeoElement> elements = identifyLayerResult.getElements();
                        if(elements.size() == 0) {

                            String text = "Няма лавинни данни!";
                            if (!avalancheDangerLabel.getText().equals(text)) {
                                avalancheDangerLabel.setText(text);
                            }
                        } else {

                            for (GeoElement element : elements) {

                                if (element instanceof Feature) {

                                    Feature feature = (Feature) element;
                                    AvalancheWarningLevel newAvalancheDanger = AvalancheWarningLevel.getRespectiveWarningLevel(
                                            (String) feature.getAttributes().get("AvalDanger"));

                                    setTextAccordingToDangerLevel(newAvalancheDanger, avalancheDangerLabel);
                                }
                            }
                        }
                    } catch (Exception e) {

                        // TODO - improve error handling
                    }
                });
            }
        });
    }
    private void setTextAccordingToDangerLevel(AvalancheWarningLevel newAvalancheDanger, TextView avalancheDangerLabel) {

        String text;

        switch (newAvalancheDanger.ordinal()) {

            case 0:
                text = "На безопасно място";
                if (!avalancheDangerLabel.getText().equals(text)) {

                    avalancheDangerLabel.setText(text);
                }
                break;

            case 1:
                text = "На безопасно място";
                if (!avalancheDangerLabel.getText().equals(text)) {

                    avalancheDangerLabel.setText(text);
                }
                break;

            case 2:
                text = "Внимание!";
                if (!avalancheDangerLabel.getText().equals(text)) {

                    avalancheDangerLabel.setText(text);
                }
                break;

            case 3:
                text = "В опасност!";
                if (!avalancheDangerLabel.getText().equals(text)) {

                    avalancheDangerLabel.setText(text);
                }
                break;

            case 4:
                text = "Огромен риск!";
                if (!avalancheDangerLabel.getText().equals(text)) {
                    avalancheDangerLabel.setText(text);
                }
                break;

            default:
                break;
        }
    }

    /*private void triggerVibrate(int timeToVibrate) {

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            v.vibrate(VibrationEffect.createOneShot(timeToVibrate, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {

            v.vibrate(timeToVibrate);
        }
    }*/
}
