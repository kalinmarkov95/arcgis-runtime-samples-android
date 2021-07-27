package com.esri.arcgisruntime.sample.displaymap.legend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.LegendInfo;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.Symbol;

import java.util.ArrayList;
import java.util.List;

public class LegendController {

    private Context context;

    public LegendController(Context context) {

        this.context = context;
    }

    public void createLegendButton(MapView mapView, FeatureLayer featureLayer, String buttonText) {

        TextView textView = new TextView(context);
        textView.setText(buttonText);
        textView.setTextSize(15);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.BLACK);

        Legend legend = createLegendForLayer("Наклон в\nградуси", featureLayer);

        textView.setOnClickListener(view -> {

            if(legend.isShown()) {

                mapView.removeView(legend);
            } else {

                mapView.addView(legend);
            }
        });
        mapView.addView(textView);
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
