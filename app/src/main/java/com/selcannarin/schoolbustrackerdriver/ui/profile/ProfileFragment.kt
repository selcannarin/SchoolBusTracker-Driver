package com.selcannarin.schoolbustrackerdriver.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.remote.AuthEvents
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentProfileBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import com.selcannarin.schoolbustrackerdriver.util.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityVisible()
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Profile"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        goToEditProfile()

        getUser()
        registerObserver()
        listenToChannels()

        return binding.root
    }

    private fun goToEditProfile() {
        binding.imageViewEditProfile.setOnClickListener {

            val name = binding.userProfileName.text.toString()
            val mail = binding.userEmail.text.toString()
            val license = binding.licensePlateText.text.toString()
            findNavController().navigate(
                R.id.editProfileFragment, EditProfileFragmentArgs(name, license, mail).toBundle()
            )
        }
    }

    private fun getUser() {
        authViewModel.getCurrentUser()
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun registerObserver() {
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.apply {
                    signOutLayout.setOnClickListener {
                        authViewModel.signOut()
                        findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                    }
                }

                val email = user.email ?: ""
                val fullName = ""
                val licensePlate = ""
                val students: List<Int> = emptyList()
                profileViewModel.getDriver(Driver(email, fullName, licensePlate, students))
                profileViewModel.driver.observe(viewLifecycleOwner) { driverState ->
                    when (driverState) {
                        is UiState.Success -> {
                            val driver = driverState.data
                            binding.apply {
                                userProfileName.text = driver.fullName
                                licensePlateText.text = driver.licensePlate
                                userEmail.text = driver.email

                                val imageref =
                                    Firebase.storage.reference.child("photos/${driver.email}.jpg")
                                imageref.downloadUrl.addOnSuccessListener { uri ->
                                    val imageURL = uri.toString()
                                    imageViewUserImage.loadUrl(imageURL)
                                }
                            }
                        }

                        else -> {}
                    }
                }
            } else {
                binding.signOutLayout.setOnClickListener {
                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                }
            }
        }
    }

}
