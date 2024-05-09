
package com.example.transitrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fromuser = intent.getStringExtra("from") ?: ""
        val touser = intent.getStringExtra("to") ?: ""



        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = BlankFragment().apply {
            arguments = Bundle().apply {
                putString("from", fromuser)
                putString("to", touser)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()


    }
}
