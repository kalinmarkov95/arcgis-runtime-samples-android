package com.esri.arcgisruntime.sample.displaymap.FeatureLayer;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.sample.displaymap.R;
import com.esri.arcgisruntime.sample.displaymap.legend.LegendController;

public class FeatureLayerHandler {

    private Context context;

    private MapView mapView;

    private FeatureLayerDisplayStatus featureLayerDisplayStatus;

    private FeatureLayer avalancheLayerEchmishte;

    private FeatureLayer avalancheLayerPirin;

    private FeatureLayer echmishteSlope;

    private FeatureLayer slopeLayer1;

    private FeatureLayer slopeLayer2;

    private FeatureLayer slopeLayer3;

    private FeatureLayer slopeLayer4;

    private FeatureLayer slopeLayer5;

    private LegendController legendController;

    private float opacity;

    public FeatureLayerHandler(Context context, MapView mapView, TextView legendButton) {

        this.context = context;
        this.mapView = mapView;
        this.featureLayerDisplayStatus = new FeatureLayerDisplayStatus(
                com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.SLOPE);

        this.avalancheLayerEchmishte = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope1/FeatureServer/0");
        this.avalancheLayerPirin = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/PirinAvalancheDanger/FeatureServer/0");

        this.echmishteSlope = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/EchmishteSlope/FeatureServer/0");

        this.slopeLayer1 = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope11/FeatureServer/0");
        this.slopeLayer2 = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope2/FeatureServer/0");
        this.slopeLayer3 = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope3/FeatureServer/0");
        this.slopeLayer4 = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/slope4/FeatureServer/0");
        this.slopeLayer5 = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/dissolved5/FeatureServer/0");

        this.legendController = new LegendController(context, mapView,this, legendButton);
    }

    public FeatureLayerDisplayStatus getFeatureLayerDisplayStatus() {

        return featureLayerDisplayStatus;
    }

    public FeatureLayer getAvalancheLayerEchmishte() {

        return avalancheLayerEchmishte;
    }

    public FeatureLayer getAvalancheLayerPirin() {

        return avalancheLayerPirin;
    }

    public FeatureLayer getEchmishteSlope() {

        return echmishteSlope;
    }

    public FeatureLayer getSlopeLayer1() {

        return slopeLayer1;
    }

    public FeatureLayer getSlopeLayer2() {

        return slopeLayer2;
    }

    public FeatureLayer getSlopeLayer3() {

        return slopeLayer3;
    }

    public FeatureLayer getSlopeLayer4() {

        return slopeLayer4;
    }

    public FeatureLayer getSlopeLayer5() {

        return slopeLayer5;
    }

    public void setOpacity(float opacity) {

        this.opacity = opacity;
    }

    public void displayChangeFeatureLayerIcon() {

        ImageView changeFeatureLayerIcon = new ImageView(context);
        changeFeatureLayerIcon.setImageResource(R.drawable.change_feature_layer);
        changeFeatureLayerIcon.setBackgroundColor(Color.WHITE);

        changeFeatureLayerIcon.setX(10);
        changeFeatureLayerIcon.setY(1100);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeFeatureLayerIcon.setLayoutParams(params);

        changeFeatureLayerIcon.getLayoutParams().height = 125;
        changeFeatureLayerIcon.getLayoutParams().width = 125;

        displaySlope();
        legendController.displaySlopeLegendButton();

        changeFeatureLayerIcon.setOnClickListener(view -> {

            com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer currentFeatureLayer = featureLayerDisplayStatus.getCurrentFeatureLayer();

            switch(currentFeatureLayer.ordinal()) {

                case 0:

                    removeSlope();
                    displayFeatureLayer(avalancheLayerEchmishte);
                    displayFeatureLayer(avalancheLayerPirin);

                    //legendController.removeSlopeLegendButton();
                    legendController.removeSlopeLegend();
                    legendController.displayAvalancheRiskLegendButton();

                    featureLayerDisplayStatus.setCurrentFeatureLayer(
                            com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.AVALANCHE_RISK);
                    break;
                case 1:

                    removeFeatureLayer(avalancheLayerEchmishte);
                    removeFeatureLayer(avalancheLayerPirin);
                    displaySlope();

                    //legendController.removeAvalancheRiskLegendButton();
                    legendController.removeAvalancheRiskLegend();
                    legendController.displaySlopeLegendButton();

                    featureLayerDisplayStatus.setCurrentFeatureLayer(
                            com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.SLOPE);
                    break;
            }
        });

        mapView.addView(changeFeatureLayerIcon);
    }

    private void displaySlope() {

        displayFeatureLayer(echmishteSlope);
        displayFeatureLayer(slopeLayer1);
        displayFeatureLayer(slopeLayer2);
        displayFeatureLayer(slopeLayer3);
        displayFeatureLayer(slopeLayer4);
        displayFeatureLayer(slopeLayer5);
    }

    private void removeSlope() {

        removeFeatureLayer(echmishteSlope);
        removeFeatureLayer(slopeLayer1);
        removeFeatureLayer(slopeLayer2);
        removeFeatureLayer(slopeLayer3);
        removeFeatureLayer(slopeLayer4);
        removeFeatureLayer(slopeLayer5);
    }

    public void displayBoundaryLayer(FeatureLayer boundaryLayer) {

        mapView.getMap().getOperationalLayers().add(boundaryLayer);
    }

    public void displayFeatureLayer(FeatureLayer featureLayer) {

        featureLayer.setOpacity(opacity);
        mapView.getMap().getOperationalLayers().add(featureLayer);
    }

    private void removeFeatureLayer(FeatureLayer featureLayer) {

        mapView.getMap().getOperationalLayers().remove(featureLayer);
    }

    public FeatureLayer createFeatureLayer(String featureLayerURL) {

        ServiceFeatureTable featureTable = new ServiceFeatureTable(featureLayerURL);
        return new FeatureLayer(featureTable);
    }
}
