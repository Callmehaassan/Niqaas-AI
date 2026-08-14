package com.nikaas.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nikaas.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentDestinationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)

        // Link Navigation Component with BottomNavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
            invalidateOptionsMenu()
            when (destination.id) {
                R.id.loginFragment -> {
                    binding.toolbar.visibility = android.view.View.GONE
                    binding.bottomNavigation.visibility = android.view.View.GONE
                }
                R.id.citizenReportFragment -> {
                    binding.toolbar.visibility = android.view.View.VISIBLE
                    binding.toolbar.title = "Nikaas Citizen Portal"
                    binding.bottomNavigation.visibility = android.view.View.GONE
                }
                R.id.dashboardFragment -> {
                    binding.toolbar.visibility = android.view.View.VISIBLE
                    binding.toolbar.title = "Nikaas Control Panel"
                    binding.bottomNavigation.visibility = android.view.View.GONE
                }
                R.id.incidentDetailFragment -> {
                    binding.toolbar.visibility = android.view.View.GONE
                    binding.bottomNavigation.visibility = android.view.View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        if (currentDestinationId == R.id.loginFragment) {
            return false
        }
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_sign_out) {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.loginFragment)
            android.widget.Toast.makeText(this, "Logged out successfully!", android.widget.Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
