package com.example.transitrace
import kotlinx.coroutines.*
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
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

class trackingthebus : AppCompatActivity() {

    private lateinit var busListRef: DatabaseReference
    private lateinit var liveTrackBusRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var keys: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private val busList: MutableList<BusData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_track_bus_request)

        recyclerView = findViewById(R.id.driverSelectedBusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        keys = intent.getStringExtra("busKey")?: ""

        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("live_track_bus")
        busListRef = database.getReference("bus_list")
        liveTrackBusRef = database.getReference("live_track_bus")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?: return
                for (location in locationResult.locations) {
                    saveLocationData(location.latitude, location.longitude, keys)
                }
            }
        }
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // Set the desired interval for active location updates, in milliseconds.
            fastestInterval = 5000 // Set the fastest rate for active location updates, in milliseconds.
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Set the priority of the request.
        }

        // Check for location permission
        if (checkLocationPermission()) {
            // If permission is granted, proceed with fetching and tracking
            FetchandTrackLiveLocation(keys)
        } else {
            // If permission is not granted, request the permission
            requestLocationPermission()
        }

        // Fetch data from "bus_list"
        busListRef.orderByChild("id").equalTo(keys)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(
                            this@trackingthebus,
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
                        this@trackingthebus,
                        "Error fetching data: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })




        val trackButton: Button = findViewById(R.id.buttonTrack)
        trackButton.setOnClickListener {
            if (checkLocationPermission()) {
                saveBusDataToDatabase(keys)
                FetchandTrackLiveLocation(keys)
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun saveBusDataToDatabase(keys: String) {
        // Implementation of saving bus data to database
    }

    private fun FetchandTrackLiveLocation(keys: String) {
        liveTrackBusRef.child(keys)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the first matching document
                        val busDataSnapshot = dataSnapshot.child(keys)
                        val busData = busDataSnapshot.getValue(BusData::class.java)

                        // Use the retrieved busData as needed
                        if (busData!= null) {
                            // Display the bus data or use it to start location updates
                            Toast.makeText(this@trackingthebus, "Bus data fetched successfully", Toast.LENGTH_SHORT).show()
                            // Start location updates here
                            startLocationUpdates()
                        } else {
                            Toast.makeText(this@trackingthebus, "Bus data not fetched successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@trackingthebus, "Error fetching bus data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun saveLocationData(latitude: Double, longitude: Double, keys: String) {
        val liveLocationRef = database.getReference("live_track_bus/$keys/live_location")
        liveLocationRef.setValue(LocationData(latitude, longitude))
    }

    data class LocationData(val latitude: Double, val longitude: Double)
     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startLocationUpdates()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }




    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
