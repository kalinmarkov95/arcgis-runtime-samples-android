/* Copyright 2016 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.arcgisruntime.sample.displaymap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MainActivity extends AppCompatActivity {

  private MapView mMapView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);

    displayBaseMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD, 41.76122, 23.44046, 10000);

    String echmishteSlopeURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishte_slope_with_simplified_polygons/FeatureServer/1";
    String echmishteBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/echmishteboundary/FeatureServer/0";
    String pirinNationalParkBoundaryURL = "https://services9.arcgis.com/ALBafD9UofIP26pj/arcgis/rest/services/pirinnationalparkboundary/FeatureServer/0";

    displayFeatureLayer(echmishteSlopeURL);
    displayFeatureLayer(echmishteBoundaryURL);
    displayFeatureLayer(pirinNationalParkBoundaryURL);
  }

  private void displayBaseMap(BasemapStyle basemapStyle, double initialLat, double initialLong, int scale) {

    mMapView = findViewById(R.id.mapView);
    ArcGISMap map = new ArcGISMap(basemapStyle);
    mMapView.setMap(map);
    mMapView.setViewpoint(new Viewpoint(initialLat, initialLong, scale));
  }

  private void displayFeatureLayer(String featureLayerURL) {

    ServiceFeatureTable testFeatureTable = new ServiceFeatureTable(featureLayerURL);
    mMapView.getMap().getOperationalLayers().add(new FeatureLayer(testFeatureTable));
  }

  @Override
  protected void onPause() {

    mMapView.pause();
    super.onPause();
  }

  @Override
  protected void onResume() {

    super.onResume();
    mMapView.resume();
  }

  @Override
  protected void onDestroy() {

    mMapView.dispose();
    super.onDestroy();
  }
}
