package com.esri.arcgisruntime.sample.vectortiledlayercustomstyle

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.data.VectorTileCache
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.ItemResourceCache
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesTask
import com.esri.arcgisruntime.sample.vectortiledlayercustomstyle.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

//[DocRef: END]

class MainActivity : AppCompatActivity() {

    // A list of portal item IDs for the online layers.
    private var onlineItemIds: Array<String> = arrayOf(
        "1349bfa0ed08485d8a92c442a3850b06",
        "bd8ac41667014d98b933e97713ba8377",
        "02f85ec376084c508b9c8e5a311724fa",
        "1bf0cc4a4380468fbbff107e100f65a5"
    )

    // A list of portal item IDs for the layers which custom style is applied from local resources.
    private var offlineItemIds: Array<String> = arrayOf(
        // A vector tiled layer created by the local VTPK and light custom style.
        "e01262ef2a4f4d91897d9bbd3a9b1075",
        // A vector tiled layer created by the local VTPK and dark custom style.
        "ce8a34e5d4ca4fa193a097511daa8855"
    )

    // The item ID of the currently showing layer.
    private var currentItemID: String = onlineItemIds[0]

    // A dictionary to cache loaded vector tiled layers.
    private var vectorTiledLayersMap: MutableMap<String, ArcGISVectorTiledLayer> = mutableMapOf()


    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        // Authentication with an API key or named user is required to access basemaps and other location services
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        // Sets up the spinner to change the selected Vector Styled Layer
        setUpSpinner()

