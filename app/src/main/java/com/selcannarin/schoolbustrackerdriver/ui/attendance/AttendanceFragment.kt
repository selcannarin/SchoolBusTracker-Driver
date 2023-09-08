package com.selcannarin.schoolbustrackerdriver.ui.attendance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.NotificationData
import com.selcannarin.schoolbustrackerdriver.data.model.PushNotification
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.data.notification.MyFirebaseMessagingService.Companion.token
import com.selcannarin.schoolbustrackerdriver.data.notification.RetrofitInstance
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentAttendanceBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.ui.profile.ProfileViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding
    private lateinit var adapter: AttendanceAdapter
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private val driverLiveData: MutableLiveData<Driver> = MutableLiveData()
    val TAG = "Attendance"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceBinding.inflate(inflater)
        (activity as MainActivity).showNavigationDrawer()

        binding.attendanceRv.setHasFixedSize(true)
        binding.attendanceRv.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setBottomNavVisibilityVisible()

        (requireActivity() as MainActivity).setToolbarTitle("Student Attendance")
        (activity as MainActivity).showNavigationDrawer()
        onBackPressed()
        checkUser()
        saveStudentAttendanceList()

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
                            driverLiveData.value = driver
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
        adapter = AttendanceAdapter(studentList) { student ->
            goToStudentDetail(student)
        }
        if (adapter.itemCount == 0) {
            binding.progressCircular.visibility = View.GONE
            binding.textViewNoStudent.text = "There no student. Please add student."
        }
        binding.attendanceRv.adapter = adapter
    }

    private fun goToStudentDetail(student: Student) {
        val name = student.student_name
        val number = student.student_number
        val parent_phone = student.parent_phone_number
        val address = student.student_address

        findNavController().navigate(
            R.id.studentDetailFragment,
            StudentDetailFragmentArgs(name, number, parent_phone, address).toBundle()
        )
    }

    private fun addStudent() {
        findNavController().navigate(R.id.action_attendanceFragment_to_addStudentFragment)

    }

    private fun onGoingButtonClicked() {
        driverLiveData.observe(viewLifecycleOwner) { driver ->
            val userEmail = driver.email
            val selectedStudents = getStudentAttendanceList()
            attendanceViewModel.saveGoingAttendanceList(userEmail, selectedStudents)
            attendanceViewModel.goingAttendanceList.observe(viewLifecycleOwner) { goingAttendanceList ->
                when (goingAttendanceList) {
                    is UiState.Success -> {
                        val studentNumberList = goingAttendanceList.data
                        val absentStudentList = getAbsentStudentList(studentNumberList)

                        setNotificationDetails(
                            absentStudentList,
                            "The student did not get on the outgoing bus.",
                            "Absent Student - School Bus Tracker"
                        )
                        Toast.makeText(
                            requireContext(),
                            "Going Attendance List saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("going", studentNumberList.toString())
                    }

                    else -> {}
                }
            }
        }

    }

    private fun onReturnButtonClicked() {
        driverLiveData.observe(viewLifecycleOwner) { driver ->
            val userEmail = driver.email
            val selectedStudents = getStudentAttendanceList()
            attendanceViewModel.saveReturnAttendanceList(userEmail, selectedStudents)
            attendanceViewModel.returnAttendanceList.observe(viewLifecycleOwner) { returnAttendanceList ->
                when (returnAttendanceList) {
                    is UiState.Success -> {
                        val studentNumberList = returnAttendanceList.data
                        val absentStudentList = getAbsentStudentList(studentNumberList)

                        setNotificationDetails(
                            absentStudentList,
                            "The student did not take the return bus.",
                            "Absent Student - School Bus Tracker"
                        )
                        Toast.makeText(
                            requireContext(),
                            "Return Attendance List saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    else -> {}
                }
            }
        }
    }

    private fun getStudentAttendanceList(): List<Int> {
        val selectedStudents = mutableListOf<Int>()

        val itemCount = binding.attendanceRv.adapter?.itemCount ?: 0
        for (i in 0 until itemCount) {
            val viewHolder =
                binding.attendanceRv.findViewHolderForAdapterPosition(i) as AttendanceAdapter.ViewHolder
            val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.attendance_checkBox)

            if (checkBox.isChecked) {
                val studentNumberTextView =
                    viewHolder.itemView.findViewById<TextView>(R.id.textView_student_number)
                val studentNumber = studentNumberTextView.text.toString().toInt()
                selectedStudents.add(studentNumber)
            }
        }
        return selectedStudents
    }

    private fun saveStudentAttendanceList() {
        binding.buttonSave.setOnClickListener {

            val isGoingChecked = binding.buttonGoing.isChecked
            val isReturnChecked = binding.buttonReturn.isChecked

            if (isGoingChecked || isReturnChecked) {
                if (isGoingChecked) {
                    onGoingButtonClicked()
                }
                if (isReturnChecked) {
                    onReturnButtonClicked()
                }
            }
        }
    }

    private fun getAbsentStudentList(studentList: List<Int>): List<Int> {
        val absentStudents = mutableListOf<Int>()
        driverLiveData.observe(viewLifecycleOwner) { driver ->
            val driverStudentNumbers = driver.students
            if (driverStudentNumbers != null) {
                for (studentNumber in driverStudentNumbers) {
                    if (!studentList.contains(studentNumber)) {
                        absentStudents.add(studentNumber)
                    }
                }
            }
        }
        return absentStudents
    }

    private fun setNotificationDetails(studentList: List<Int>, message: String, title: String) {
        for (studentNumber in studentList) {
            attendanceViewModel.getFCMTokenByStudentNumber(studentNumber)
            attendanceViewModel.getFCMToken.observe(viewLifecycleOwner) { fcmToken ->
                when (fcmToken) {
                    is UiState.Success -> {
                        if (fcmToken.data != null) {
                            token = fcmToken.data
                            token?.let { parentToken ->
                                val notificationTitle = title
                                val notificationMessage = message
                                PushNotification(
                                    NotificationData(
                                        notificationTitle,
                                        notificationMessage
                                    ),
                                    parentToken
                                ).also {
                                    sendNotification(it)
                                }
                            }
                        } else {
                            Log.e(TAG, "Null fcm token")
                        }
                    }

                    is UiState.Loading -> {
                        Log.d(TAG, "Setting notification details.")
                    }

                    is UiState.Failure -> {
                        Log.e(TAG, "Failed to set notification details.")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    token = ""
                    Log.d(TAG, "Response: ${Gson().toJson(response)}")

                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

}




