package com.example.transitrace

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import javax.xml.transform.OutputKeys

class DriverTrackBusRequest : AppCompatActivity() {

    private lateinit var busListRef: DatabaseReference
    private lateinit var liveTrackBusRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusAdapter
    private lateinit var keys: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var busData: BusData
    private var isPaused = false
    private val pauseHandler = Handler(Looper.getMainLooper())
    private var timer: CountDownTimer? = null
    private val busList: MutableList<BusData> = mutableListOf()
    private lateinit var messageTextView: TextView
    private var trackingStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_track_bus_request)

        recyclerView = findViewById(R.id.driverSelectedBusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        keys = intent.getStringExtra("busKey") ?: ""

        val database = FirebaseDatabase.getInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up location updates callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    saveLocationData(location.latitude, location.longitude, keys)
                }
            }
        }
        databaseRef = database.getReference("live_track_bus")
        // Correctly initialize busListRef and liveTrackBusRef
        busListRef = database.getReference("bus_list")
        liveTrackBusRef = database.getReference("live_track_bus")

        // Get reference to the driver data
        val pauseButton: Button = findViewById(R.id.buttonPause)
        val stopButton: Button = findViewById(R.id.buttonStop)

        pauseButton.setOnClickListener {
            pauseTrackingForMinutes(15)
            trackingStatus = "Track paused"
            updateMessageTextView()

        }

        stopButton.setOnClickListener {
            stopTrackingPermanently()
            trackingStatus = "Tracking stopped"
            updateMessageTextView()
        }

        // Fetch data from "bus_list"
        busListRef.orderByChild("id").equalTo(keys)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(
                            this@DriverTrackBusRequest,
                            "No buses available",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Clear existing data
                        busList.clear()

                        // Iterate through dataSnapshot
                        for (snapshot in dataSnapshot.children) {
                            // Parse each dataSnapshot to your BusData model
                            val busData = snapshot.getValue(BusData::class.java)
                            // Add bus to the list
                            busData?.let {
                                val bus = BusData(
                                    id = it.id,
                                    busNumber = it.busNumber,
                                    to = it.to,
                                    endingTime = it.endingTime,
                                    busRoute = it.busRoute,
                                    from = it.from,
                                    startingTime = it.startingTime
                                )
                                busList.add(bus)
                            }
                        }

                        // Update RecyclerView adapter
                        adapter = BusAdapter(busList) { }
                        recyclerView.adapter = adapter
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@DriverTrackBusRequest,
                        "Error fetching data: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })



        val trackButton: Button = findViewById(R.id.buttonTrack)
        trackButton.setOnClickListener {
            if (checkLocationPermission()) {
                saveBusDataToDatabase(keys)
                fetchBusData(keys)

                trackingStatus = "Bus tracked successfully"
                updateMessageTextView()


            } else {
                requestLocationPermission()
            }
        }
    }


    fun saveBusDataToDatabase(keys: String) {
        // Create a reference to the bus document with the provided key under "live_track_bus" collection
        val busDocRef = liveTrackBusRef.child(keys)

        busDocRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the bus is already being tracked, display a toast message
                    Toast.makeText(
                        this@DriverTrackBusRequest,
                        "Bus already being tracked",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // If the bus is not already being tracked, proceed to add it
                    busListRef.orderByChild("id").equalTo(keys)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // If the bus ID doesn't exist in bus_list collection, show a toast message
                                    Toast.makeText(
                                        this@DriverTrackBusRequest,
                                        "Bus ID not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // If the bus ID exists in bus_list collection, add it to live_track_bus collection
                                    for (snapshot in dataSnapshot.children) {
                                        // Parse each dataSnapshot to your BusData model
                                        val busData = snapshot.getValue(BusData::class.java)
                                        // Add bus to the list
                                        busData?.let {
                                            val bus = BusData(
                                                id = it.id,
                                                busNumber = it.busNumber,
                                                to = it.to,
                                                endingTime = it.endingTime,
                                                busRoute = it.busRoute,
                                                from = it.from,
                                                startingTime = it.startingTime
                                            )
                                            busDocRef.setValue(bus)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this@DriverTrackBusRequest,
                                                        "Driver data stored successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this@DriverTrackBusRequest,
                                                        "Error storing driver data: ${e.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } ?: run {
                                            // Handle case where driverData is null
                                            Toast.makeText(
                                                this@DriverTrackBusRequest,
                                                "Driver data is null",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@DriverTrackBusRequest,
                                    "Error fetching driver data: ${error.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DriverTrackBusRequest,
                    "Error fetching bus tracking data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun fetchBusData(key: String) {
        busListRef.orderByChild("id").equalTo(keys)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the bus data
                        busData = dataSnapshot.getValue(BusData::class.java) ?: BusData()

                        // Display the fetched data or use it as needed
                        // For example, you can display it in the UI or use it to start location updates
                        Toast.makeText(
                            this@DriverTrackBusRequest,
                            "Bus data fetched successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startLocationUpdates()
                    } else {
                        Toast.makeText(
                            this@DriverTrackBusRequest,
                            "No bus data found for the provided key",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@DriverTrackBusRequest,
                        "Error fetching bus data: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }
//    private fun saveBasicDataToDatabase() {
//        // Retrieve data from EditText fields
//        val from = editTextFrom.text.toString().trim().lowercase()
//        val to = editTextTo.text.toString().trim().lowercase()
//        val startingTime = editTextStartingTime.text.toString().trim()
//        val endingTime = editTextEndingTime.text.toString().trim()
//        val busRoute = editTextBusRoute.text.toString().trim()
//        val busNumber = editTextBusNumber.text.toString().trim()
//
//        // Generating unique key for each entry in "live_bus_track" collection
//        val key = databaseRef.push().key
//
//        // Check if key is not null before proceeding
//        if (key != null) {
//            // Create BusData object
//            val busData = BusData(from, to, startingTime, endingTime, busRoute, busNumber)
//
//            // Save BusData object to "live_bus_track" collection
//            databaseRef.child(key).setValue(busData)
//                .addOnSuccessListener {
//                    // Save the key for future reference
//                    busKey = key
//                    Toast.makeText(this, "Location data updated successfully", Toast.LENGTH_SHORT).show()
//                    // Check if starting time is reached
//                    val currentTimeMillis = System.currentTimeMillis()
//                    val startingTimeMillis = startingTime.toLong()
//                    if (currentTimeMillis >= startingTimeMillis) {
//                        // Starting time reached, start location updates
//                        startLocationUpdates()
//
//                    } else {
//                        // Starting time not reached
//                        // Navigate to next page
//
//                    }
//                }
//                .addOnFailureListener {e->
//                    // Handle failure if needed
//                    val errorMessage = "Error updating location data: ${e.message}"
//                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
//                    Log.e(TAG, errorMessage)
//                }
//        }
//    }


    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            // Request location updates
            val locationRequest = LocationRequest.create()?.apply {
                interval = 1000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationRequest?.let {
                fusedLocationClient.requestLocationUpdates(it, locationCallback, null)
            }
        } else {
            // Handle the case where location permission is not granted
        }
    }

    private fun pauseTrackingForMinutes(minutes: Int) {
        // Pause location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
            .addOnSuccessListener {
                // Start the countdown timer
                timer = object : CountDownTimer(minutes.toLong() * 60 * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                      //  Toast.makeText(this@DriverTrackBusRequest, "Paused for 15 minutes", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFinish() {
                        // Resume tracking after the countdown
                        startLocationUpdates()
                        timer?.cancel()
                    }
                }.start()
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
                Toast.makeText(this, "Error pausing location updates: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun stopTrackingPermanently() {
        // Stop tracking and delete the entry from "live_track_bus"
        liveTrackBusRef.child(keys).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Tracking stopped permanently.", Toast.LENGTH_SHORT).show()
                    // Stop location updates if they are still active
                    stopLocationUpdates()
                } else {
                    Toast.makeText(this, "Failed to stop tracking.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveLocationData(latitude: Double, longitude: Double, key: String) {
        // Ensure that the key is not empty and location permission is granted
        if (key.isNotEmpty() && checkLocationPermission()) {
            // Create a reference to the bus document with the provided key under "live_track_bus" collection
            val busDocRef = liveTrackBusRef.child(key)

            // Create a reference to the "live_location" subcollection inside the bus document
            val locationRef = busDocRef.child("live_location")

            // Create a map containing the updated latitude and longitude
            val locationData = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )

            // Push a new location entry under "live_location" subcollection
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
            // Handle the case where key is empty or location permission is not granted
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
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateMessageTextView() {
        val intent = Intent(this, DriverBusTrackSuccessActivity::class.java)
        intent.putExtra("trackingStatus", trackingStatus)
        startActivity(intent)
    }

}
