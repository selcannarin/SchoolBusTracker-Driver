package com.selcannarin.schoolbustrackerdriver.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
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

        if (navController.currentDestination?.id == R.id.signInFragment) {
            binding.bottomNavigationView.visibility = View.GONE
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
    }

    fun setBottomNavVisibilityGone() {
        binding.bottomNavigationView.isVisible = false
    }


    fun setBottomNavVisibilityVisible() {
        binding.bottomNavigationView.isVisible = true
    }
}
