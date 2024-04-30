package com.example.transitrace

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class UserBusMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var liveLocationRef: DatabaseReference
    private lateinit var busKey: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_bus_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the busKey from the intent
        busKey = intent.getStringExtra("busKey") ?: ""

        // Get the reference to the "live_location" subcollection
        liveLocationRef = FirebaseDatabase.getInstance().getReference("live_track_bus").child(busKey).child("live_location")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Request location permission
        if (checkLocationPermission()) {
            // Get user's last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Add a marker for the user's location if available
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    val userMarkerOptions = MarkerOptions()
                        .position(userLocation)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    mMap.addMarker(userMarkerOptions)

                    // Move camera to user's location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                }

                // Listen for changes in the live_location subcollection
                liveLocationRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Fetch the latest live location data
                        val latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
                        val longitude = dataSnapshot.child("longitude").getValue(Double::class.java)

                        // If location data exists, update the bus marker position without altering the zoom level
                        if (latitude != null && longitude != null) {
                            val busLocation = LatLng(latitude, longitude)
                            mMap.clear() // Clear existing markers
                            val busMarkerOptions = MarkerOptions().position(busLocation).title("Bus Location")
                                .icon(BitmapDescriptorFactory.fromBitmap(convertXmlVectorToBitmap(R.drawable.bus_icon, 64, 64)))
                            mMap.addMarker(busMarkerOptions)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })
            }.addOnFailureListener { exception ->
                // Handle failure to get user's last known location
                Toast.makeText(this, "Error getting location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Request location permission if not granted
            requestLocationPermission()
        }
    }


    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reload the map
                recreate()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertXmlVectorToBitmap(resourceId: Int, width: Int, height: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(this, resourceId)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
