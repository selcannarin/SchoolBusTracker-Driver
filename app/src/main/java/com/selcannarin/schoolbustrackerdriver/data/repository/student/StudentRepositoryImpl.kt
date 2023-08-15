package com.selcannarin.schoolbustrackerdriver.data.repository.student

import com.selcannarin.schoolbustrackerdriver.data.datasource.student.StudentDataSource
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentDataSource: StudentDataSource
) : StudentRepository {

    override suspend fun addStudent(
        user: Driver,
        student: Student,
        result: (UiState<String>) -> Unit
    ) {
        return studentDataSource.addStudent(user, student, result)
    }

    override suspend fun deleteStudent(
        user: Driver,
        student: Student,
        result: (UiState<String>) -> Unit
    ) {
        return studentDataSource.deleteStudent(user, student, result)
    }

    override suspend fun updateStudent(
        user: Driver,
        student: Student,
        result: (UiState<String>) -> Unit
    ) {
        return studentDataSource.updateStudent(user, student, result)
    }

    override suspend fun getStudentDetailsByNumbers(
        studentNumbers: List<Int>,
        result: (UiState<List<Student>>) -> Unit
    ) {
        return studentDataSource.getStudentDetailsByNumbers(studentNumbers, result)
    }

    override suspend fun saveGoingAttendanceList(
        userEmail: String,
        studentNumbers: List<Int>,
        result: (UiState<List<Int>>) -> Unit
    ) {
        return studentDataSource.saveGoingAttendanceList(userEmail, studentNumbers, result)
    }

    override suspend fun saveReturnAttendanceList(
        userEmail: String,
        studentNumbers: List<Int>,
        result: (UiState<List<Int>>) -> Unit
    ) {
        return studentDataSource.saveReturnAttendanceList(userEmail, studentNumbers, result)
    }
}