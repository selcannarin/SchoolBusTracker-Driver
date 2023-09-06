package com.selcannarin.schoolbustrackerdriver.ui.attendance

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentStudentDetailBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.ui.profile.ProfileViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDetailFragment : Fragment() {

    private lateinit var binding: FragmentStudentDetailBinding
    private val args: StudentDetailFragmentArgs by navArgs()
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentDetailBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityVisible()

        (requireActivity() as MainActivity).setToolbarTitle("Student Detail")
        (activity as MainActivity).showNavigationDrawer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentDetailBinding.inflate(inflater)
        (activity as MainActivity).showNavigationDrawer()
        val name = args.studentName
        val parent_phone = args.studentParentPhone
        val number = args.studentNumber
        val address = args.studentAddress

        with(binding) {
            textViewStudentName.text = name
            textViewParentNumber.text = parent_phone.toString()
            textViewStudentAddress.text = address
            textViewStudentNumber.text = number.toString()
        }

        initListener()

        return binding.root
    }

    private fun initListener() {

        with(binding) {

            removeStudentLayout.setOnClickListener {

                val name = textViewStudentName.text.toString()
                val number = textViewStudentNumber.text.toString().toInt()
                val parentNumber = textViewParentNumber.text.toString().toLong()
                val address = textViewStudentAddress.text.toString()

                val student = Student(number, name, parentNumber, address)
                checkUserforDelete(student)
            }
            imageViewEditStudent.setOnClickListener {

                editTextStudentName.setText(textViewStudentName.text)
                textViewStudentName.visibility = View.GONE
                editTextStudentName.visibility = View.VISIBLE

                editTextParentNumber.setText(textViewParentNumber.text)
                textViewParentNumber.visibility = View.GONE
                editTextParentNumber.visibility = View.VISIBLE

                editTextStudentAddress.setText(textViewStudentAddress.text)
                textViewStudentAddress.visibility = View.GONE
                editTextStudentAddress.visibility = View.VISIBLE

                imageViewEditStudent.visibility = View.GONE
                imageViewSaveStudent.visibility = View.VISIBLE

                imageViewParentPhone.visibility = View.GONE
                removeStudentLayout.visibility = View.GONE


            }
            imageViewSaveStudent.setOnClickListener {
                val newName = editTextStudentName.text.toString()
                val number = textViewStudentNumber.text.toString().toInt()
                val newParentNumber = editTextParentNumber.text.toString().toLong()
                val newAddress = editTextStudentAddress.text.toString()

                val updatedStudent = Student(number, newName, newParentNumber, newAddress)

                checkUserforUpdate(updatedStudent)
            }

            imageViewParentPhone.setOnClickListener {
                val phoneNumber = textViewParentNumber.text.toString()
                dialPhoneNumber(phoneNumber)
            }

        }


    }

    private fun checkUserforDelete(student: Student) {
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
                            deleteStudent(driver, student)
                        }

                        is UiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "User not found. Please login!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun deleteStudent(user: Driver, student: Student) {
        attendanceViewModel.deleteStudent(user, student)
        attendanceViewModel.deleteStudent.observe(viewLifecycleOwner) { deleteState ->
            when (deleteState) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Student deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_studentDetailFragment_to_attendanceFragment)
                }

                is UiState.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete student",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun checkUserforUpdate(student: Student) {
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
                            updateStudent(driver, student)
                        }

                        is UiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "User not found. Please login!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateStudent(user: Driver, student: Student) {
        attendanceViewModel.updateStudent(user, student)
        attendanceViewModel.updateStudent.observe(viewLifecycleOwner) { updatedState ->
            when (updatedState) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Student updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_studentDetailFragment_to_attendanceFragment)
                }

                is UiState.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update student",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

}