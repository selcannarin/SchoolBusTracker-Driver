package com.selcannarin.schoolbustrackerdriver.data.repository.student

import com.selcannarin.schoolbustrackerdriver.data.datasource.student.StudentDataSource
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentDataSource: StudentDataSource
) : StudentRepository {

    override suspend fun addStudent(user: Student, result: (UiState<String>) -> Unit) {
        return studentDataSource.addStudent(user, result)
    }

    override suspend fun deleteStudent(user: Student, result: (UiState<String>) -> Unit) {
        return studentDataSource.deleteStudent(user, result)
    }

    override suspend fun updateStudent(user: Student, result: (UiState<String>) -> Unit) {
        return studentDataSource.updateStudent(user, result)
    }

    override suspend fun getStudentDetailsByNumbers(
        studentNumbers: List<Int>,
        result: (UiState<List<Student>>) -> Unit
    ) {
        return studentDataSource.getStudentDetailsByNumbers(studentNumbers, result)
    }
}