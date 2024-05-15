package com.example.transitrace

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity

class DriverHomeActivity : AppCompatActivity() {

    private lateinit var fromEditText: EditText
    private lateinit var toEditText: EditText
    private lateinit var trackButton: Button
    private lateinit var resetButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_home)
        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", MODE_PRIVATE)
        fromEditText = findViewById(R.id.from)
        toEditText = findViewById(R.id.to)
        trackButton = findViewById(R.id.buttonTrack)
        resetButton = findViewById(R.id.buttonReset)
        val profile = findViewById<ImageView>(R.id.profile)
        profile.setOnClickListener {
            showProfileMenu()
        }
        trackButton.setOnClickListener {
            val from = fromEditText.text.toString().trim().lowercase() // Trim extra spaces and convert to lowercase
            val to = toEditText.text.toString().trim().lowercase()

            if (from.isNotEmpty() && to.isNotEmpty()) {
                val intent = Intent(this, DriverSelectedBusActivity::class.java).apply {
                    putExtra("from", from)
                    putExtra("to", to)
                }
                startActivity(intent)
            } else {
                // Handle empty input
                // You can show a Toast message or set error on EditText fields
            }
        }


        resetButton.setOnClickListener {
            fromEditText.text.clear()
            toEditText.text.clear()
        }
    }
    private fun showProfileMenu() {
        val profile = findViewById<ImageView>(R.id.profile)
        val popupMenu = PopupMenu(this, profile)
        popupMenu.menuInflater.inflate(R.menu.drivermenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sos -> {
                    startActivity(Intent(this, EmergencySosActivity::class.java))
                    true // Return true to consume the click event

                }

                R.id.logout -> {
                    val editor = sharedPreferences.edit()
                    editor.clear() // Clear all stored preferences
                    editor.apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

}
