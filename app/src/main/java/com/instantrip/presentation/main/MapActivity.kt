package com.instantrip.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.instantrip.R
import com.instantrip.databinding.ActivityMapBinding
import com.instantrip.databinding.LayoutMapMainBinding
import com.instantrip.presentation.login.LoginActivity
import com.instantrip.util.Constants.REQ_LOCATION_PERMISSION
import com.instantrip.util.PreferenceUtil
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnCameraMoveEndListener
import com.kakao.vectormap.KakaoMap.OnCameraMoveStartListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import timber.log.Timber

class MapActivity: AppCompatActivity(), OnCameraMoveEndListener, OnCameraMoveStartListener,
    View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapBinding: LayoutMapMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var kakaoMap: KakaoMap
    private lateinit var centerPointLabel: Label
    private val viewModel: MainViewModel by viewModels()
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val DEFAULT_LATLNG: LatLng = LatLng.from(37.406960, 127.115587)

    private lateinit var getGPSPermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var getRuntimePermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var getLoginAction: ActivityResultLauncher<Intent>
    private var isFirst: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        mapBinding = LayoutMapMainBinding.bind(binding.mapMain.root)
        setContentView(binding.root)

        init()
        checkAllPermissions()

        if (intent.hasExtra("isUserLogined")) {
            Timber.d("NicknameActivity 타고 들어올때")
            viewModel.setIsUserLogined(intent.getBooleanExtra("isUserLogined", false))
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    private fun checkAllPermissions() {
        // GPS 확인
        if (isLocationServicesAvailable()) {
            checkRuntimePermissions()
        } else {
            showDialogForLocationServiceSetting()
        }
    }

    private fun isLocationServicesAvailable(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져 있습니다. 현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
        builder.setPositiveButton("확인") { dialog, which ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        }
        builder.setNegativeButton("취소") { dialog, which ->
            dialog.cancel()
            Toast.makeText(this, "기기에서 위치서비스(GPS) 설정 후 사용해주세요.", Toast.LENGTH_LONG).show()
            finish()
        }
        builder.show()
    }

    private fun init() {
        // 로그인 체크
        PreferenceUtil.getBoolean("isLogined", false)?.let { viewModel.setIsUserLogined(it) }

        initNavigationMenu()
        setViewModels()
        // 메인 버튼
        mapBinding.btnLocation.setOnClickListener(this)
        mapBinding.btnMenu.setOnClickListener(this)
        mapBinding.btnSearch.setOnClickListener(this)
        mapBinding.btnLayer.setOnClickListener(this)
        mapBinding.btnFavorite.setOnClickListener(this)
        // 메인 플로팅버튼
        mapBinding.fabMain.setOnClickListener(this)
        mapBinding.fabAddPicture.setOnClickListener(this)
        mapBinding.fabAddVideo.setOnClickListener(this)
        mapBinding.fabAddMessage.setOnClickListener(this)

        // 권한 요청 런처
        getRuntimePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Timber.d("ActivityResultContracts resultcode = ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK
                || result.resultCode == Activity.RESULT_CANCELED) {
                checkRuntimePermissions()
            } else {
                Toast.makeText(this, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 사용자가 gps 활성화시켰는지 확인
                if (isLocationServicesAvailable()) {
                    checkRuntimePermissions()
                } else {
                    Toast.makeText(this, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        getLoginAction = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Timber.d("ActivityResultContracts resultcode = ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.setIsUserLogined(true)
            } else {
                Toast.makeText(this, "로그인실패", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun checkRuntimePermissions() {
        Timber.d("checkRuntimePermissions들어옴")
        val isFirstCheck = PreferenceUtil.getBoolean("isFirstPermissionCheck", true)
        Timber.tag("permission_check").e("isFirstCheck=$isFirstCheck")

        if ((ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED)) {
            // 권한이 없을 때
            Timber.tag("permission_check").e("권한없음")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                // 권한 거절 (다시 물어봄)
                Timber.tag("permission_check").e("권한 거절 (다시 물어봄)")
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, permissions, REQ_LOCATION_PERMISSION)
                }
                builder.setNegativeButton("취소") { dialog, which ->
                    finish()
                }
                builder.show()
            } else {
                if (isFirstCheck == true) {
                    // 최초 권한 요청일때
                    Timber.tag("permission_check").e("처음 요청하는 경우 그냥 권한 요청")
                    PreferenceUtil.setBoolean("isFirstPermissionCheck", false)
                    ActivityCompat.requestPermissions(this, permissions, REQ_LOCATION_PERMISSION)
                } else {
                    Timber.tag("permission_check").e("다시 묻지 않음 선택")
                    // 다시 묻지 않음 선택 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        getRuntimePermissionLauncher.launch(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있을 때
            startTracking()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 요청 후 승인됨
                    Toast.makeText(this, "권한허용", Toast.LENGTH_SHORT).show()
                    startTracking()
                } else {
                    // 권한 요청 후 거절됨 (다시 요청 OR 토스트)
                    Toast.makeText(this, "권한거절", Toast.LENGTH_SHORT).show()
                    checkRuntimePermissions()
                }
            }
        }
    }

    private fun startTracking() {
        getCurrentLocation()
    }

    private fun initializeMap(latLng: LatLng) {
        Timber.tag("location_test").d("initializeMap = ${latLng.latitude}, ${latLng.longitude}")

        if (isFirst) {
            isFirst = false
            mapBinding.mapView.start(object : MapLifeCycleCallback() {
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
                override fun onMapReady(map: KakaoMap) {
                    // 인증 후 API 가 정상적으로 실행될 때 호출됨
                    Timber.d("인증 후 API 가 정상적으로 실행될 때 호출됨")
                    kakaoMap = map
                    kakaoMap.setOnCameraMoveStartListener(this@MapActivity)
                    kakaoMap.setOnCameraMoveEndListener(this@MapActivity)
                    centerPointLabel = kakaoMap.labelManager!!.layer!!
                        .addLabel(
                            LabelOptions.from(kakaoMap.cameraPosition!!.position)
                                .setStyles(R.drawable.red_dot_marker)
                        )
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
        } else {
            Timber.tag("location_test").d("else로 빠져서 카메라 업데이트")
            val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
            kakaoMap.moveCamera(cameraUpdate)
        }
    }

    override fun onCameraMoveEnd(
        kakaoMap: KakaoMap,
        cameraPosition: CameraPosition,
        gestureType: GestureType,
    ) {
//        centerPointLabel.moveTo(cameraPosition.position)
    }

    override fun onCameraMoveStart(kakaoMap: KakaoMap, gestureType: GestureType) {
        Toast.makeText(this, "onCameraMoveStart", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        var resultLatLng: LatLng = DEFAULT_LATLNG
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            resultLatLng = location?.let {
                LatLng.from(location.latitude, location.longitude)
            } ?: DEFAULT_LATLNG
            Timber.tag("location_test").d("location = ${resultLatLng.latitude}, ${resultLatLng.longitude}")
            initializeMap(resultLatLng)
        }

        fusedLocationClient.lastLocation.addOnFailureListener {
            Timber.tag("location_test").e("fail listener")
            initializeMap(resultLatLng)
        }
    }

    override fun onClick(view: View) {
        when (view) {
            mapBinding.btnLocation -> {
                getCurrentLocation()
            }
            mapBinding.btnMenu -> {
                // Drawer
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            mapBinding.btnSearch -> {
                // 검색
                Toast.makeText(this@MapActivity, "검색?", Toast.LENGTH_LONG).show()
            }
            mapBinding.btnLayer -> {
                // 레이어
                Toast.makeText(this@MapActivity, "레이어?", Toast.LENGTH_LONG).show()
            }
            mapBinding.btnFavorite -> {
                // 즐겨찾기?
                Toast.makeText(this@MapActivity, "즐찾?", Toast.LENGTH_LONG).show()
            }
            mapBinding.fabMain -> {
                // 플로팅버튼
                viewModel.toggleVisibility()
            }
            mapBinding.fabAddPicture -> {
            }
            mapBinding.fabAddVideo -> {

                Toast.makeText(this@MapActivity, "비디오추가", Toast.LENGTH_LONG).show()
            }
            mapBinding.fabAddMessage -> {

                Toast.makeText(this@MapActivity, "텍스트추가", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setViewModels() {
        viewModel.isVisible.observe(this) {
            if (it) {
                mapBinding.fabAddPicture.visibility = View.VISIBLE
                mapBinding.fabAddVideo.visibility = View.VISIBLE
                mapBinding.fabAddMessage.visibility = View.VISIBLE
            } else {
                mapBinding.fabAddPicture.visibility = View.GONE
                mapBinding.fabAddVideo.visibility = View.GONE
                mapBinding.fabAddMessage.visibility = View.GONE
            }
        }

        val menu = binding.navigationDrawer.menu
        viewModel.isUserLogined.observe(this) {
            PreferenceUtil.setBoolean("isLogined", it)
            if (it) { // 로그인 상태
                menu[0].isVisible = false
                menu[1].isVisible = true
                menu[2].isVisible = true
                menu[3].isVisible = true
            } else { // 비로그인 상태
                menu[0].isVisible = true
                menu[1].isVisible = false
                menu[2].isVisible = false
                menu[3].isVisible = false
            }
        }

    }

    private fun initNavigationMenu() {
        val drawerLayout = binding.drawerLayout
        val navView = binding.navigationDrawer

        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val builder = AlertDialog.Builder(this@MapActivity)
            builder.setMessage("앱을 종료하시겠습니까?")
            builder.setPositiveButton("종료") { dialog, which ->
                Toast.makeText(this@MapActivity, "앱을 종료합니다.", Toast.LENGTH_LONG).show()
                finish()
            }
            builder.setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }
            builder.show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item0_login -> {
                getLoginAction.launch(Intent(this, LoginActivity::class.java))
            }
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