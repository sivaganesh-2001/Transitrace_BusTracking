package com.example.transitrace

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserFeedback : AppCompatActivity() {

    private lateinit var feedbackEditText: EditText
    private lateinit var ratingBar: RatingBar

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_feedback)

        // Initialize UI elements
        feedbackEditText = findViewById(R.id.editTextTextMultiLine)
        ratingBar = findViewById(R.id.ratingBar)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        val clearButton: Button = findViewById(R.id.button2)
        val submitButton: Button = findViewById(R.id.button)

        // Set click listener for the clear button
        clearButton.setOnClickListener {
            clearFeedbackFields()
        }

        // Set click listener for the submit button
        submitButton.setOnClickListener {
            saveFeedbackToFirestore()
        }
    }

    private fun clearFeedbackFields() {
        feedbackEditText.setText("")
        ratingBar.rating = 0f
    }

    private fun saveFeedbackToFirestore() {
        val feedbackText = feedbackEditText.text.toString().trim()
        val rating = ratingBar.rating.toDouble()

        if (feedbackText.isNotEmpty()) {
            // Retrieve the email ID from SharedPreferences
            val email = getEmailFromSharedPreferences()

            // Ensure email is not empty
            if (email.isNotEmpty()) {
                // Create a new feedback document
                val feedbackData = hashMapOf(
                    "email" to email,
                    "feedback" to feedbackText,
                    "rating" to rating
                )

                // Save the feedback document to Firestore
                firestore.collection("feedback")
                    .add(feedbackData)
                    .addOnSuccessListener {
                        // Feedback saved successfully
                        Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
                        clearFeedbackFields() // Clear the fields after successful submission
                    }
                    .addOnFailureListener { e ->
                        // Handle errors while saving feedback
                        Toast.makeText(this, "Failed to submit feedback. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Show an error message if email is empty
                Toast.makeText(this, "Email not found. Please login again.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Show an error message if feedback text is empty
            Toast.makeText(this, "Please enter your feedback.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEmailFromSharedPreferences(): String {
        return sharedPreferences.getString("email", "") ?: ""
    }
}
