package com.esri.arcgisruntime.sample.displaymap.FeatureLayer;

public class FeatureLayerDisplayStatus {

    private FeatureLayer currentFeatureLayer;

    public FeatureLayerDisplayStatus(FeatureLayer currentFeatureLayer) {

        this.currentFeatureLayer = currentFeatureLayer;
    }

    public FeatureLayer getCurrentFeatureLayer() {

        return currentFeatureLayer;
    }

    public void setCurrentFeatureLayer(FeatureLayer currentFeatureLayer) {

        this.currentFeatureLayer = currentFeatureLayer;
    }
}
