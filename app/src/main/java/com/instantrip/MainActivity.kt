package com.instantrip

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.instantrip.databinding.ActivityMainBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {
    private var mapView : MapView? = null
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this)
        binding.map.addView(mapView)
        mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

        initNavigationMenu()
        binding.btnMenu.setOnClickListener(this)

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

    private fun initNavigationMenu() {
        val drawerLayout = binding.mainDrawerLayout
        val navView = binding.navigationDrawer

        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
        val closeBtn = headerView.findViewById<Button>(R.id.btn_close)
        closeBtn.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_menu -> {
                binding.mainDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item1_my -> {
                Toast.makeText(this, "메뉴1", Toast.LENGTH_LONG).show()
            }
            R.id.menu_item2_like -> {
                Toast.makeText(this, "메뉴2", Toast.LENGTH_LONG).show()
            }
            R.id.menu_item3_setting -> {
                Toast.makeText(this, "메뉴3", Toast.LENGTH_LONG).show()
            }
        }
        return false
    }
}