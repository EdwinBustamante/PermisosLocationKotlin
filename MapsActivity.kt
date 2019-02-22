package com.misionrescate.permisosmapas

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.google.android.gms.common.api.GoogleApiClient




class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

//https://stackoverflow.com/questions/33251373/turn-on-location-services-without-navigating-to-settings-page
  //  http://www.digitstory.com/enable-gps-automatically-android/

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        methodRequiresPermissions()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    private fun methodRequiresPermissions() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, options = optionsConfigure) {
        GPSStatus()
        if (gpsStatus == true) {
            Toast.makeText(this, "permisos aceptados y gps encendico", Toast.LENGTH_LONG).show()
        } else {
            val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(i)
        }
    }

    lateinit var locationManager: LocationManager
    var gpsStatus: Boolean = false
    private fun GPSStatus() {
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    val optionsConfigure = QuickPermissionsOptions(
            handleRationale = true,
            handlePermanentlyDenied = true,
            rationaleMessage = "Para usar la aplicacion debe aceptar si o si los permisos",
            permanentlyDeniedMessage = "como desactivo los permisos no funcionara correctamente",
            rationaleMethod = { req -> rationaleCallback(req) }
    )

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        Toast.makeText(this, "PERMISOS denegados", Toast.LENGTH_SHORT).show()
    }

    lateinit var googleApiClient: GoogleApiClient

    @Synchronized
    private fun setUpGClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        googleApiClient.connect()
    }
    override fun onConnectionFailed(p0: ConnectionResult) {

    }
    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }

}
