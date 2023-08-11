package com.selcannarin.schoolbustrackerdriver.data.repository.student

import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState

interface StudentRepository {

    suspend fun addStudent(user: Student, result: (UiState<String>) -> Unit)

    suspend fun deleteStudent(user: Student, result: (UiState<String>) -> Unit)

    suspend fun updateStudent(user: Student, result: (UiState<String>) -> Unit)

    suspend fun getStudentDetailsByNumbers(
        studentNumbers: List<Int>,
        result: (UiState<List<Student>>) -> Unit
    )
}