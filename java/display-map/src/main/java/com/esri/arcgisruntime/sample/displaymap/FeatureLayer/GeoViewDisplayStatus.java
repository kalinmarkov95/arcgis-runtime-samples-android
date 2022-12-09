package com.esri.arcgisruntime.sample.displaymap.FeatureLayer;

import com.esri.arcgisruntime.mapping.view.GeoView;

public class GeoViewDisplayStatus {

    private GeoView geoView;

    public GeoViewDisplayStatus(GeoView geoView) {

        this.geoView = geoView;
    }

    public GeoView getGeoView() {

        return geoView;
    }

    public void setGeoView(GeoView geoView) {

        this.geoView = geoView;
    }
}
