package com.example.transitrace

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminDriverDeleteActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var searchButton: Button
    private lateinit var editTextDriverName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_driver_delete)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("drivers")

        // Initialize views
        listView = findViewById(R.id.listViewDrivers)
        searchButton = findViewById(R.id.buttonSearch)
        editTextDriverName = findViewById(R.id.editTextDriverName)

        // Search button click listener
        searchButton.setOnClickListener {
            val driverName = editTextDriverName.text.toString()
            searchDriver(driverName)
        }
    }

    private fun searchDriver(driverName: String) {
        val query: Query = database.orderByChild("name").equalTo(driverName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val driverList = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val driver = snapshot.getValue(Driver::class.java)
                    driver?.let {
                        val driverInfo = "Name: ${it.name}, License: ${it.license}"
                        driverList.add(driverInfo)
                    }
                }

                // Display the list of drivers
                val adapter = ArrayAdapter(this@AdminDriverDeleteActivity, android.R.layout.simple_list_item_1, driverList)
                listView.adapter = adapter

                // Handle item click to delete the selected driver
                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedSnapshot = dataSnapshot.children.elementAt(position)
                    selectedSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}

data class Driver(
    val name: String = "",
    val license: String = ""
)
