package com.esri.arcgisruntime.sample.displaymap.FeatureLayer;

import android.content.Context;
import android.util.Log;
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
import com.esri.arcgisruntime.sample.displaymap.R;
import com.esri.arcgisruntime.sample.displaymap.legend.LegendController;
import com.esri.arcgisruntime.toolkit.compass.Compass;
import com.esri.arcgisruntime.toolkit.scalebar.Scalebar;

import java.util.Map;

public class FeatureLayerHandler {

    private MapView mapView;

    private SceneView sceneView;

    private GeoViewDisplayStatus geoViewDisplayStatus;

    /*private FeatureLayer avalancheLayerEchmishte;

    private FeatureLayer avalancheLayerPirin;

    private FeatureLayer echmishteSlope;

    private FeatureLayer slopeLayer1;

    private FeatureLayer slopeLayer2;

    private FeatureLayer slopeLayer3;

    private FeatureLayer slopeLayer4;

    private FeatureLayer slopeLayer5;*/

    private FeatureLayer todorkaATES;

    private LegendController legendControllerView;

    private LegendController legendControllerSceneView;

    private float opacity;

    public FeatureLayerHandler(
            Context context,
            MapView mapView,
            SceneView sceneView,
            TextView legendButton) {

        this.mapView = mapView;
        this.sceneView = sceneView;
        this.geoViewDisplayStatus = new GeoViewDisplayStatus(mapView);
                //com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.SLOPE);

      /*  this.avalancheLayerEchmishte = createFeatureLayer(
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
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/dissolved5/FeatureServer/0");*/

        /*this.todorkaATES = createFeatureLayer(
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/TodorkaATES/FeatureServer/0");*/

        this.todorkaATES = createFeatureLayer("" +
                "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/ATES/FeatureServer/0");

        this.legendControllerView = new LegendController(context,this, legendButton);
    }

    public GeoViewDisplayStatus getFeatureLayerDisplayStatus() {

        return geoViewDisplayStatus;
    }

   /* public FeatureLayer getAvalancheLayerEchmishte() {

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
    }*/

    public FeatureLayer getTodorkaATES() {

        return todorkaATES;
    }

    public void setOpacity(float opacity) {

        this.opacity = opacity;
    }

    public void displayChangeFeatureLayerIcon(
            ImageView changeFeatureLayerIcon, Compass compass, Scalebar scalebar) {

        //displaySlope();

        //legendController.displaySlopeLegendButton();

        legendControllerView.displayTodorkaATESLegendButton(mapView);
        displayFeatureLayer(todorkaATES, mapView);

        changeFeatureLayerIcon.setOnClickListener(view -> {

            GeoView currentGeoView = geoViewDisplayStatus.getGeoView();
            boolean isMapView = currentGeoView instanceof MapView;

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

               /*Camera camera = new Camera(
                        mapView.getLocationDisplay().getMapLocation(),
                        0.0,
                        80.0,
                        0.0);
                sceneView.setViewpointCamera(camera);*/

                //legendControllerView.displayTodorkaATESLegendButton(sceneView);

            } else {

                removeFeatureLayer(todorkaATES, sceneView);
                legendControllerView.removeTodorkaATESLegend(sceneView);

                mapView.setVisibility(View.VISIBLE);
                sceneView.setVisibility(View.INVISIBLE);


                displayFeatureLayer(todorkaATES, mapView);
                legendControllerView.displayTodorkaATESLegendButton(mapView);
                //legendController.removeSlopeLegend();
                // legendController.displayAvalancheRiskLegendButton()

                // featureLayerDisplayStatus.setCurrentFeatureLayer(
                //        com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.AVALANCHE_RISK);
                geoViewDisplayStatus.setGeoView(mapView);
                compass.bindTo(mapView);
                scalebar.setVisibility(View.VISIBLE);
            }

           /* switch(isMapView) {

                case "":

                    //removeSlope();

                    // displayFeatureLayer(avalancheLayerEchmishte);
                    // displayFeatureLayer(avalancheLayerPirin);

                           // com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.TODORKA_ATES);

                    break;

                case "kkk":

                    //removeFeatureLayer(avalancheLayerEchmishte);
                    //removeFeatureLayer(avalancheLayerPirin);
                    //removeFeatureLayer(todorkaATES);
                    //displaySlope();

                    //legendController.removeAvalancheRiskLegend();
                    //legendController.removeTodorkaATESLegend();
                    //legendController.displaySlopeLegendButton();

                    //featureLayerDisplayStatus.setCurrentFeatureLayer(
                    //        com.esri.arcgisruntime.sample.displaymap.FeatureLayer.FeatureLayer.SLOPE);


                    break;
            }*/
        });
    }

   /* private void displaySlope() {

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
    }*/

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
