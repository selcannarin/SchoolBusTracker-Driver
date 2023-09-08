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
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentSignInBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.util.AuthEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding
    private val TAG = "SignInFragment"
    private var isNavigationPerformed = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)
        (requireActivity() as MainActivity).setToolbarTitle("Sign In")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupListeners()
        listenToChannels()
        return binding?.root
    }

    private fun setupListeners() {
        binding?.apply {
            buttonSignIn.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (email.isEmpty()) {
                    editTextEmail.error = "Email should not be empty"
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    editTextPassword.error = "Password should not be empty"
                    return@setOnClickListener
                }

                viewModel.signInUser(email, password)
            }

            textViewSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }

            textViewForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
            }

        }
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthEvents.Error -> {
                        binding?.apply {
                            textViewErrorSignIn.text = event.error
                        }
                    }

                    is AuthEvents.Message -> {
                        if (event.message == "login success" && !isNavigationPerformed) {
                            isNavigationPerformed = true
                            findNavController().navigate(R.id.action_signInFragment_to_attendanceFragment)
                        }
                    }

                    is AuthEvents.ErrorCode -> {
                        binding?.apply {
                            if (event.code == 1) {
                                editTextEmail.error = "Email should not be empty"
                            }
                            if (event.code == 2) {
                                editTextPassword.error = "Password should not be empty"
                            }
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
                    findNavController().navigate(R.id.action_signInFragment_to_attendanceFragment)
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
