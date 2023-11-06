package com.instantrip.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.instantrip.R
import com.instantrip.databinding.ActivityMapBinding
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnCameraMoveEndListener
import com.kakao.vectormap.KakaoMap.OnCameraMoveStartListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import timber.log.Timber


class MapActivity: AppCompatActivity(), OnCameraMoveEndListener, OnCameraMoveStartListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var kakaoMap: KakaoMap
    private lateinit var centerPointLabel: Label
    private val DEFAULT_LATLNG: LatLng = LatLng.from(37.406960, 127.115587)
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Timber.tag("location_test").i("Precise location access granted.")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Timber.tag("location_test").i("Only approximate location access granted.")
            } else -> {
                Timber.tag("location_test").e("권한요청 실패")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.start(object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                kakaoMap.setOnCameraMoveStartListener(this@MapActivity)
                kakaoMap.setOnCameraMoveEndListener(this@MapActivity)
                centerPointLabel = kakaoMap.labelManager!!.layer!!
                    .addLabel(
                        LabelOptions.from(kakaoMap.cameraPosition!!.position)
                            .setStyles(R.drawable.red_dot_marker)
                    )
            }
        })

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        var resultLatLng: LatLng = DEFAULT_LATLNG
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            resultLatLng = location?.let {
                LatLng.from(location.latitude, location.longitude)
            } ?: DEFAULT_LATLNG
            Timber.tag("location_test").d("location = ${resultLatLng.latitude}, ${resultLatLng.longitude}")

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                initializeMap(resultLatLng)
            } else {
                Timber.tag("location_test").e("check permission 실패")

            }
        }

        fusedLocationClient.lastLocation.addOnFailureListener {
            Timber.tag("location_test").e("fail listener")
            initializeMap(resultLatLng)
        }
    }

    private fun initializeMap(latLng: LatLng) {
        Timber.tag("location_test").d("initializeMap = ${latLng.latitude}, ${latLng.longitude}")
        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
                Timber.d("맵 정상종료")
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                Timber.e("인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨")
                Timber.e(error.message)
            }

        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                Timber.d("인증 후 API 가 정상적으로 실행될 때 호출됨")
            }

            override fun getPosition(): LatLng {
                val lat = latLng.latitude
                val lng = latLng.longitude
                return LatLng.from(lat, lng);
            }

            override fun getZoomLevel(): Int {
                return 15
            }
        })
    }

    override fun onCameraMoveEnd(
        kakaoMap: KakaoMap,
        cameraPosition: CameraPosition,
        gestureType: GestureType,
    ) {
        centerPointLabel.moveTo(cameraPosition.position)
    }

    override fun onCameraMoveStart(kakaoMap: KakaoMap, gestureType: GestureType) {
        Toast.makeText(this, "onCameraMoveStart", Toast.LENGTH_SHORT).show()
    }
}