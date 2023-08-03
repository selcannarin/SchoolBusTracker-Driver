package com.selcannarin.schoolbustrackerdriver.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.navHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val visibleDestinations = setOf(
                R.id.profileFragment,
                R.id.attendanceFragment,
                R.id.parentsFragment
            )

            binding.bottomNavigationView.visibility = if (destination.id in visibleDestinations) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.attendance -> {
                    navController.navigate(R.id.attendanceFragment)
                    true
                }
                R.id.parents -> {
                    navController.navigate(R.id.parentsFragment)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> {
                    navController.navigate(R.id.attendance)
                    true
                }
            }
        }
    }
}
