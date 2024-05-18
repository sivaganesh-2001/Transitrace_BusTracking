package com.example.transitrace

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", MODE_PRIVATE)

        val editTextFrom = view.findViewById<EditText>(R.id.editTextFrom)
        val editTextTo = view.findViewById<EditText>(R.id.editTextTo)
        val buttonSubmit = view.findViewById<Button>(R.id.buttonTrack)
        val buttonReset = view.findViewById<Button>(R.id.buttonReset)

        buttonSubmit.setOnClickListener {
            // Validation check
            if (editTextFrom.text.toString().trim().isEmpty()) {
                editTextFrom.error = "Please enter a starting location"
            } else if (editTextTo.text.toString().trim().isEmpty()) {
                editTextTo.error = "Please enter a destination"
            } else {
                navigateToBusListActivity(editTextFrom.text.toString().trim().lowercase(), editTextTo.text.toString().trim().lowercase())
            }
        }

        buttonReset.setOnClickListener {
            resetFields(editTextFrom, editTextTo)
        }

        return view
    }

//    private fun showProfileMenu() {
//        val profile = requireView().findViewById<ImageView>(R.id.profile)
//        val popupMenu = PopupMenu(requireContext(), profile)
//        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
//        popupMenu.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.sos -> {
//                    startActivity(Intent(requireContext(), EmergencySosActivity::class.java))
//                    true
//                }
//                R.id.feedback -> {
//                    startActivity(Intent(requireContext(), UserFeedback::class.java))
//                    true
//                }
//                R.id.complaint -> {
//                    startActivity(Intent(requireContext(), UserComplaintActivity::class.java))
//                    true
//                }
//                R.id.logout -> {
//                    val editor = sharedPreferences.edit()
//                    editor.clear()
//                    editor.apply()
//                    startActivity(Intent(requireContext(), LoginActivity::class.java))
//                    requireActivity().finish()
//                    true
//                }
//                else -> false
//            }
//        }
//        popupMenu.show()
//    }

    private fun navigateToBusListActivity(from: String, to: String) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("from", from)
        intent.putExtra("to", to)
        startActivity(intent)
    }

    private fun resetFields(editTextFrom: EditText, editTextTo: EditText) {
        editTextFrom.text.clear()
        editTextTo.text.clear()
    }
}
