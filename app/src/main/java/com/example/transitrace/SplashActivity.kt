package com.example.transitrace

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        // Delay navigation to the next screen for 2 seconds
        Handler().postDelayed({
            // Check if the user's email is saved in SharedPreferences
            val email = getEmailFromSharedPreferences()

            if (email.isNotEmpty()) {
                if(isEmail(email)){
                    // User email is saved, navigate to the appropriate screen based on the saved email
                    navigateToHomeScreen()
                }else {
                    navigateToDriverHomeActivity()                }

            } else {
                // No email saved, navigate to LoginActivity
                navigateToLogin()
            }
        }, 2000) // 2000 milliseconds = 2 seconds
    }

    private fun getEmailFromSharedPreferences(): String {
        return sharedPreferences.getString("email", "") ?: ""
    }

    private fun navigateToHomeScreen() {
        // Navigate to the appropriate screen based on the saved email
        // For example, you can navigate to UserHomeActivity or DriverHomeActivity here
        // Replace UserHomeActivity::class.java with your actual home screen activity
        startActivity(Intent(this, UserHomeActivity::class.java))
        finish() // Finish SplashActivity so that it's not in the back stack
    }

    private fun navigateToLogin() {
        // Navigate to LoginActivity
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish() // Finish SplashActivity so that it's not in the back stack
    }
    private fun isEmail(text: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }
    private fun navigateToDriverHomeActivity() {
        val intent = Intent(this, DriverHomeActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity so that it's not in the back stack
    }
}
