package com.example.transitrace
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminManageDriverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_driver)

        val imageViewProfile: ImageView = findViewById(R.id.imageViewProfile)
        val createDriverCardView: CardView = findViewById(R.id.driver)
        val searchDriverCardView: CardView = findViewById(R.id.track)
        val updateDriverCardView: CardView = findViewById(R.id.emergency)
        val deleteDriverCardView: CardView = findViewById(R.id.buslist)

       /* imageViewProfile.setOnClickListener {
            // Replace MyProfileActivity::class.java with your desired activity
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }*/

        createDriverCardView.setOnClickListener {
            // Replace CreateDriverActivity::class.java with your desired activity
            val intent = Intent(this, AdminDriverCreateActivity::class.java)
            startActivity(intent)
        }

        searchDriverCardView.setOnClickListener {
            // Replace SearchDriverActivity::class.java with your desired activity
            val intent = Intent(this, AdminDriverSearchActivity::class.java)
            startActivity(intent)
        }

        updateDriverCardView.setOnClickListener {
            // Replace UpdateDriverActivity::class.java with your desired activity
            val intent = Intent(this, AdminDriverUpdateActivity::class.java)
            startActivity(intent)
        }

        deleteDriverCardView.setOnClickListener {
            // Replace DeleteDriverActivity::class.java with your desired activity
            val intent = Intent(this, AdminDriverDeleteActivity::class.java)
            startActivity(intent)
        }
    }
}
