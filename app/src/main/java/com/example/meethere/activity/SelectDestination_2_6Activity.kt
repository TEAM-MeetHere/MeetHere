package com.example.meethere.activity

import KakaoAPI
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meethere.R
import com.example.meethere.ResultSearchKeyword
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_select_destination26.*
import noman.googleplaces.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*

class SelectDestination_2_6Activity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback,
    PlacesListener {
    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    var needRequest = false
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var mCurrentLocation: Location? = null
    var currentPosition: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null
    private var mLayout: View? = null
    var previous_marker: MutableList<Marker>? = null
    val geocoder = Geocoder(this)

    var averageLat: Double = 0.0
    var averageLon: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_select_destination26)
        mLayout = findViewById(R.id.layout_select_destination26)
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(SelectDestination_2_6Activity.UPDATE_INTERVAL_MS.toLong())
            .setFastestInterval(SelectDestination_2_6Activity.FATEST_UPDATE_INTERVAL_MS.toLong())
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapDestination) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@SelectDestination_2_6Activity)
        previous_marker = ArrayList()

        val addresses: Array<com.example.meethere.Address> =
            intent.getSerializableExtra("addressData") as Array<com.example.meethere.Address>
        val lats = DoubleArray(addresses.size)
        val lons = DoubleArray(addresses.size)

        for (i in addresses.indices) {
            val list = geocoder.getFromLocationName(addresses[i].address, 10)
            if (list != null) {
                val address: android.location.Address? = list.get(0)
                lats[i] = address?.latitude!!.toDouble()
                lons[i] = address?.longitude!!.toDouble()
                Log.d("좌표 : ", lats[i].toString())
                Log.d("좌표 : ", lons[i].toString())
            }
        }

        averageLat = getMean(lats)
        averageLon = getMean(lons)

        Log.d("좌표 : ", averageLat.toString())
        Log.d("좌표 : ", averageLon.toString())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기 전에 지도의 초기위치 서울로
        setDefaultLocation()

        //런타임 퍼미션 처리
        //1. 위치 퍼미션을 가지고 있는지 체크
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            startLocationUpdates() // 3. 위치 업데이트 시작.
        } else {
            // 2. 퍼미션 요청을 허용한 적이 없다면, 퍼미션 요청 발생. 2가지 경우가 존재

            //3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자에게 퍼미션이 필요한 이유를 설명해줄 필요가 있다
                Snackbar.make(
                    mLayout!!,
                    "이 앱을 실행하기 위해서는 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인") { // 3-3. 사용자에게 퍼미션 요청, 요청 결과는 onRequestPermissionResult에서 수신
                    ActivityCompat.requestPermissions(
                        this@SelectDestination_2_6Activity,
                        REQUIRED_PERMISSIONS,
                        SelectDestination_2_6Activity.PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            } else {
                //4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우 에는 퍼미션 요청을 바로한다
                // 요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    SelectDestination_2_6Activity.PERMISSIONS_REQUEST_CODE
                )
            }
        }
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.setOnMapClickListener { Log.d(SelectDestination_2_6Activity.TAG, "onMapClick :") }

        val averagePosition = LatLng(averageLat, averageLon)

        showPlaceInformation(averagePosition);

        searchKeyword("카페", averageLon.toString(), averageLat.toString(), Integer(5000))
    }

    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                location = locationList[locationList.size - 1]
                currentPosition = LatLng(location!!.latitude, location!!.longitude)
                val markerTitle = getCurrentAddress(currentPosition!!)
                val markerSnippet =
                    "위도 : " + location!!.latitude.toString() + "경도 : " + location!!.longitude.toString()
                Log.d(SelectDestination_2_6Activity.TAG, "onLocationResult : $markerSnippet")

                //현재 위치에 마커 생성하고 이동
