package com.selcannarin.schoolbustrackerdriver.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentAddStudentBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.ui.profile.ProfileViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStudentFragment : Fragment() {

    private lateinit var binding: FragmentAddStudentBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStudentBinding.bind(view)

        (requireActivity() as MainActivity).setToolbarTitle("Add Student")
        (activity as MainActivity).showNavigationDrawer()

        setupListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStudentBinding.inflate(inflater)
        (activity as MainActivity).showNavigationDrawer()
        return binding.root
    }

    private fun setupListeners() {
        binding?.apply {
            buttonSave.setOnClickListener {

                val name = binding.editTextName.text.toString()
                val number = binding.editTextNumber.text.toString().toInt()
                val parentNumber = binding.editTextParentNumber.text.toString().toLong()
                val address = binding.editTextAddress.text.toString()

                if (name.isEmpty()) {
                    binding.editTextName.error = "Student Name should not be empty"
                    return@setOnClickListener
                }
                if (number == null) {
                    binding.editTextNumber.error = "Student Number should not be empty"
                    return@setOnClickListener
                }
                if (parentNumber == null) {
                    binding.editTextParentNumber.error = "Parent Number should not be empty"
                    return@setOnClickListener
                }
                if (address.isEmpty()) {
                    binding.editTextAddress.error = "Student Address should not be empty"
                    return@setOnClickListener
                }

                val newStudent = Student(number, name, parentNumber.toLong(), address)

                checkUser(newStudent)

            }

            buttonCancel.setOnClickListener {
                findNavController().navigate(R.id.action_addStudentFragment_to_attendanceFragment)
            }

        }
    }

    private fun checkUser(student: Student) {
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
                            addStudent(driver, student)
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

    private fun addStudent(user: Driver, student: Student) {
        attendanceViewModel.addStudent(user, student)
        attendanceViewModel.addStudent.observe(viewLifecycleOwner) { studentState ->
            when (studentState) {
                is UiState.Success -> {
                    val addedStudent = studentState.data
                    Toast.makeText(
                        requireContext(),
                        "The student added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_addStudentFragment_to_attendanceFragment)
                }

                is UiState.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "The student could not be added. Try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }
}

