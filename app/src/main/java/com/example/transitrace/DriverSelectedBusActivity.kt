package com.example.transitrace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class DriverSelectedBusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseRef: DatabaseReference
    private lateinit var busKeys: MutableList<String> // Change to Int
    private lateinit var from: String
    private lateinit var to: String
    private val busList: MutableList<BusData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_selected_bus)

        recyclerView = findViewById(R.id.driverSelectedBusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        databaseReference = FirebaseDatabase.getInstance().reference
        databaseRef = FirebaseDatabase.getInstance().getReference("bus_list")
        busKeys = mutableListOf()

        from = intent.getStringExtra("from") ?: ""
        to = intent.getStringExtra("to") ?: ""

        databaseReference.child("bus_list").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                databaseRef.orderByChild("from").equalTo(from)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                // No buses available
                            } else {
                                // Clear existing data
                                busList.clear()

                                // Iterate through dataSnapshot
                                for (snapshot in snapshot.children) {
                                    // Parse each dataSnapshot to your BusData model
                                    val busData = snapshot.getValue(BusData::class.java)
                                    // Add bus to the list
                                    busData?.let {
                                        if (it.to == to) {
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
                                }

                                // Update RecyclerView adapter
                                recyclerView.adapter = BusAdapter(busList) {}
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle database error
                        }
                    })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}
