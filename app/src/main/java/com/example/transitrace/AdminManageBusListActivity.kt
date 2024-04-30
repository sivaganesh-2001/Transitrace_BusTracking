package com.example.transitrace
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminManageBusListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_bus_list)
        val profile = findViewById<ImageView>(R.id.profile)
        profile.setOnClickListener {
            showProfileMenu()
        }

        val imageViewProfile: ImageView = findViewById(R.id.profile)
        val createDriverCardView: CardView = findViewById(R.id.driver)
        val searchDriverCardView: CardView = findViewById(R.id.track)
        val updateDriverCardView: CardView = findViewById(R.id.emergency)
        val deleteDriverCardView: CardView = findViewById(R.id.buslist)

        createDriverCardView.setOnClickListener {

            val intent = Intent(this, AdminBusCreateActivity::class.java)
            startActivity(intent)
        }

        searchDriverCardView.setOnClickListener {

            val intent = Intent(this, AdminBusSearchActivity::class.java)
            startActivity(intent)
        }

        updateDriverCardView.setOnClickListener {

            val intent = Intent(this, AdminBusUpdateActivity::class.java)
            startActivity(intent)
        }

        deleteDriverCardView.setOnClickListener {

            val intent = Intent(this, AdminBusDeleteActivity::class.java)
            startActivity(intent)
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
}
