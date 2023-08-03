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
import com.selcannarin.schoolbustrackerdriver.data.remote.FirebaseEvents
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding
    private val TAG = "SignInFragment"

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
                    is FirebaseEvents.Error -> {
                        binding?.apply {
                            textViewErrorSignIn.text = event.error
                        }
                    }
                    is FirebaseEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseEvents.ErrorCode -> {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
