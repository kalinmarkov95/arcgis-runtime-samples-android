package com.esri.arcgisruntime.sample.displaymap.Widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class Slider {

    private Context context;

    private MapView mapView;

    private FeatureLayerHandler featureLayerHandler;

    private SeekBar seekBar;

    public Slider(Context context, MapView mapView, FeatureLayerHandler featureLayerHandler) {

        this.context = context;
        this.mapView = mapView;
        this.featureLayerHandler = featureLayerHandler;
    }

    public void createSlider() {

        this.seekBar = new SeekBar(context);
        seekBar.setX(370);
        seekBar.setY(70);
        seekBar.setProgress(50);
        featureLayerHandler.setOpacity(new Float(0.5));

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        seekBar.setLayoutParams(params);

        seekBar.getLayoutParams().height = 75;
        seekBar.getLayoutParams().width = 400;
        seekBar.setBackgroundColor(Color.WHITE);

        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int opacity, boolean fromUser) {

                FeatureLayer currentFeatureLayer =
                        featureLayerHandler.getFeatureLayerDisplayStatus().getCurrentFeatureLayer();
                float transparency = new Float(opacity / 100.0);
                featureLayerHandler.setOpacity(transparency);

                switch(currentFeatureLayer.ordinal()) {

                    case 0:

                        featureLayerHandler.getSlopeLayer1().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer2().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer3().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer4().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer5().setOpacity(transparency);
                        break;

                    case 1:
                        featureLayerHandler.getAvalancheLayer().setOpacity(transparency);
                        break;
                }
            }
        });

        TextView textView = new TextView(context);
        textView.setText("Прозрачност");
        textView.setX(370);
        textView.setGravity(TextSymbol.HorizontalAlignment.CENTER.ordinal());
        textView.setLayoutParams(params);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);

        mapView.addView(textView);
        mapView.addView(seekBar);
    }
}