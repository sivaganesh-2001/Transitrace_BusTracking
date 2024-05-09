package com.example.transitrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class UserSelectedBusListActivity : AppCompatActivity() {

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var databaseRef: DatabaseReference
//    private lateinit var adapter: BusAdapter
//    private val busList = mutableListOf<Bus>()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_selected_bus_list)
//
//        recyclerView = findViewById(R.id.userSelectedBusRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = BusAdapter(busList)
//        recyclerView.adapter = adapter
//
//        databaseRef = FirebaseDatabase.getInstance().getReference("live_track_bus")
//
//        val from = intent.getStringExtra("from") ?: ""
//        val to = intent.getStringExtra("to") ?: ""
//
//        databaseRef.orderByChild("from").equalTo(from)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (!snapshot.exists()) {
//                        // No buses available
//                    } else {
//                        busList.clear()
//                        snapshot.children.forEach { childSnapshot ->
//                            val busData = childSnapshot.getValue(BusData::class.java)
//                            busData?.let {


//                                    val bus = Bus(
//                                        busNumber = it.busNumber,
//                                        endingPoint = it.to,
//                                        endingTime = it.endingTime,
//                                        routes = it.busRoute,
//                                        startingPoint = it.from,
//                                        startingTime = it.startingTime
//                                    )
//                                    busList.add(bus)
//                                }
//                            }
//                        }
//                        adapter.notifyDataSetChanged()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle error
//                }
//            })
//    }
}
