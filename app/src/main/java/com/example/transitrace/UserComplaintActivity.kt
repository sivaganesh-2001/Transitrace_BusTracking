package com.example.transitrace

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class UserComplaintActivity : AppCompatActivity() {

    private lateinit var complaintEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var clearButton: Button

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_complaint)

        // Initialize UI elements
        complaintEditText = findViewById(R.id.editTextTextMultiLine)
        submitButton = findViewById(R.id.submit)
        clearButton = findViewById(R.id.clear)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        // Set click listener for the submit button
        submitButton.setOnClickListener {
            saveComplaintToFirestore()
        }

        // Set click listener for the clear button
        clearButton.setOnClickListener {
            clearFields()
        }
    }

    private fun saveComplaintToFirestore() {
        val complaintText = complaintEditText.text.toString().trim()

        // Retrieve email from SharedPreferences
        val userEmail = sharedPreferences.getString("email", "")

        if (complaintText.isNotEmpty() && userEmail != null && userEmail.isNotEmpty()) {
            // Create a new complaint document
            val complaintData = hashMapOf(
                "complaint" to complaintText,
                "userEmail" to userEmail, // Use the retrieved email
                "date" to Calendar.getInstance().time // Save the current date
            )

            // Save the complaint document to Firestore
            firestore.collection("complaints")
                .add(complaintData)
                .addOnSuccessListener {
                    // Clear the fields after successful submission
                    clearFields()

                    // Show a success message
                    Toast.makeText(this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Handle errors while saving complaint
                    Toast.makeText(this, "Failed to submit complaint. Please try again later.", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Show an error message if complaint text or email is empty
            Toast.makeText(this, "Please enter complaint", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        complaintEditText.text.clear()
    }
}
