package com.example.transitrace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DriverBusTrackSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_track_success)

        val trackingStatus = intent.getStringExtra("trackingStatus")?: ""
        val messageTextView = findViewById<TextView>(R.id.messageTextView) // Assuming you have a TextView with id messageTextView in your layout

        messageTextView.text = trackingStatus

    }
}