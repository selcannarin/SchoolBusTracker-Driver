package com.selcannarin.schoolbustrackerdriver.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentEditProfileBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import com.selcannarin.schoolbustrackerdriver.util.loadUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_FILE_REQUEST = 2
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var selectedFileUri: Uri? = null
    private val driverLiveData: MutableLiveData<Driver> = MutableLiveData()
    private val args: EditProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        val fullName = args.fullName
        val email = args.email
        val licensePlate = args.licensePlate

        binding.editTextUserProfileName.setText(fullName)
        binding.textViewUserEmail.text = email
        binding.editTextLicensePlate.setText(licensePlate)

        val imageref =
            Firebase.storage.reference.child("photos/${email}.jpg")
        imageref.downloadUrl.addOnSuccessListener { Uri ->
            val imageURL = Uri.toString()
            binding.imageViewUserImage.loadUrl(imageURL)
        }

        checkUser()
        initListener()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityGone()
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Edit Profile"


    }

    fun initListener() {

        binding.addPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.licensePlateIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

        binding.imageViewSaveProfile.setOnClickListener {

            driverLiveData.observe(viewLifecycleOwner) { driver ->
                val userEmail = driver.email
                val students = driver.students

                val fullName = binding.editTextUserProfileName.text.toString()
                val licensePlate = binding.editTextLicensePlate.text.toString()

                val updatedDriver = Driver(userEmail, fullName, licensePlate, students)

                profileViewModel.editDriver(updatedDriver)

                if (selectedImageUri != null) {
                    showPhotoConfirmationDialog(updatedDriver, selectedImageUri!!)
                }

                if (selectedFileUri != null) {
                    showFileConfirmationDialog(updatedDriver, selectedFileUri!!)
                }
            }
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

    }

    private fun checkUser() {
        authViewModel.getCurrentUser()
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val email = user.email ?: ""
                val fullName = ""
                val licensePlate = ""
                val students: List<Int> = emptyList()
                profileViewModel.getDriver(Driver(email, fullName, licensePlate, students))
                profileViewModel.driver.observe(viewLifecycleOwner) { driverState ->
                    when (driverState) {
                        is UiState.Success -> {
                            val driver = driverState.data
                            driverLiveData.value = driver
                        }

                        else -> {}
                    }

                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data

            if (selectedImageUri != null) {
                binding.imageViewUserImage.loadUrl(selectedImageUri.toString())
            }
        }

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.data
        }
    }

    private fun showFileConfirmationDialog(driver: Driver, fileUri: Uri) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Upload File")
        alertDialog.setMessage("Are you sure you want to upload the file??")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            profileViewModel.uploadFile(driver, fileUri)
        }
        alertDialog.setNegativeButton("No") { _, _ ->

        }
        alertDialog.show()
    }

    private fun showPhotoConfirmationDialog(driver: Driver, photoUri: Uri) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Profile Photo")
        alertDialog.setMessage("Are you sure about choosing it as your profile photo?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            profileViewModel.addPhoto(driver, photoUri)
        }
        alertDialog.setNegativeButton("No") { _, _ ->

        }
        alertDialog.show()
    }
}