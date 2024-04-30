package com.example.transitrace

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupButton: TextView = findViewById(R.id.signupbutton)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        val loginButton = findViewById<MaterialButton>(R.id.btnlogin)
        signupButton.setOnClickListener {
            // Start SignupActivity when the TextView is clicked
            val intent = Intent(this, UserSignupActivity::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.etmail).text.toString()
            val password = findViewById<EditText>(R.id.etpassword).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Save email to SharedPreferences
                        saveEmailToSharedPreferences(email) // Ensure this is being called
                        Log.d("LoginActivity", "Email saved to SharedPreferences: $email") // Log the saved email
                        // Check if the user exists in the "user" collection
                        val userRef = database.child("users").child(user.uid)
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                if (userSnapshot.exists()) {
                                    // User exists in the "user" collection, navigate to UserHomeActivity
                                    navigateToUserHomeActivity()
                                } else {
                                    // Check if the user exists in the "driver" collection
                                    val driverRef = database.child("driver").child(user.uid)
                                    driverRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(driverSnapshot: DataSnapshot) {
                                            if (driverSnapshot.exists()) {
                                                // User exists in the "driver" collection, navigate to DriverHomeActivity
                                                navigateToDriverHomeActivity()
                                            } else {
                                                // Check if the user exists in the "admin" collection
                                                val adminRef = database.child("admin").child(user.uid)
                                                adminRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(adminSnapshot: DataSnapshot) {
                                                        if (adminSnapshot.exists()) {
                                                            // User exists in the "admin" collection, navigate to AdminDriverCreateActivity
                                                            navigateToAdminDriverCreateActivity()
                                                        } else {
                                                            // User not found in any collection
                                                            Toast.makeText(
                                                                this@LoginActivity, "User not found ",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        // Handle database error
                                                        Log.e("UserData", "Error retrieving admin data: ${error.message}")
                                                    }
                                                })
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Handle database error
                                            Log.e("UserData", "Error retrieving driver data: ${error.message}")
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle database error
                                Log.e("UserData", "Error retrieving user data: ${error.message}")
                            }
                        })
                    }
                } else {
                    // Authentication failed
                    Toast.makeText(
                        this, "Authentication failed. ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveEmailToSharedPreferences(email: String) {
        with(sharedPreferences.edit()) {
            putString("email", email)
            apply()
        }
    }

    private fun navigateToAdminDriverCreateActivity() {
        // Navigate to AdminDriverCreateActivity
        startActivity(Intent(this, AdminHomeActivity::class.java))
        finish()
    }

    private fun navigateToUserHomeActivity() {
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity so that it's not in the back stack
    }

    private fun navigateToDriverHomeActivity() {
        val intent = Intent(this, DriverHomeActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity so that it's not in the back stack
    }
}
