package com.example.transitrace

import FeedbackFragment
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainFrame : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var toolbar: Toolbar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_frame)

         toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", MODE_PRIVATE)
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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_complaints -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ComplaintsFragment()).commit()
            R.id.nav_feedback -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeedbackFragment()).commit()
            R.id.nav_about -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AboutFragment()).commit()
            R.id.nav_logout -> {
                // Clear session data
                clearSession()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                Log.d("t","logged out")
                return true
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Check if the current fragment is not HomeFragment
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment !is HomeFragment) {
                // Replace current fragment with HomeFragment
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                val navigationView: NavigationView = findViewById(R.id.nav_view)
                navigationView.setCheckedItem(R.id.nav_home)
            } else {
                super.onBackPressed()
            }
        }
    }


    private fun clearSession() {
        // Clear session data (userId in this case)
        val sharedPreferences = getSharedPreferences("com.example.transitrace.PREFERENCE_FILE_KEY", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Remove all data from the SharedPreferences
        editor.apply()
    }


}