        // Set the currentItemID to default layer.
        currentItemID = onlineItemIds[0]
    }

    /**
     * Displays the layer of the given itemID.
     */
    private fun showSelectedItem(itemID: String) {
        currentItemID = itemID
        val vectorTiledLayer: ArcGISVectorTiledLayer

        // Checks if the layer has been cached.
        when {
            vectorTiledLayersMap.contains(itemID) -> {
                vectorTiledLayer = vectorTiledLayersMap.getValue(itemID)
            }
            onlineItemIds.contains(itemID) -> {
                // Retrieve the layer from online
                val portalItem = PortalItem(Portal("https://www.arcgis.com"), itemID)
                vectorTiledLayer = ArcGISVectorTiledLayer(portalItem)
                // Adds the retrieved layer to the mutable map for cache
                vectorTiledLayersMap[itemID] = vectorTiledLayer
            }
            else -> {
                // Load the layer using offline Vector Tiles
                checkOfflineItemCache(itemID)
                return
            }
        }

        // OnlineItemIDs uses WebMercator as a spatial ref
        val viewpoint = Viewpoint(
            Point(1990591.559979, 794036.007991, SpatialReferences.getWebMercator()),
            100000000.0
        )
        setMap(vectorTiledLayer, viewpoint)
    }

    /**
     *  Checks local cache for local cache files.
     *  If not it calls loadLayerWithOfflineCustomStyle() to retrieve the cache files.
     */
    private fun checkOfflineItemCache(itemID: String) {

        val portalItem = PortalItem(Portal("https://www.arcgis.com"), itemID)
        val itemResourceCache =
            ItemResourceCache(getExternalFilesDir(null)?.path + "/" + portalItem.itemId)
        itemResourceCache.addDoneLoadingListener {
            if (itemResourceCache.loadStatus == LoadStatus.LOADED) {
                setResourceAndVectorTileCache(itemResourceCache)
            } else {
                loadLayerWithOfflineCustomStyle(itemID)
            }
        }
        itemResourceCache.loadAsync()
    }

    /**
     * Retrieves the style resource files and caches it to the local device.
     */
    private fun loadLayerWithOfflineCustomStyle(itemID: String) {

        // Retrieve the layer from online
        val portalItem = PortalItem(Portal("https://www.arcgis.com"), itemID)
        val task = ExportVectorTilesTask(portalItem)

        val exportVectorTilesJob =
            task.exportStyleResourceCache(getExternalFilesDir(null)?.path + "/" + portalItem.itemId)
        exportVectorTilesJob.addJobDoneListener {
            if (exportVectorTilesJob.status == Job.Status.SUCCEEDED) {
                setResourceAndVectorTileCache(exportVectorTilesJob.result.itemResourceCache)
            } else {
                Toast.makeText(
                    this,
                    "Error reading cache: " + exportVectorTilesJob.error.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        exportVectorTilesJob.start()

    }

    private fun setResourceAndVectorTileCache(itemResourceCache: ItemResourceCache) {
        //Loads the vector tile layer cache.
        val vectorTileCache = VectorTileCache(getExternalFilesDir(null)?.path + "/dodge_city.vtpk")
        vectorTileCache.loadAsync()
        vectorTileCache.addDoneLoadingListener {
            if (vectorTileCache.loadError != null)
                Log.e("VectorTileCache: ", vectorTileCache.loadError.message.toString())
            else {
                // Loads the layer based on the vector tile cache and the style resource.
                val layer = ArcGISVectorTiledLayer(vectorTileCache, itemResourceCache)
                layer.addDoneLoadingListener {
                    if (layer.loadError != null)
                        Log.e("VectorTiledLayer: ", layer.loadError.toString())
                    else
                        Log.d("VectorTiledLayer:", "Loaded successfully")
                }

                // OfflineItemIDs uses WGS-84 as a spatial ref
                val viewpoint =
                    Viewpoint(Point(-100.01766, 37.76528, SpatialReferences.getWgs84()), 100000.0)
                setMap(layer, viewpoint)
            }
        }
    }

    /**
     * Set the map using the layer and the viewpoint.
     */
    private fun setMap(layer: ArcGISVectorTiledLayer, viewpoint: Viewpoint) {
        //Reset the map to release resources
        mapView.map = null
        //mapView.map.basemap = null

        // Assign a new map created from the base layer.
        val basemap = Basemap(layer.copy())
        val map = ArcGISMap(basemap)
        mapView.map = map
        //Set viewpoint without animation.
        mapView.setViewpoint(viewpoint)
    }

    /**
     * Sets the adapter and listens for a item selection to update the
     * MapView with the selected layer.
     */
    private fun setUpSpinner() {
        val customDropDownAdapter = CustomDropDownAdapter(this)
        spinner.adapter = customDropDownAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                // Sets the selected itemID to either the Online/Custom ID.
                currentItemID = when (position) {
                    4 -> {
                        // Custom style 1 - Dodge City OSM - Light
                        offlineItemIds[0]
                    }
                    5 -> {
                        // Custom style 2 - Dodge City OSM - Dark
                        offlineItemIds[1]
                    }
                    else -> {
                        // Else use the online vector styles
                        onlineItemIds[position]
                    }
                }
                showSelectedItem(currentItemID)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Keeps the current selected vector style.
            }
        }
    }

    //[DocRef: Name=Pause and resume-Android, Category=Get started, Topic=Develop your first map app with Kotlin]
    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

    //[DocRef: END]

    class CustomDropDownAdapter(private val context: Context) : BaseAdapter() {

        // The names of the each Vector Tiled Layer
        private val styleNames = arrayOf(
            "Default",
            "Style 1",
            "Style 2",
            "Style 3",
            "Offline custom style: Light",
            "Offline custom style: Dark"
        )

        // The drawable XML file names for the associated style name
        private val styleDrawableNames = arrayOf(
            "default_color",
            "style1_color",
            "style2_color",
            "style3_color",
            "custom1_color",
            "custom2_color"
        )

        // Inflates each row of the adapter.
        private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val vh: ItemHolder
            if (convertView == null) {
                view = inflater.inflate(R.layout.spinner_item, parent, false)
                vh = ItemHolder(view)
                view?.tag = vh
            } else {
                view = convertView
                vh = view.tag as ItemHolder
            }
            // Sets the TextView to the style name.
            vh.layerText.text = styleNames[position]

            // Gets the drawable style associated with the position.
            val id = context.resources.getIdentifier(
                styleDrawableNames[position],
                "drawable",
                context.packageName
            )
            // Sets the retrieved drawable as the background of the colorView.
            vh.colorView.setBackgroundResource(id)

            return view
        }

        override fun getItem(position: Int): Any {
            return styleNames[position]
        }

        override fun getCount(): Int {
            return styleNames.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        private class ItemHolder(row: View?) {
            val layerText: TextView = row?.findViewById(R.id.text) as TextView
            val colorView: View = row?.findViewById(R.id.color_view) as View
        }

    }
}
