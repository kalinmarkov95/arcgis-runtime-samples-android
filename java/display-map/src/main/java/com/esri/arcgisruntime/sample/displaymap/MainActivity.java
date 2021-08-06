package com.esri.arcgisruntime.sample.displaymap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.sample.displaymap.TransparencySlider.Slider;
import com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem.AvalancheWarningLevel;
import com.esri.arcgisruntime.sample.displaymap.location.LocationDisplayer;
import com.esri.arcgisruntime.toolkit.scalebar.Scalebar;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    private LocationDisplay locationDisplay;

    private FeatureLayerHandler featureLayerHandler;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);
        displayBaseMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD, 41.76122, 23.44046, 1000000);
        locationDisplay = mapView.getLocationDisplay();
        spinner = findViewById(R.id.spinner);

        String echmishteBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishte_boundary/FeatureServer/0";
        String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

        featureLayerHandler = new FeatureLayerHandler(this, mapView, locationDisplay);
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(echmishteBoundaryURL));
        featureLayerHandler.displayBoundaryLayer(
                featureLayerHandler.createFeatureLayer(pirinNationalParkBoundaryURL));

        Slider transparencySlider = new Slider(this, mapView, featureLayerHandler);
        transparencySlider.createSlider();

        featureLayerHandler.displayChangeFeatureLayerIcon();
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

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {

                if (locationDisplay.isStarted()) {

                    Point screenPoint = mapView.locationToScreen(locationDisplay.getMapLocation());
                    ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture = mapView
                            .identifyLayerAsync(featureLayerHandler.getAvalancheLayer(), screenPoint, 0.01, false, -1);

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
                                        AvalancheWarningLevel newAvalancheDanger = AvalancheWarningLevel.getRespectiveWarningLevel(
                                                (String) feature.getAttributes().get("AvalDanger"));

                                        setTextAccordingToDangerLevel(newAvalancheDanger, avalancheDangerLabel);
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

            private void setTextAccordingToDangerLevel(AvalancheWarningLevel newAvalancheDanger, TextView avalancheDangerLabel) {

                switch (newAvalancheDanger.ordinal()) {

                    case 0:
                        updateLabel("На безопасно място", Color.GREEN);
                        break;

                    case 1:
                        updateLabel("На безопасно място", Color.GREEN);
                        break;

                    case 2:
                        updateLabel("Внимание!", Color.MAGENTA);
                        vibrateAndSoundAlarm();
                        break;

                    case 3:
                        updateLabel("В опасност!", Color.RED);
                        vibrateAndSoundAlarm();
                        break;

                    case 4:
                        updateLabel("Огромен риск!", Color.RED);
                        vibrateAndSoundAlarm();
                        break;

                    default:
                        break;
                }
            }

            private void updateLabel(String text, int color) {

                if(!avalancheDangerLabel.getText().equals(text) || avalancheDangerLabel.getTextColors().getDefaultColor() == color) {

                    runOnUiThread(

                        () -> {

                            menu.removeItem(0);
                            avalancheDangerLabel.setTextColor(color);
                            avalancheDangerLabel.setText(text);
                            
                            if(text.equals("Внимание!") || text.equals("В опасност!") || text.equals("Огромен риск!")) {

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
        }, 0, 1000);

        new LocationDisplayer().displayGPSServices(locationDisplay, spinner, this);

        return true;
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
}