package com.selcannarin.schoolbustrackerdriver.data.datasource.student

import com.google.firebase.firestore.FirebaseFirestore
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentDataSource {

    override suspend fun addStudent(student: Student, result: (UiState<String>) -> Unit) {
        val studentData = hashMapOf(
            "student_number" to student.student_number,
            "student_name" to student.student_name,
            "student_status" to student.student_status,
            "parent_phone_number" to student.parent_phone_number,
            "student_adress" to student.student_adress
        )

        firestore.collection("students")
            .document(student.student_number.toString())
            .set(studentData)
            .addOnSuccessListener {
                result(UiState.Success("Student added successfully"))
            }
            .addOnFailureListener { e ->
                result(UiState.Failure(e.localizedMessage ?: "Failed to add student"))
            }
    }

    override suspend fun deleteStudent(user: Student, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun updateStudent(user: Student, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun getStudentDetailsByNumbers(
        studentNumbers: List<Int>,
        result: (UiState<List<Student>>) -> Unit
    ) {
        try {
            val students = mutableListOf<Student>()

            for (studentNumber in studentNumbers) {
                val document =
                    firestore.collection("students").document(studentNumber.toString()).get()
                        .await()
                val student = document.toObject(Student::class.java)
                student?.let {
                    it.student_number = studentNumber
                    students.add(it)
                }
            }

            result.invoke(UiState.Success(students))
        } catch (e: Exception) {
            result.invoke(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }


}