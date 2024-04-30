package com.example.transitrace

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class DriverSelectedBusActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var busList: MutableList<String>
    private lateinit var selectedBusKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_selected_bus)

        listView = findViewById(R.id.list_view)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        database = FirebaseFirestore.getInstance()
        busList = mutableListOf()

        val from = intent.getStringExtra("from") ?: ""
        val to = intent.getStringExtra("to") ?: ""

        database.collection("bus_list")
            .whereEqualTo("from", from)
            .whereEqualTo("to", to)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty()) {
                    // If no buses available, display "No buses available"
                    adapter.add("No buses available")
                } else {
                    for (document in documents) {
                        val busNumber = document.getString("busNumber") ?: ""
                        busList.add(busNumber)
                    }
                    adapter.addAll(busList)
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }

        listView.setOnItemClickListener { _, view, position, _ ->
            if (busList.isNotEmpty()) {
                selectedBusKey = busList[position]
                // Track the selected bus using its key
                Toast.makeText(this, "Tracking $selectedBusKey", Toast.LENGTH_SHORT).show()
            }
        }

        val addButton = findViewById<FloatingActionButton>(R.id.addbutton)
        addButton.setOnClickListener {
            // Handle addition of special bus
            Toast.makeText(this, "Add Special Bus", Toast.LENGTH_SHORT).show()
        }
    }
}
