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

    private TextView slopeLegendButton;

    private Legend slopeLegend;

    private TextView avalancheRiskLegendButton;

    private Legend avalancheRiskLegend;

    public LegendController(Context context, MapView mapView, FeatureLayerHandler featureLayerHandler) {

        this.context = context;
        this.mapView = mapView;
        this.slopeLegend = createLegendForLayer("Наклон в\nградуси", featureLayerHandler.getSlopeLayer1());
        this.avalancheRiskLegend = createLegendForLayer("Лавинна\nопасност", featureLayerHandler.getAvalancheLayerPirin());
        this.slopeLegendButton = createSlopeLegendButton();
        this.avalancheRiskLegendButton = createAvalancheRiskLegendButton();
    }

    private TextView createSlopeLegendButton() {

        TextView textView = createTextView("Покажи/скрий");
        textView.setOnClickListener(view -> {

            if(slopeLegend.isShown()) {

                mapView.removeView(slopeLegend);
            } else {

                mapView.addView(slopeLegend);
            }
        });

        return textView;
    }

    private TextView createAvalancheRiskLegendButton() {

        TextView textView = createTextView("Покажи/скрий");
        textView.setOnClickListener(view -> {

            if(avalancheRiskLegend.isShown()) {

                mapView.removeView(avalancheRiskLegend);
            } else {

                mapView.addView(avalancheRiskLegend);
            }
        });

        return textView;
    }

    private TextView createTextView(String buttonText) {

        TextView textView = new TextView(context);
        textView.setText(buttonText);
        textView.setTextSize(15);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.BLACK);
        textView.setX(10);
        return textView;
    }

    public void displaySlopeLegendButton() {

        mapView.addView(slopeLegendButton);
        slopeLegendButton.performClick();
    }

    public void displayAvalancheRiskLegendButton() {

        mapView.addView(avalancheRiskLegendButton);
        avalancheRiskLegendButton.performClick();
    }

    public void removeSlopeLegendButton() {

        mapView.removeView(slopeLegendButton);
    }

    public void removeAvalancheRiskLegendButton() {

        mapView.removeView(avalancheRiskLegendButton);
    }

    public void removeSlopeLegend() {

        mapView.removeView(slopeLegend);
    }

    public void removeAvalancheRiskLegend() {

        mapView.removeView(avalancheRiskLegend);
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

            return new Legend(context, legendTitle, legendItems, 10, 90);
        } catch (Exception e) {

            // TODO - handle this exception in some way
            return null;
        }
    }
}
