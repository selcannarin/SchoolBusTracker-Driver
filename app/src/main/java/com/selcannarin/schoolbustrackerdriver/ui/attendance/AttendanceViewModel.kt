package com.selcannarin.schoolbustrackerdriver.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.data.repository.student.StudentRepository
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _addStudent = MutableLiveData<UiState<String>>()
    val addStudent: LiveData<UiState<String>>
        get() = _addStudent

    private val _deleteStudent = MutableLiveData<UiState<String>>()
    val deleteStudent: LiveData<UiState<String>>
        get() = _deleteStudent

    private val _updateStudent = MutableLiveData<UiState<String>>()
    val updateStudent: LiveData<UiState<String>>
        get() = _updateStudent

    private val _studentList = MutableLiveData<UiState<List<Student>>>()
    val studentList: LiveData<UiState<List<Student>>>
        get() = _studentList

    private val _returnAttendanceList = MutableLiveData<UiState<List<Int>>>()
    val returnAttendanceList: LiveData<UiState<List<Int>>>
        get() = _returnAttendanceList

    private val _goingAttendanceList = MutableLiveData<UiState<List<Int>>>()
    val goingAttendanceList: LiveData<UiState<List<Int>>>
        get() = _goingAttendanceList

    private val _getFCMToken = MutableLiveData<UiState<String?>>()
    val getFCMToken: LiveData<UiState<String?>>
        get() =_getFCMToken

    fun getStudentDetailsByNumbers(studentNumbers: List<Int>) = viewModelScope.launch {
        _studentList.value = UiState.Loading
        repository.getStudentDetailsByNumbers(studentNumbers) { _studentList.value = it }
    }

    fun addStudent(user: Driver, student: Student) = viewModelScope.launch {
        _addStudent.value = UiState.Loading
        repository.addStudent(user, student) { _addStudent.value = it }
    }

    fun deleteStudent(user: Driver, student: Student) = viewModelScope.launch {
        _deleteStudent.value = UiState.Loading
        repository.deleteStudent(user, student) { _deleteStudent.value = it }
    }

    fun updateStudent(user: Driver, student: Student) = viewModelScope.launch {
        _updateStudent.value = UiState.Loading
        repository.updateStudent(user, student) { _updateStudent.value = it }
    }

    fun saveGoingAttendanceList(
        userEmail: String,
        studentNumbers: List<Int>
    ) = viewModelScope.launch {
        _goingAttendanceList.value = UiState.Loading
        repository.saveGoingAttendanceList(userEmail, studentNumbers) {
            _goingAttendanceList.value = it
        }
    }

    fun saveReturnAttendanceList(
        userEmail: String,
        studentNumbers: List<Int>
    ) = viewModelScope.launch {
        _returnAttendanceList.value = UiState.Loading
        repository.saveReturnAttendanceList(userEmail, studentNumbers) {
            _returnAttendanceList.value = it
        }
    }

    fun getFCMTokenByStudentNumber(studentNumber: Int) = viewModelScope.launch {
        _getFCMToken.value = UiState.Loading
        repository.getFCMTokenByStudentNumber(studentNumber) { _getFCMToken.value = it }
    }
}