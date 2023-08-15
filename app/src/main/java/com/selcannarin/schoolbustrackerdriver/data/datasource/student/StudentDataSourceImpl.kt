package com.selcannarin.schoolbustrackerdriver.data.datasource.student

import com.google.firebase.firestore.FirebaseFirestore
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.util.UiState
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

class StudentDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentDataSource {

    override suspend fun addStudent(
        user: Driver,
        student: Student,
        result: (UiState<String>) -> Unit
    ) {
        try {
            val document = firestore.collection("drivers").document(user.email).get().await()
            val driver = document.toObject(Driver::class.java)

            if (driver != null) {
                val updatedStudents = if (driver.students == null) {
                    listOf(student.student_number)
                } else {
                    driver.students!! + student.student_number
                }

                driver.students = updatedStudents

                firestore.collection("drivers")
                    .document(user.email)
                    .set(driver)
                    .addOnSuccessListener {
                        val studentData = hashMapOf(
                            "student_number" to student.student_number,
                            "student_name" to student.student_name,
                            "parent_phone_number" to student.parent_phone_number,
                            "student_address" to student.student_address
                        )

                        firestore.collection("students")
                            .document(student.student_number.toString())
                            .set(studentData)
                            .addOnSuccessListener {
                                result(UiState.Success("Student added successfully"))
                            }
                            .addOnFailureListener { e ->
                                result(
                                    UiState.Failure(
                                        e.localizedMessage ?: "Failed to add student"
                                    )
                                )
                            }
                    }
                    .addOnFailureListener { e ->
                        result(UiState.Failure(e.localizedMessage ?: "Failed to update driver"))
                    }
            } else {
                result(UiState.Failure("Driver not found"))
            }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }

    override suspend fun deleteStudent(
        user: Driver,
        student: Student,
        result: (UiState<String>) -> Unit
    ) {
        try {
            val document = firestore.collection("drivers").document(user.email).get().await()
            val driver = document.toObject(Driver::class.java)

            if (driver != null) {
                val updatedStudents = driver.students?.filter { it != student.student_number }

                driver.students = updatedStudents

                firestore.collection("drivers")
                    .document(user.email)
                    .set(driver)
                    .addOnSuccessListener {
                        firestore.collection("students")
                            .document(student.student_number.toString())
                            .delete()
                            .addOnSuccessListener {
                                result(UiState.Success("Student deleted successfully"))
                            }
                            .addOnFailureListener { e ->
                                result(
                                    UiState.Failure(
                                        e.localizedMessage ?: "Failed to delete student"
                                    )
                                )
                            }
                    }
                    .addOnFailureListener { e ->
                        result(UiState.Failure(e.localizedMessage ?: "Failed to update driver"))
                    }
            } else {
                result(UiState.Failure("Driver not found"))
            }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }

    override suspend fun updateStudent(
        user: Driver,
        student: Student,
        result: (UiState<String>) -> Unit
    ) {
        try {
            val document = firestore.collection("drivers").document(user.email).get().await()
            val driver = document.toObject(Driver::class.java)

            if (driver != null) {
                val updatedStudents = driver.students?.map {
                    if (it == student.student_number) {
                        student.student_number
                    } else {
                        it
                    }
                }

                driver.students = updatedStudents

                firestore.collection("drivers")
                    .document(user.email)
                    .set(driver)
                    .addOnSuccessListener {
                        val studentData = hashMapOf(
                            "student_number" to student.student_number,
                            "student_name" to student.student_name,
                            "parent_phone_number" to student.parent_phone_number,
                            "student_address" to student.student_address
                        )

                        firestore.collection("students")
                            .document(student.student_number.toString())
                            .set(studentData)
                            .addOnSuccessListener {
                                result(UiState.Success("Student updated successfully"))
                            }
                            .addOnFailureListener { e ->
                                result(
                                    UiState.Failure(
                                        e.localizedMessage ?: "Failed to update student"
                                    )
                                )
                            }
                    }
                    .addOnFailureListener { e ->
                        result(UiState.Failure(e.localizedMessage ?: "Failed to update driver"))
                    }
            } else {
                result(UiState.Failure("Driver not found"))
            }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
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

    override suspend fun saveGoingAttendanceList(
        userEmail: String,
        studentNumbers: List<Int>,
        result: (UiState<List<Int>>) -> Unit
    ) {
        val attendanceCollection = firestore.collection("attendance")
        val userAttendanceDoc = attendanceCollection.document(userEmail)

        val currentDate = Calendar.getInstance().time
        val day = currentDate.date
        val month = currentDate.month
        val year = currentDate.year

        val attendanceDate = "${day}_${month}_${year}"

        val goingAttendanceData = hashMapOf(
            "timestamp" to currentDate,
            "students" to studentNumbers
        )

        val goingCollection = userAttendanceDoc.collection("going")
        val attendanceDoc = goingCollection.document(attendanceDate)

        try {
            attendanceDoc.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    attendanceDoc.delete().await()
                }

                goingCollection.document(attendanceDate).set(goingAttendanceData).await()

                result(UiState.Success(studentNumbers))
            }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "Failed to save attendance"))
        }
    }


    override suspend fun saveReturnAttendanceList(
        userEmail: String,
        studentNumbers: List<Int>,
        result: (UiState<List<Int>>) -> Unit
    ) {
        val attendanceCollection = firestore.collection("attendance")
        val userAttendanceDoc = attendanceCollection.document(userEmail)

        val currentDate = Calendar.getInstance().time
        val day = currentDate.date
        val month = currentDate.month
        val year = currentDate.year

        val attendanceDate = "${day}_${month}_${year}"

        val returnAttendanceData = hashMapOf(
            "timestamp" to currentDate,
            "students" to studentNumbers
        )

        val returnCollection = userAttendanceDoc.collection("return")
        val attendanceDoc = returnCollection.document(attendanceDate)

        try {
            attendanceDoc.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {

                    attendanceDoc.delete().await()
                }


                returnCollection.document(attendanceDate).set(returnAttendanceData).await()

                result(UiState.Success(studentNumbers))
            }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "Failed to save attendance"))
        }
    }

}