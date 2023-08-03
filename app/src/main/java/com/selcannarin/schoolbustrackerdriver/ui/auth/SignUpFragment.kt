package com.selcannarin.schoolbustrackerdriver.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.remote.FirebaseEvents
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding
    private val TAG = "SignUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setupListeners()
        listenToChannels()
        return binding?.root
    }

    private fun setupListeners() {
        binding?.apply {
            buttonSignUp.setOnClickListener {
                val driverName = editTextDriverName.text.toString()
                val licensePlate = editTextLicensePlate.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                val confirmPassword = editTextConfirmPassword.text.toString()

                if (driverName.isEmpty()) {
                    editTextDriverName.error = "Driver Name should not be empty"
                    return@setOnClickListener
                }

                if (licensePlate.isEmpty()) {
                    editTextLicensePlate.error = "License Plate should not be empty"
                    return@setOnClickListener
                }

                if (email.isEmpty()) {
                    editTextEmail.error = "Email should not be empty"
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    editTextPassword.error = "Password should not be empty"
                    return@setOnClickListener
                }

                if (confirmPassword.isEmpty()) {
                    editTextConfirmPassword.error = "Confirm Password should not be empty"
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    editTextConfirmPassword.error = "Passwords do not match"
                    return@setOnClickListener
                }

                viewModel.signUpUser(email, password, confirmPassword)
                val driver = Driver(email, driverName, licensePlate)

                viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
                    if (firebaseUser != null) {
                        viewModel.saveUser(driver)
                    }
                }

            }

            textViewSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is FirebaseEvents.Error -> {
                        binding?.apply {
                            textViewErrorSignUp.text = event.error
                        }
                    }
                    is FirebaseEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.d(TAG, "listenToChannels: No event received so far")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
