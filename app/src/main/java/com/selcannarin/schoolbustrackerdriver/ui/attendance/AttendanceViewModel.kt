package com.selcannarin.schoolbustrackerdriver.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _studentList = MutableLiveData<UiState<List<Student>>>()
    val studentList: LiveData<UiState<List<Student>>>
        get() = _studentList

    fun getStudentDetailsByNumbers(studentNumbers: List<Int>) = viewModelScope.launch {
        _studentList.value = UiState.Loading
        repository.getStudentDetailsByNumbers(studentNumbers) { _studentList.value = it }
    }
}