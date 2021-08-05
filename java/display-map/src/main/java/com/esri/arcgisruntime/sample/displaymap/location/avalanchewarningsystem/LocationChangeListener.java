package com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem;

import android.content.Context;
import android.graphics.Point;
import android.widget.TextView;

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

    public LocationChangeListener(MapView mapView, LocationDisplay locationDisplay, Context context) {

        this.mapView = mapView;
        this.locationDisplay = locationDisplay;
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
}
