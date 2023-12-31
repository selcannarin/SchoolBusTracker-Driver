package com.selcannarin.schoolbustrackerdriver.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentSignUpBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.util.AuthEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding
    private val TAG = "SignUpFragment"
    private var isNavigationPerformed = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityGone()
        (requireActivity() as MainActivity).hideNavigationDrawer()
        (requireActivity() as MainActivity).setToolbarTitle("Sign Up")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityGone()
        (requireActivity() as MainActivity).hideNavigationDrawer()
        setupListeners()
        listenToChannels()
        return binding?.root
    }

    private fun setupListeners() {
        binding?.apply {
            buttonSignUp.setOnClickListener {
                val driverName = editTextDriverName.text.toString()
                val driverPhone = editTextDriverPhone.text.toString()
                val licensePlate = editTextLicensePlate.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                val confirmPassword = editTextConfirmPassword.text.toString()

                if (driverName.isEmpty()) {
                    editTextDriverName.error = "Driver Name should not be empty"
                    return@setOnClickListener
                }

                if (driverPhone.isEmpty()) {
                    editTextDriverPhone.error = "Driver Phone should not be empty"
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
                stopAuthStateListener()
                viewModel.signUpUser(email, password, confirmPassword)
                val driver = Driver(email, driverName, licensePlate, null, driverPhone.toLong())

                viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
                    if (firebaseUser != null) {
                        viewModel.saveUser(driver)
                    }
                }

                startAuthStateListener()

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
                    is AuthEvents.Error -> {
                        binding?.apply {
                            textViewErrorSignUp.text = event.error
                        }
                    }

                    is AuthEvents.Message -> {
                        if (event.message == "sign up success" && !isNavigationPerformed) {
                            isNavigationPerformed = true
                            findNavController().navigate(R.id.action_signUpFragment_to_attendanceFragment)
                        }
                    }

                    else -> {
                        Log.d(TAG, "listenToChannels: No event received so far")
                    }
                }
            }
        }
    }

    private fun startAuthStateListener() {
        if (authStateListener == null) {
            authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                Log.i("firebase", "AuthState changed to ${firebaseAuth.currentUser?.uid}")
                if (firebaseAuth.currentUser != null && !isNavigationPerformed) {
                    isNavigationPerformed = true
                    findNavController().navigate(R.id.action_signUpFragment_to_attendanceFragment)
                }
            }
            firebaseAuth.addAuthStateListener(authStateListener!!)
        }
    }

    private fun stopAuthStateListener() {
        authStateListener?.let { firebaseAuth.removeAuthStateListener(it) }
    }

    override fun onResume() {
        super.onResume()
        startAuthStateListener()
        (requireActivity() as MainActivity).setBottomNavVisibilityGone()
        (requireActivity() as MainActivity).hideNavigationDrawer()
    }

    override fun onPause() {
        super.onPause()
        stopAuthStateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
