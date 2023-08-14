package com.selcannarin.schoolbustrackerdriver.data.datasource.student

import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState

interface StudentDataSource {

    suspend fun addStudent(user: Driver, student: Student, result: (UiState<String>) -> Unit)

    suspend fun deleteStudent(student: Student, result: (UiState<String>) -> Unit)

    suspend fun updateStudent(student: Student, result: (UiState<String>) -> Unit)

    suspend fun getStudentDetailsByNumbers(
        studentNumbers: List<Int>,
        result: (UiState<List<Student>>) -> Unit
    )

}