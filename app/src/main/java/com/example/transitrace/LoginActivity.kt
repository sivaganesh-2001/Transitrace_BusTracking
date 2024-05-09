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
            val emailOrPhoneNumber = findViewById<EditText>(R.id.etmail).text.toString()
            val password = findViewById<EditText>(R.id.etpassword).text.toString()

            if (emailOrPhoneNumber.isNotEmpty() && password.isNotEmpty()) {
                if (isEmail(emailOrPhoneNumber)) {
                    // User login
                    loginUser(emailOrPhoneNumber, password)
                } else {
                    // Driver login
                    signInWithPhoneNumberAndPassword(emailOrPhoneNumber, password)
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isEmail(text: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Save email to SharedPreferences
                        saveEmailToSharedPreferences(email)
                        // Check if the user exists in the "user" collection
                        val userRef = database.child("users").child(user.uid)
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                if (userSnapshot.exists()) {
                                    // User exists in the "user" collection, navigate to UserHomeActivity
                                    navigateToUserHomeActivity()
                                } else {
                                    // User not found in "user" collection
                                    Toast.makeText(
                                        this@LoginActivity, "User not found ",
                                        Toast.LENGTH_SHORT
                                    ).show()
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

    private fun navigateToUserHomeActivity() {
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity so that it's not in the back stack
    }

    private fun signInWithPhoneNumberAndPassword(phoneNumber: String, password: String) {
        val userRef = database.child("driver").orderByChild("Phone").equalTo(phoneNumber)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val storedPassword =   userSnapshot.child("Password").getValue(String::class.java)
                        if (storedPassword == password) {
                            // Authentication successful
                            saveEmailToSharedPreferences(phoneNumber)
                            navigateToDriverHomeActivity()
                            return
                        }
                    }
                }
                // Authentication failed
                Toast.makeText(
                    this@LoginActivity, "Invalid phone number or password",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Log.e("LoginActivity", "Error retrieving user data: ${databaseError.message}")
            }
        })
    }



    private fun navigateToDriverHomeActivity() {
        val intent = Intent(this, DriverHomeActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity so that it's not in the back stack
    }
}
