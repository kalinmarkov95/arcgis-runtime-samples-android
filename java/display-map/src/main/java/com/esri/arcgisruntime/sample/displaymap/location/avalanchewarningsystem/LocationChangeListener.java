package com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem;

import android.graphics.Color;
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

    private TextView avalancheDangerLabel;

    public LocationChangeListener(MapView mapView, LocationDisplay locationDisplay, TextView avalancheDangerLabel) {

        this.mapView = mapView;
        this.locationDisplay = locationDisplay;
        this.avalancheDangerLabel = avalancheDangerLabel;
    }

    public void alertIfLocationInAvalancheTerrain(FeatureLayer avalancheLayer) {

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

                            setTextView("Няма лавинни данни!");
                            avalancheDangerLabel.setTextColor(Color.GREEN);
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

        switch (newAvalancheDanger.ordinal()) {

            case 0:
                setTextView("На безопасно място");
                avalancheDangerLabel.setTextColor(Color.GREEN);
                break;

            case 1:
                setTextView("На безопасно място");
                avalancheDangerLabel.setTextColor(Color.GREEN);
                break;

            case 2:
                setTextView("Внимание!");
                avalancheDangerLabel.setTextColor(Color.MAGENTA);
                break;

            case 3:
                setTextView("В опасност!");
                avalancheDangerLabel.setTextColor(Color.RED);
                break;

            case 4:
                setTextView("Огромен риск!");
                avalancheDangerLabel.setTextColor(Color.RED);
                break;

            default:
                break;
        }
    }

    public void setTextView(String text) {

        if (!avalancheDangerLabel.getText().equals(text)) {

            avalancheDangerLabel.setText(text);
        }
    }
}
