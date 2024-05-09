package com.example.transitrace
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class UserHomeActivity : AppCompatActivity() {

    private lateinit var editTextFrom: EditText
    private lateinit var editTextTo: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonReset: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", MODE_PRIVATE)

        val profile = findViewById<ImageView>(R.id.profile)
        profile.setOnClickListener {
            showProfileMenu()
        }
        editTextFrom = findViewById(R.id.editTextFrom)
        editTextTo = findViewById(R.id.editTextTo)
        buttonSubmit = findViewById(R.id.buttonTrack)
        buttonReset = findViewById(R.id.buttonReset)

        buttonSubmit.setOnClickListener {
            navigateToBusListActivity()
        }

        buttonReset.setOnClickListener {
            resetFields()
        }
    }

    private fun showProfileMenu() {
        val profile = findViewById<ImageView>(R.id.profile)
        val popupMenu = PopupMenu(this, profile)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sos -> {
                    startActivity(Intent(this, EmergencySosActivity::class.java))
                    true // Return true to consume the click event

                }
                R.id.feedback -> {
                    // Handle account profile action
                    startActivity(Intent(this, UserFeedback::class.java))
                    true // Return true to consume the click event
                }
                R.id.complaint -> {
                    // Handle complaint action
                    startActivity(Intent(this, UserComplaintActivity::class.java))
                    true
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

    private fun navigateToBusListActivity() {
        val from = editTextFrom.text.toString().trim().lowercase() // Trim extra spaces and convert to lowercase
        val to = editTextTo.text.toString().trim().lowercase() // Trim extra spaces and convert to lowercase

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from", from)
        intent.putExtra("to", to)
        startActivity(intent)
    }

    private fun resetFields() {
        editTextFrom.text.clear()
        editTextTo.text.clear()
    }
}
