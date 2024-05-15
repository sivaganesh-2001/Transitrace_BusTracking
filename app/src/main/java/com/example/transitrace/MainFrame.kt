package com.example.transitrace

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainFrame : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_frame)

        val toolbar: Toolbar = findViewById(R.id.toolbar) // Ignore red line errors
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment()).commit()
            R.id.nav_complaints->supportFragmentManager.beginTransaction().replace(R.id.fragment_container,ComplaintsFragment()).commit()
            R.id.nav_emergency-> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EmergencyFragment()).commit()
            R.id.nav_about->supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                AboutFragment()
            ).commit()
            R.id.nav_logout -> logoutUser()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun logoutUser() {
        // Clear session data
        clearSession()

        // Navigate to the login screen
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun clearSession() {
        // Clear session data (userId in this case)
        val sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.apply()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}


