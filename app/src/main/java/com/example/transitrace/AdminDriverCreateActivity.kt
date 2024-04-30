package com.example.transitrace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminDriverCreateActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var etName: EditText
    private lateinit var etmail: EditText
    private lateinit var etID: EditText
    private lateinit var et_dob: EditText
    private lateinit var et_doj: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_driver_create)
        val profile = findViewById<ImageView>(R.id.profile)
        profile.setOnClickListener {
            showProfileMenu()
        }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        etmail= findViewById(R.id.etmail)
        etName = findViewById(R.id.etname)
        etID = findViewById(R.id.etID)
        et_dob=findViewById(R.id.etdob)
        et_doj=findViewById(R.id.et_doj)
        etPhone = findViewById(R.id.etphone)
        etPassword = findViewById(R.id.etpassword)
        signupButton = findViewById(R.id.signupbutton)

        signupButton.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etmail.text.toString().trim()
            val id = etID.text.toString().trim()
            val dob=et_dob.text.toString().trim()
            val doj=et_doj.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && id.isNotEmpty() && dob.isNotEmpty() && doj.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                createUser(name,email, id, dob, doj, phone,password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showProfileMenu() {
        val profile = findViewById<ImageView>(R.id.profile)
        val popupMenu = PopupMenu(this, profile)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.feedback -> {
                    // Handle account profile action
                    Toast.makeText(this, "Account Profile clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.complaint -> {
                    // Handle complaint action
                    Toast.makeText(this, "Complaint clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun createUser(name: String,email: String, id: String, dob: String, doj:String, phone: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "id" to id,
                        "dob" to dob,
                        "doj" to doj,
                        "phone" to phone,
                        "password" to password
                    )
                    database.reference.child("driver").child(userId).setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Driver registered successfully", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Failed to register: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
