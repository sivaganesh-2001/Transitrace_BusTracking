    package com.example.transitrace
    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.google.android.material.tabs.TabLayout
    import com.google.firebase.database.*

    class BlankFragment : Fragment() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var databaseReference: DatabaseReference
        private lateinit var databaseRef: DatabaseReference
        private lateinit var databaseRef1: DatabaseReference
        private lateinit var databaseRef2: DatabaseReference

        private var from: String = ""
        private var to: String = ""
        val busList = mutableListOf<BusData>()
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.user_selected_bus, container, false)

            // Retrieve arguments
            from = arguments?.getString("from") ?: ""
            to = arguments?.getString("to") ?: ""

            // Initialize RecyclerView
            recyclerView = view.findViewById(R.id.userSelectedBusRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context)

            // Initialize Firebase database reference
            databaseReference = FirebaseDatabase.getInstance().reference
            databaseRef1 = FirebaseDatabase.getInstance().getReference("bus_list")
            databaseRef2 = FirebaseDatabase.getInstance().getReference("live_track_bus")

            val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
            val adapter = BusAdapter(busList){}
            // Set default tab selection listener
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    busList.clear()
                    recyclerView.adapter?.notifyDataSetChanged()
                    when (tab.position) {
                        0 -> loadBusListFromDatabase(from,to) // Pass from and to arguments
                        1 -> loadRunningBusesFromDatabase(from,to)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    // Do nothing
                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }
            })

            tabLayout.getTabAt(0)?.select()

            return view

        }
        private fun loadBusListFromDatabase(from: String,to: String) {


            // Query Firebase database
            databaseReference.child("bus_list").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseRef1.orderByChild("from").equalTo(from)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                    Toast.makeText(context, "No Buses available", Toast.LENGTH_SHORT).show();
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
                                    recyclerView.adapter = BusAdapter(busList) { }
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
        private fun loadRunningBusesFromDatabase(from: String,to: String) {


            // Query Firebase database
            databaseReference.child("live_track_bus").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseRef2.orderByChild("from").equalTo(from)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                    Toast.makeText(context, "No Running Buses", Toast.LENGTH_SHORT).show();
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
                                    recyclerView.adapter = BusAdapter(busList) { busKey: String ->
                                        // Handle item click, pass data to the next activity
                                        val intent = Intent(requireContext(), UserBusMapActivity::class.java).apply {
                                            putExtra("busKey", busKey)
                                            // Pass the bus number as well
                                        }
                                        startActivity(intent)
                                    }
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
