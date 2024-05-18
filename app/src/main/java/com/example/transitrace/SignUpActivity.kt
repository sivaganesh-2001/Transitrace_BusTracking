package com.example.transitrace
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobileNum: EditText
    private lateinit var etPassword: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var btnSignup: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_new)

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference.child("users")

        // Initialize views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNum = findViewById(R.id.etMobileNum)
        etPassword = findViewById(R.id.etPassword)
        rgGender = findViewById(R.id.rg_type)
        btnSignup = findViewById(R.id.btnSignup)

        btnSignup.setOnClickListener {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etMobileNum.text.toString().trim()
        val password = etPassword.text.toString().trim()

        val selectedGenderId = rgGender.checkedRadioButtonId
        val selectedGender = findViewById<RadioButton>(selectedGenderId)
        val gender = selectedGender.text.toString()

        // Validate input fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser? = mAuth.currentUser
                        val userId = currentUser?.uid

                        // Create a new User object
                        val user = User(name, email, gender, phone,password)

                        // Save user data to Firebase Realtime Database
                        if (userId != null) {
                            mDatabase.child(userId).setValue(user)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                        // Proceed to next activity or perform desired action
                                    } else {
                                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                        Log.e("SignUpActivity", "Error saving user data to database", dbTask.exception)
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    data class User(
        val name: String = "",
        val email: String = "",
        val gender: String = "",
        val phone: String = "",
        val password: String = ""
    )

}
