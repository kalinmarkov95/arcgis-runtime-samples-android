package com.esri.arcgisruntime.sample.displaymap.FeatureLayer;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.sample.displaymap.legend.LegendController;
import com.esri.arcgisruntime.toolkit.compass.Compass;
import com.esri.arcgisruntime.toolkit.scalebar.Scalebar;

public class FeatureLayerHandler {

    private MapView mapView;

    private SceneView sceneView;

    private GeoViewDisplayStatus geoViewDisplayStatus;

    public static FeatureLayer todorkaATES;

    private LegendController legendControllerView;

    private float opacity;

    public FeatureLayerHandler(
            Context context,
            MapView mapView,
            SceneView sceneView,
            TextView legendButton) {

        this.mapView = mapView;
        this.sceneView = sceneView;
        this.geoViewDisplayStatus = new GeoViewDisplayStatus(mapView);
        this.todorkaATES = createFeatureLayer("https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/ATES/FeatureServer/0");
        this.legendControllerView = new LegendController(context,this, legendButton);
    }

    public FeatureLayer getTodorkaATES() {

        return todorkaATES;
    }

    public void setOpacity(float opacity) {

        this.opacity = opacity;
    }

    public void displayChangeFeatureLayerIcon(
            ImageView changeFeatureLayerIcon, Compass compass, Scalebar scalebar) {

        legendControllerView.displayTodorkaATESLegendButton(mapView);
        displayFeatureLayer(todorkaATES, mapView);

        changeFeatureLayerIcon.setOnClickListener(view -> {

            GeoView currentGeoView = geoViewDisplayStatus.getGeoView();

            if (currentGeoView instanceof MapView) {

                removeFeatureLayer(todorkaATES, mapView);
                legendControllerView.removeTodorkaATESLegend(mapView);

                mapView.setVisibility(View.INVISIBLE);
                sceneView.setVisibility(View.VISIBLE);

                displayFeatureLayer(todorkaATES, sceneView);
                legendControllerView.displayTodorkaATESLegendButton(sceneView);
                geoViewDisplayStatus.setGeoView(sceneView);
                compass.bindTo(sceneView);
                scalebar.setVisibility(View.INVISIBLE);

                Point currentCenterOfMap =  mapView.getVisibleArea().getExtent().getCenter();
                Point currentCenterOfMapProjected =
                        (Point) GeometryEngine.project(currentCenterOfMap, SpatialReferences.getWgs84());

                Camera camera = new Camera(
                        currentCenterOfMapProjected.getY(),
                        currentCenterOfMapProjected.getX(),
                        3000.0,
                        0.0,
                        90.0,
                        0.0);

                sceneView.setViewpointCamera(camera);

            } else {

                removeFeatureLayer(todorkaATES, sceneView);
                legendControllerView.removeTodorkaATESLegend(sceneView);

                mapView.setVisibility(View.VISIBLE);
                sceneView.setVisibility(View.INVISIBLE);


                displayFeatureLayer(todorkaATES, mapView);
                legendControllerView.displayTodorkaATESLegendButton(mapView);
                geoViewDisplayStatus.setGeoView(mapView);
                compass.bindTo(mapView);
                scalebar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void displayBoundaryLayer(FeatureLayer boundaryLayer, GeoView viewToAddTo) {

       addFeatureLayer(boundaryLayer, viewToAddTo);
    }

    public void displayFeatureLayer(FeatureLayer featureLayer, GeoView viewToAddTo) {

        featureLayer.setOpacity(opacity);
        addFeatureLayer(featureLayer, viewToAddTo);
    }

    private void removeFeatureLayer(FeatureLayer featureLayer,  GeoView viewToAddTo) {

        if (viewToAddTo instanceof MapView) {

            MapView mapView = (MapView) viewToAddTo;
            mapView.getMap().getOperationalLayers().remove(featureLayer);

        } else {

            SceneView mapView = (SceneView) viewToAddTo;
            mapView.getScene().getOperationalLayers().remove(featureLayer);
        }
    }

    public FeatureLayer createFeatureLayer(String featureLayerURL) {

        ServiceFeatureTable featureTable = new ServiceFeatureTable(featureLayerURL);
        return new FeatureLayer(featureTable);
    }

    private void addFeatureLayer(FeatureLayer featureLayer, GeoView viewToAddTo) {

        if (viewToAddTo instanceof MapView) {

            MapView mapView = (MapView) viewToAddTo;
            mapView.getMap().getOperationalLayers().add(featureLayer);

        } else {

            SceneView sceneView = (SceneView) viewToAddTo;
            sceneView.getScene().getOperationalLayers().add(featureLayer);
        }
    }
}
