package com.example.transitrace

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BusTrackingActivity: AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var editTextFrom: EditText
    private lateinit var editTextTo: EditText
    private lateinit var editTextStartingTime: EditText
    private lateinit var editTextEndingTime: EditText
    private lateinit var editTextBusRoute: EditText
    private lateinit var editTextBusNumber: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonReset : Button
    private lateinit var busKey: String
    private lateinit var busData: BusData
    private lateinit var keys: String

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_home)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("live_track_bus")

        keys = intent.getStringExtra("busKey") ?: ""




        // Initialize views


        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up location updates callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    saveLocationData(location.latitude, location.longitude)
                }
            }
        }

        // Button listeners
        buttonSubmit.setOnClickListener {

            if (checkLocationPermission()) {
                // Save basic data to "live_bus_track" collection
                fetchBusData(keys)
            } else {
                requestLocationPermission()
            }
        }


    }

    private fun fetchBusData(key: String) {
        databaseRef.child(key)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the bus data
                        busData = dataSnapshot.getValue(BusData::class.java)?: BusData()

                        // Display the fetched data or use it as needed
                        // For example, you can display it in the UI or use it to start location updates
                        Toast.makeText(this@BusTrackingActivity, "Bus data fetched successfully", Toast.LENGTH_SHORT).show()
                        startLocationUpdates()
                    } else {
                        Toast.makeText(this@BusTrackingActivity, "No bus data found for the provided key", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@BusTrackingActivity, "Error fetching bus data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })

    }
    private fun saveBasicDataToDatabase() {
        // Retrieve data from EditText fields
        val from = editTextFrom.text.toString().trim().lowercase()
        val to = editTextTo.text.toString().trim().lowercase()
        val startingTime = editTextStartingTime.text.toString().trim()
        val endingTime = editTextEndingTime.text.toString().trim()
        val busRoute = editTextBusRoute.text.toString().trim()
        val busNumber = editTextBusNumber.text.toString().trim()

        // Generating unique key for each entry in "live_bus_track" collection
        val key = databaseRef.push().key

        // Check if key is not null before proceeding
        if (key != null) {
            // Create BusData object
            val busData = BusData(from, to, startingTime, endingTime, busRoute, busNumber)

            // Save BusData object to "live_bus_track" collection
            databaseRef.child(key).setValue(busData)
                .addOnSuccessListener {
                    // Save the key for future reference
                    busKey = key
                    Toast.makeText(this, "Location data updated successfully", Toast.LENGTH_SHORT).show()
                    // Check if starting time is reached
                    val currentTimeMillis = System.currentTimeMillis()
                    val startingTimeMillis = startingTime.toLong()
                    if (currentTimeMillis >= startingTimeMillis) {
                        // Starting time reached, start location updates
                        startLocationUpdates()

                    } else {
                        // Starting time not reached
                        // Navigate to next page

                    }
                }
                .addOnFailureListener {e->
                    // Handle failure if needed
                    val errorMessage = "Error updating location data: ${e.message}"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, errorMessage)
                }
        }
    }


    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            // Request location updates
            try {
                val locationRequest = LocationRequest.create()?.apply {
                    interval = 1000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                locationRequest?.let {
                    fusedLocationClient.requestLocationUpdates(it, locationCallback, null)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                // Handle the SecurityException
            }
        } else {
            // Handle the case where location permission is not granted
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun saveLocationData(latitude: Double, longitude: Double) {
        // Ensure that the busKey is not null and location permission is granted
        if (busKey != null && checkLocationPermission()) {
            // Create a reference to the live_location field under the bus document
            val locationRef = databaseRef.child(busKey).child("live_location")

            // Create a map containing the updated latitude and longitude
            val locationData = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )

            // Update the existing live_location field with the new location data
            locationRef.setValue(locationData)
                .addOnSuccessListener {
                    // Location data updated successfully
                    Toast.makeText(this, "Location data updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error occurred while updating location data
                      val errorMessage = "Error updating location data: ${e.message}"
                         Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                         Log.e(TAG, errorMessage)
                    // Add any additional error handling code here, if needed
                }


        } else {
            // Handle the case where busKey is null or location permission is not granted
        }
    }


    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
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
                // Permission granted
                fetchBusData(keys)
            } else {
                // Permission denied
                // Handle this case, show a message or request permission again
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "DriverHomeActivity"

    }

}



