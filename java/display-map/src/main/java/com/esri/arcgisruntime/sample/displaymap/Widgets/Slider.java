package com.esri.arcgisruntime.sample.displaymap.Widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;

public class Slider {

    private Context context;

    private GeoView geoView;

    private FeatureLayerHandler featureLayerHandler;

    private SeekBar seekBar;

    public Slider(Context context, GeoView geoView, FeatureLayerHandler featureLayerHandler) {

        this.context = context;
        this.geoView = geoView;
        this.featureLayerHandler = featureLayerHandler;
    }

    public void createSlider(int screenWidth, int screenHeight) {

        this.seekBar = new SeekBar(context);
        seekBar.setX((float)(screenWidth/3.0));
        seekBar.setProgress(50);
        featureLayerHandler.setOpacity(new Float(0.5));

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        seekBar.setLayoutParams(params);

        seekBar.getLayoutParams().height = (int) (screenHeight / 35.0);
        seekBar.getLayoutParams().width = (int) (screenWidth / 4.0);
        seekBar.setBackgroundColor(Color.WHITE);

        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int opacity, boolean fromUser) {

               /* FeatureLayer currentFeatureLayer =
                        featureLayerHandler.getFeatureLayerDisplayStatus().getGeoView();*/
                float transparency = new Float(1 - (opacity / 100.0));
                featureLayerHandler.setOpacity(transparency);
                featureLayerHandler.getTodorkaATES().setOpacity(transparency);

                /*switch(currentFeatureLayer.ordinal()) {

                    case 0:

                        *//*featureLayerHandler.getEchmishteSlope().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer1().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer2().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer3().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer4().setOpacity(transparency);
                        featureLayerHandler.getSlopeLayer5().setOpacity(transparency);*//*
                        featureLayerHandler.getTodorkaATES().setOpacity(transparency);
                        break;

                    case 1:

                        featureLayerHandler.getTodorkaATES().setOpacity(transparency);
                        break;
                }*/
            }
        });

        /*TextView textView = new TextView(context);
        textView.setText("Прозрачност");
        textView.setX((float)(screenWidth/3.0));
        textView.setGravity(TextSymbol.HorizontalAlignment.CENTER.ordinal());
        textView.setLayoutParams(params);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);

        mapView.addView(textView);*/
        geoView.addView(seekBar);
    }
}