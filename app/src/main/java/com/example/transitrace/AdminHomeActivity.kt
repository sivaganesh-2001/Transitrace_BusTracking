package com.example.transitrace
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        // Find views
        val imageViewProfile: ImageView = findViewById(R.id.imageViewProfile)
        val driverCardView = findViewById<CardView>(R.id.driver)
        val trackCardView = findViewById<CardView>(R.id.Track)
        val emergencyCardView = findViewById<CardView>(R.id.emergency)
        val busListCardView = findViewById<CardView>(R.id.buslist)
        val complaintCardView = findViewById<CardView>(R.id.complaint)
        val userCardView = findViewById<CardView>(R.id.user)

        // Set click listeners
    /*    imageViewProfile.setOnClickListener {
            // Replace MyProfileActivity::class.java with your desired activity
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }*/

        driverCardView.setOnClickListener {
            // Replace ManageDriverActivity::class.java with your desired activity
            val intent = Intent(this, AdminManageDriverActivity::class.java)
            startActivity(intent)
        }



        emergencyCardView.setOnClickListener {
            // Replace EmergencyActivity::class.java with your desired activity
            val intent = Intent(this, AdminEmergencyActivity::class.java)
            startActivity(intent)
        }

        busListCardView.setOnClickListener {
            // Replace BusListActivity::class.java with your desired activity
            val intent = Intent(this, AdminManageBusListActivity::class.java)
            startActivity(intent)
        }



        userCardView.setOnClickListener {
            // Replace ManageUserActivity::class.java with your desired activity
            val intent = Intent(this, AdminManageUserActivity::class.java)
            startActivity(intent)
        }
    }
}
