import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.transitrace.R
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackFragment : Fragment() {

    private lateinit var feedbackEditText: EditText
    private lateinit var ratingBar: RatingBar

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_feedback, container, false)

        // Initialize UI elements
        feedbackEditText = view.findViewById(R.id.editTextTextMultiLine)
        ratingBar = view.findViewById(R.id.ratingBar)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        val clearButton: Button = view.findViewById(R.id.button2)
        val submitButton: Button = view.findViewById(R.id.button)

        // Set click listener for the clear button
        clearButton.setOnClickListener {
            clearFeedbackFields()
        }

        // Set click listener for the submit button
        submitButton.setOnClickListener {
            saveFeedbackToFirestore()
        }

        return view
    }

    private fun clearFeedbackFields() {
        feedbackEditText.setText("")
        ratingBar.rating = 0f
    }

    private fun saveFeedbackToFirestore() {
        val feedbackText = feedbackEditText.text.toString().trim()
        val rating = ratingBar.rating.toDouble()

        if (feedbackText.isNotEmpty()) {
            // Retrieve the email ID from SharedPreferences
            val email = getEmailFromSharedPreferences()

            // Ensure email is not empty
            if (email.isNotEmpty()) {
                // Create a new feedback document
                val feedbackData = hashMapOf(
                    "email" to email,
                    "feedback" to feedbackText,
                    "rating" to rating
                )

                // Save the feedback document to Firestore
                firestore.collection("feedback")
                    .add(feedbackData)
                    .addOnSuccessListener {
                        // Feedback saved successfully
                        Toast.makeText(requireContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
                        clearFeedbackFields() // Clear the fields after successful submission
                    }
                    .addOnFailureListener { e ->
                        // Handle errors while saving feedback
                        Toast.makeText(requireContext(), "Failed to submit feedback. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Show an error message if email is empty
                Toast.makeText(requireContext(), "Email not found. Please login again.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Show an error message if feedback text is empty
            Toast.makeText(requireContext(), "Please enter your feedback.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEmailFromSharedPreferences(): String {
        return sharedPreferences.getString("email", "") ?: ""
    }
}
