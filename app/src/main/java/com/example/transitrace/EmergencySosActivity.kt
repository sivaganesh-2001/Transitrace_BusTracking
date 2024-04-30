package com.example.transitrace
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmergencySosActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var lastClickedTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_sos)

        val accidentTextView: TextView = findViewById(R.id.accident)
        val punctureTextView: TextView = findViewById(R.id.puncture)
        val breakdownTextView: TextView = findViewById(R.id.breakdown)
        val submitButton: Button = findViewById(R.id.button3)
        editText = findViewById(R.id.textbox)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        accidentTextView.setOnClickListener {
            clearAndSetText(accidentTextView.text.toString())
            lastClickedTextView = accidentTextView
        }

        punctureTextView.setOnClickListener {
            clearAndSetText(punctureTextView.text.toString())
            lastClickedTextView = punctureTextView
        }

        breakdownTextView.setOnClickListener {
            clearAndSetText(breakdownTextView.text.toString())
            lastClickedTextView = breakdownTextView
        }

        submitButton.setOnClickListener {
            val emergencyData = editText.text.toString().trim()
            if (emergencyData.isNotEmpty()) {
                val email = auth.currentUser?.email ?: ""
                val currentTime = Timestamp.now()

                // Store the emergencyData in Firestore
                val data = hashMapOf(
                    "emergencyData" to emergencyData,
                    "senderEmail" to email,
                    "timestamp" to currentTime
                )

                firestore.collection("emergency")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
                        editText.setText("")
                        showToast("Data submitted successfully!")
                    }
                    .addOnFailureListener { e ->
                        showToast("Failed to submit data. Please try again.")
                    }
            }
        }
    }

    private fun clearAndSetText(text: String) {
        editText.setText(text)
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
