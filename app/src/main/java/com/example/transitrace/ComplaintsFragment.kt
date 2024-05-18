package com.example.transitrace

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ComplaintsFragment : Fragment() {

    private lateinit var complaintEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var complaintImageView: ImageView
    private var imageUri: Uri? = null

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_complaints, container, false)

        // Initialize UI elements
        complaintEditText = view.findViewById(R.id.editTextTextMultiLine)
        phoneEditText = view.findViewById(R.id.complaint_phone)
        locationEditText = view.findViewById(R.id.complaint_txt)
        submitButton = view.findViewById(R.id.submit)
        complaintImageView = view.findViewById(R.id.complaint_img)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(
            "com.example.transitrace.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )

        // Set click listener for the ImageView to select an image
        complaintImageView.setOnClickListener {
            pickImageFromGallery()
        }

        // Set click listener for the submit button
        submitButton.setOnClickListener {
            saveComplaintToFirestore()
        }

        return view
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            complaintImageView.setImageURI(imageUri)
        }
    }

    private fun saveComplaintToFirestore() {
        val complaintText = complaintEditText.text.toString().trim()
        val phoneText = phoneEditText.text.toString().trim()
        val locationText = locationEditText.text.toString().trim()

        // Retrieve email from SharedPreferences
        val userEmail = sharedPreferences.getString("email", "")

        if (complaintText.isNotEmpty() && userEmail != null && userEmail.isNotEmpty()) {
            // Upload image if selected
            if (imageUri != null) {
                val imageRef = storage.reference.child("complaints/${UUID.randomUUID()}.jpg")
                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val complaintData = hashMapOf<String, Any>(
                                "complaint" to complaintText,
                                "userEmail" to userEmail,
                                "phone" to phoneText,
                                "location" to locationText,
                                "imageUrl" to uri.toString(),
                                "date" to Calendar.getInstance().time
                            )

                            saveDataToFirestore(complaintData)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to upload image. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Save complaint data without image
                val complaintData = hashMapOf<String, Any>(
                    "complaint" to complaintText,
                    "userEmail" to userEmail,
                    "phone" to phoneText,
                    "location" to locationText,
                    "date" to Calendar.getInstance().time
                )

                saveDataToFirestore(complaintData)
            }
        } else {
            Toast.makeText(requireContext(), "Please enter complaint and select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToFirestore(complaintData: HashMap<String, Any>) {
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
    }

    private fun clearFields() {
        complaintEditText.text.clear()
        phoneEditText.text.clear()
        locationEditText.text.clear()
        complaintImageView.setImageResource(R.drawable.bx_camera) // Reset the image view
        imageUri = null // Clear the image URI
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
