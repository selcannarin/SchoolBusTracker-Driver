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
import androidx.recyclerview.widget.LinearLayoutManager
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentAttendanceBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.ui.profile.ProfileViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding
    lateinit var adapter: AttendanceAdapter
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceBinding.inflate(inflater)

        binding.attendanceRv.setHasFixedSize(true)
        binding.attendanceRv.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setBottomNavVisibilityVisible()

        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Student Attendance"

        checkUser()

        binding.fabAddStudent.setOnClickListener {
            addStudent()
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
                            getStudentNumberList(driver)
                        }

                        else -> {}
                    }

                }
            }
        }
    }

    private fun getStudentNumberList(driver: Driver) {
        profileViewModel.getStudentNumberList(driver)
        profileViewModel.studentNumberList.observe(viewLifecycleOwner) { studentNumberList ->

            when (studentNumberList) {
                is UiState.Success -> {
                    val studentNumberList = studentNumberList.data
                    getStudentInfoList(studentNumberList)
                }

                else -> {}
            }
        }


    }

    private fun getStudentInfoList(studentNumberList: List<Int>) {
        attendanceViewModel.getStudentDetailsByNumbers(studentNumberList)
        attendanceViewModel.studentList.observe(viewLifecycleOwner) { studentList ->

            when (studentList) {
                is UiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    val studentInfoList = studentList.data
                    initRecycler(studentInfoList)
                }

                is UiState.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }

                else -> {
                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(context, "No registered students!", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun initRecycler(studentList: List<Student>) {
        adapter = AttendanceAdapter(studentList)
        binding.attendanceRv.adapter = adapter
    }

    private fun addStudent() {
        findNavController().navigate(R.id.action_attendanceFragment_to_addStudentFragment)

    }
}