//                setCurrentLocation(location, markerTitle, markerSnippet)
                mCurrentLocation = location
            }
        }
    }

    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(
                SelectDestination_2_6Activity.TAG,
                "startLocationUpdates : call showDialogForLocationServiceSetting"
            )
            showDialogForLocationServiceSetting()
        } else {
            val hasFineLocationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val hasCoarseLocationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(SelectDestination_2_6Activity.TAG, "startLocationUpdates : 퍼미션 안가지고 있음")
                return
            }
            Log.d(
                SelectDestination_2_6Activity.TAG,
                "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates"
            )
            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
            if (checkPermission()) mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(SelectDestination_2_6Activity.TAG, "onStart")
        if (checkPermission()) {
            Log.d(
                SelectDestination_2_6Activity.TAG,
                "onStart : call mFusedLocationClient.requestLocationUpdates"
            )
            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
            if (mMap != null) mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (mFusedLocationClient != null) {
            Log.d(SelectDestination_2_6Activity.TAG, "onStop : call stopLocationUpdates")
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
    }

    fun getCurrentAddress(latLng: LatLng): String {
        // GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<android.location.Address>?
        addresses = try {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 이용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 이용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        return if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            "주소 미발견"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    /*fun setCurrentLocation(location: Location?, markerTitle: String?, markerSnippet: String?) {
        if (currentMarker != null) currentMarker!!.remove()
        val currentLatLng = LatLng(location!!.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(currentLatLng)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap!!.moveCamera(cameraUpdate)
    }*/

    fun setDefaultLocation() {
        //디폴트 위치 -> 서울
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치 정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요"
        if (currentMarker != null) currentMarker!!.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap!!.moveCamera(cameraUpdate)
    }

    //여기서부터는 런타임 퍼미션 처리를 위한 메서드
    private fun checkPermission(): Boolean {
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            true
        } else false
    }

    // activityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메서드
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults)
        if (permsRequestCode == SelectDestination_2_6Activity.PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {
            // 요청 코드가 permissions request code이고 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            //모든 퍼미션을 허용했는지 체크
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                //퍼미션을 허용했다면 위치 업데이트 시작..
                startLocationUpdates()
            } else {
                //거부된 퍼미션이 있다면, 앱을 사용할 수 없는 이유 설명 하고 앱을 종료(2가지 case)
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    //사용자가 거부만 선택한 경우 앱을 다시 실행하여 허용을 선택하면 앱 사용 가능
                    Snackbar.make(
                        mLayout!!,
                        "퍼미션이 거부되었습니다, 앱을 다시 실행하여 퍼미션을 허용해 주세요",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("확인") { finish() }.show()
                } else {
                    // 다시 묻지 않음을 사용자가 체크하고 거부를 선택한 경우 설정으로 사용자가 가서 퍼미션을 허용해야 앱을 사용할 수 있다
                    Snackbar.make(
                        mLayout!!,
                        "퍼미션이 거부되었습니다, 설정에서 퍼미션을 허용해 주세요",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("확인") { finish() }.show()
                }
            }
        }
    }

    //여기서부터는 GPS활용을 위한 메서드
    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this@SelectDestination_2_6Activity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
    앱을 사용하기 위해서는 위치 서비스가 필요합니다.
    위치 설정을 수정하시겠습니까?
    """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(
                callGPSSettingIntent,
                SelectDestination_2_6Activity.GPS_ENABLE_REQUEST_CODE
            )
        }
        builder.setNegativeButton("취소") { dialog, id -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SelectDestination_2_6Activity.GPS_ENABLE_REQUEST_CODE ->                 //사용자가 gps활성화 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(SelectDestination_2_6Activity.TAG, "onActivityResult : GPS 활성화")
                        needRequest = true
                        return
                    }
                }
        }
    }

    override fun onPlacesFailure(e: PlacesException) {}
    override fun onPlacesStart() {}
    override fun onPlacesSuccess(places: List<Place>) {
        runOnUiThread {
            for (place in places) {
                val latLng = LatLng(place.latitude, place.longitude)
                val markerSnippet: String = getCurrentAddress(latLng)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(place.name)
                markerOptions.snippet(markerSnippet)
                val item: Marker = mMap!!.addMarker(markerOptions)
                previous_marker!!.add(item)
            }

            //중복 마커 제거
            val hashSet = HashSet<Marker>()
            hashSet.addAll(previous_marker!!)
            previous_marker!!.clear()
            previous_marker!!.addAll(hashSet)
        }
    }

    override fun onPlacesFinished() {}
    fun showPlaceInformation(location: LatLng?) {
        mMap!!.clear()
        if (previous_marker != null) {
            previous_marker!!.clear()
        }

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: Array<com.example.meethere.Address> =
            intent.getSerializableExtra("addressData") as Array<com.example.meethere.Address>
        for (i in addresses.indices) {
            val str: String = addresses[i].address
            val list = geocoder.getFromLocationName(str, 10)
            if (list != null) {
                val address: android.location.Address? = list.get(0)
                val lat: Double = address?.latitude!!.toDouble()
                val lon: Double = address?.longitude!!.toDouble()

                val addressPos = LatLng(lat, lon)
                val markerOptions = MarkerOptions()
                markerOptions.position(addressPos)
                markerOptions.title(addresses[i].name)
                markerOptions.snippet(addresses[i].address)
                mMap!!.addMarker(markerOptions)
            }
        }

        NRPlaces.Builder()
            .listener(this@SelectDestination_2_6Activity)
            .key("AIzaSyB0cxRl8WstOzAISge8W_w4k8oq-rx9AJ8")
            .latlng(location!!.latitude, location!!.longitude)
            .radius(500)
            .minprice(0, 2)
            .type(PlaceType.CAFE)
            .language("ko", "KR")
            .build()
            .execute()

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun getSum(numbers: DoubleArray): Double {
        var sum: Double = 0.0

        for (x in numbers) {
            sum += x
        }

        return sum
    }

    private fun getMean(numbers: DoubleArray): Double {
        var sum = getSum(numbers)
        return sum / numbers.size.toDouble()
    }

    companion object {
        private const val TAG = "googlemap_example"
        private const val GPS_ENABLE_REQUEST_CODE = 2001
        private const val UPDATE_INTERVAL_MS = 1000
        private const val FATEST_UPDATE_INTERVAL_MS = 500
        private const val PERMISSIONS_REQUEST_CODE = 100
        private const val BASE_URL = "https://dapi.kakao.com/"
        private const val API_KEY = "KakaoAK d921c75efdd92ffb9c3584afeacee0b6"  // REST API 키
    }

    private fun searchKeyword(keyword: String, x: String, y: String, radius: Integer) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(SelectDestinationActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(
            SelectDestinationActivity.API_KEY,
            keyword,
            x,
            y,
            radius
        )   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)

                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })

    }
}