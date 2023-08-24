package com.selcannarin.schoolbustrackerdriver.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.util.AuthEvents
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentResetPasswordBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResetPasswordBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityGone()
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Reset Password"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityGone()
        setUpWidgets()
        listenToChannels()
        return binding?.root
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_resetPasswordFragment_to_signInFragment)
                    }

                    is AuthEvents.Error -> {
                        binding?.apply {
                            errorText.text = event.error
                        }
                    }

                    is AuthEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding?.apply {
                                editTextResetEmail.error = "Email should not be empty!"
                            }
                    }
                }

            }
        }
    }

    private fun setUpWidgets() {
        binding?.apply {
            buttonResetPassword.setOnClickListener {
                val email = editTextResetEmail.text.toString()
                viewModel.verifySendPasswordReset(email)
            }
            textViewBackToSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_resetPasswordFragment_to_signInFragment)
            }
        }
    }

}