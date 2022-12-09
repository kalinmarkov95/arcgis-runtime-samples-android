package com.esri.arcgisruntime.sample.displaymap.legend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.LegendInfo;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayerHandler;
import com.esri.arcgisruntime.symbology.Symbol;

import java.util.ArrayList;
import java.util.List;

public class LegendController {

    private Context context;

    private TextView legendButton;

    private Legend todorkaATESLegend;

    public LegendController(Context context, FeatureLayerHandler featureLayerHandler, TextView legendButton) {

        this.context = context;
        this.todorkaATESLegend = createLegendForLayer("Тодорка\nATES", featureLayerHandler.getTodorkaATES());
        this.legendButton = legendButton;
    }

    private void setLegendButtonToATESState(GeoView geoView) {

        legendButton.setOnClickListener(view -> {

            if(todorkaATESLegend.isShown()) {

                geoView.removeView(todorkaATESLegend);
            } else {

                geoView.addView(todorkaATESLegend);
            }
        });
    }

    public void displayTodorkaATESLegendButton(GeoView geoView) {

        setLegendButtonToATESState(geoView);
        legendButton.performClick();
    }

    public void removeTodorkaATESLegend(GeoView geoView) {

        geoView.removeView(todorkaATESLegend);
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
