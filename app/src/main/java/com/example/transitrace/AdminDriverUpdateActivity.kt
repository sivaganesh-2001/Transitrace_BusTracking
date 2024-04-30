package com.example.transitrace
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AdminDriverUpdateActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonUpdate: Button
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_driver_update)

        editTextSearch = findViewById(R.id.editTextSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonUpdate = findViewById(R.id.buttonUpdate)

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().reference.child("drivers")

        // Set click listeners
        buttonSearch.setOnClickListener {
            val searchText = editTextSearch.text.toString().trim()
            searchDriver(searchText)
        }

        buttonUpdate.setOnClickListener {
            // Handle update logic here
        }
    }

    private fun searchDriver(searchText: String) {
        // Perform Firebase Database query to search for the driver
        val query: Query = databaseRef.orderByChild("name").equalTo(searchText)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Driver found, handle the data retrieval here
                    for (snapshot in dataSnapshot.children) {
                        val driverId = snapshot.key // Get the driver ID
                        val driverData = snapshot.getValue(Driver::class.java)
                        // Handle displaying driver information or updating UI
                    }
                } else {
                    // Driver not found, show appropriate message to the user
                    Toast.makeText(this@AdminDriverUpdateActivity, "Driver not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@AdminDriverUpdateActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
