package com.example.transitrace

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class ComplaintsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complaints, container, false)
    }



        private lateinit var complaintEditText: EditText
        private lateinit var submitButton: Button
        private lateinit var clearButton: Button

        private val firestore: FirebaseFirestore by lazy {
            FirebaseFirestore.getInstance()
        }

        private lateinit var sharedPreferences: SharedPreferences


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Initialize UI elements
            complaintEditText = view.findViewById(R.id.editTextTextMultiLine)
            submitButton = view.findViewById(R.id.submit)
            clearButton = view.findViewById(R.id.clear)

            // Initialize SharedPreferences
            sharedPreferences = requireActivity().getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

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
                        Toast.makeText(requireContext(), "Complaint submitted successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Handle errors while saving complaint
                        Toast.makeText(requireContext(), "Failed to submit complaint. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Show an error message if complaint text or email is empty
                Toast.makeText(requireContext(), "Please enter complaint", Toast.LENGTH_SHORT).show()
            }
        }

        private fun clearFields() {
            complaintEditText.text.clear()
        }
    }

