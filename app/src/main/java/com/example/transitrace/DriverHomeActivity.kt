package com.example.transitrace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class DriverHomeActivity : AppCompatActivity() {

    private lateinit var fromEditText: EditText
    private lateinit var toEditText: EditText
    private lateinit var trackButton: Button
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_home)

        fromEditText = findViewById(R.id.from)
        toEditText = findViewById(R.id.to)
        trackButton = findViewById(R.id.buttonTrack)
        resetButton = findViewById(R.id.buttonReset)

        trackButton.setOnClickListener {
            val from = fromEditText.text.toString().trim()
            val to = toEditText.text.toString().trim()

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
}
