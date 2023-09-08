package com.selcannarin.schoolbustrackerdriver.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.notification.MyFirebaseMessagingService
import com.selcannarin.schoolbustrackerdriver.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (!isNetworkConnected()) {
            showNoInternetDialog()
        } else {
            setContentView(binding.root)
            MyFirebaseMessagingService.sharedPref =
                getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            setSupportActionBar(binding.toolbar.toolbar)
            setupNavigation()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showNoInternetDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection. You cannot use this application without internet.")
        alertDialogBuilder.setNegativeButton("CLOSE APPLICATION") { _, _ ->
            finish()
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.navHostFragment)

        if (navController.currentDestination?.id == R.id.signInFragment) {
            binding.bottomNavigationView.visibility = View.GONE
            hideNavigationDrawer()
        } else {
            binding.bottomNavigationView.visibility = View.VISIBLE

        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.attendance -> {
                    navController.navigate(R.id.attendanceFragment)
                    return@setOnItemSelectedListener true
                }

                R.id.location -> {
                    navController.navigate(R.id.locationFragment)
                    return@setOnItemSelectedListener true
                }

                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.toolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.nav_addStudent -> {
                    navController.navigate(R.id.addStudentFragment)
                }

                R.id.nav_attendanceList -> {
                    navController.navigate(R.id.attendanceFragment)
                }

                R.id.nav_location -> {
                    navController.navigate(R.id.locationFragment)
                }

                R.id.nav_profile -> {
                    navController.navigate(R.id.profileFragment)
                }

                R.id.nav_signOut -> {
                    firebaseAuth.signOut()
                    navController.navigate(R.id.signInFragment)
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }

    fun setBottomNavVisibilityGone() {
        binding.bottomNavigationView.isVisible = false
    }

    fun setBottomNavVisibilityVisible() {
        binding.bottomNavigationView.isVisible = true
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun hideNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun showNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.toolbar,
            R.string.open,
            R.string.close
        )

        toggle.isDrawerIndicatorEnabled = true
        toggle.setToolbarNavigationClickListener(null)

        binding.drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
    }

}