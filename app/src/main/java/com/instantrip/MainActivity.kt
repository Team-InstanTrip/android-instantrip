package com.instantrip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instantrip.databinding.ActivityMainBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MainActivity : AppCompatActivity() {
    private var mapView : MapView? = null
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this)
        binding.map.addView(mapView)
        mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }
}