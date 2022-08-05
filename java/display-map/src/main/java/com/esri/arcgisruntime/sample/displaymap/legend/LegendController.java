package com.esri.arcgisruntime.sample.displaymap.legend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.LegendInfo;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.symbology.Symbol;

import java.util.ArrayList;
import java.util.List;

public class LegendController {

    private Context context;

    private MapView mapView;

    private TextView legendButton;

    private Legend slopeLegend;

    private Legend todorkaATESLegend;

    public LegendController(Context context, MapView mapView, FeatureLayerHandler featureLayerHandler, TextView legendButton) {

        this.context = context;
        this.mapView = mapView;
        this.slopeLegend = createLegendForLayer("Наклон в\nградуси", featureLayerHandler.getSlopeLayer1());
        this.todorkaATESLegend = createLegendForLayer("Тодорка\nATES", featureLayerHandler.getTodorkaATES());
        this.legendButton = legendButton;
    }

    private void setLegendButtonToSlopeState() {

        legendButton.setOnClickListener(view -> {

            if(slopeLegend.isShown()) {

                mapView.removeView(slopeLegend);
            } else {

                mapView.addView(slopeLegend);
            }
        });
    }

    private void setLegendButtonToATESState() {

        legendButton.setOnClickListener(view -> {

            if(todorkaATESLegend.isShown()) {

                mapView.removeView(todorkaATESLegend);
            } else {

                mapView.addView(todorkaATESLegend);
            }
        });
    }

    public void displaySlopeLegendButton() {

        setLegendButtonToSlopeState();
        legendButton.performClick();
    }

    public void displayTodorkaATESLegendButton() {

        setLegendButtonToATESState();
        legendButton.performClick();
    }

    public void removeSlopeLegend() {

        mapView.removeView(slopeLegend);
    }

    public void removeTodorkaATESLegend() {

        mapView.removeView(todorkaATESLegend);
    }

    private Legend createLegendForLayer(String legendTitle, FeatureLayer featureLayer) {

        ListenableFuture<List<LegendInfo>> legendInfoFuture = featureLayer.fetchLegendInfosAsync();
        List<LegendItem> legendItems = new ArrayList<>();

        try {

            List<LegendInfo> legendInfoList = legendInfoFuture.get();

            for(LegendInfo legendInfo : legendInfoList) {

                Symbol legendSymbol = legendInfo.getSymbol();
                ListenableFuture<Bitmap> symbolSwatch = legendSymbol.createSwatchAsync(context, Color.BLUE);
                legendItems.add(new LegendItem(legendInfo.getName(), symbolSwatch.get()));
            }

            return new Legend(context, legendTitle, legendItems, 0, 90);
        } catch (Exception e) {

            // TODO - handle this exception in some way
            return null;
        }
    }
}
