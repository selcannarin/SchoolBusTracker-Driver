package com.selcannarin.schoolbustrackerdriver.data.repository.student

import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState

interface StudentRepository {

    suspend fun addStudent(user: Driver, student: Student, result: (UiState<String>) -> Unit)

    suspend fun deleteStudent(user: Driver, student: Student, result: (UiState<String>) -> Unit)

    suspend fun updateStudent(user: Driver, student: Student, result: (UiState<String>) -> Unit)

    suspend fun getStudentDetailsByNumbers(
        studentNumbers: List<Int>,
        result: (UiState<List<Student>>) -> Unit
    )
}