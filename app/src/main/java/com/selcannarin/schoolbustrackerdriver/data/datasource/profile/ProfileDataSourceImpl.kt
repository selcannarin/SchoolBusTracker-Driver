package com.selcannarin.schoolbustrackerdriver.data.datasource.profile

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.util.UiState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProfileDataSource {

    override suspend fun getDriver(user: Driver, result: (UiState<Driver>) -> Unit) {
        try {
            val document = firestore.collection("drivers").document(user.email).get().await()
            val driver = document.toObject(Driver::class.java)
            driver?.let {
                result.invoke(UiState.Success(it))
            } ?: result.invoke(UiState.Failure("Driver not found"))
        } catch (e: Exception) {
            result.invoke(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }

    override suspend fun editDriver(user: Driver, result: (UiState<String>) -> Unit) {
        try {
            firestore.collection("drivers").document(user.email).set(user).await()
            result.invoke(UiState.Success("Driver information updated"))
        } catch (e: Exception) {
            result.invoke(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }

    override suspend fun getStudentNumberList(user: Driver, result: (UiState<List<Int>>) -> Unit) {
        try {
            val document = firestore.collection("drivers").document(user.email).get().await()
            val driver = document.toObject(Driver::class.java)

            val studentNumbers = driver?.students

            if (studentNumbers != null) {
                result.invoke(UiState.Success(studentNumbers))
            } else {
                result.invoke(UiState.Failure("Student numbers not found"))
            }
        } catch (e: Exception) {
            result.invoke(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }


    override suspend fun addPhoto(user: Driver, photoUri: Uri, result: (UiState<String>) -> Unit) {
        try {
            val photoRef = storage.reference.child("photos/${user.email}.jpg")

            val listResult = photoRef.parent?.listAll()?.await()
            val oldPhoto = listResult?.items?.firstOrNull()

            if (oldPhoto == null) {
                photoRef.putFile(photoUri).await()
                result.invoke(UiState.Success("Photo added successfully"))
            } else {
                oldPhoto.delete().await()
                photoRef.putFile(photoUri).await()
                result.invoke(UiState.Success("Photo added successfully"))
            }
        } catch (e: Exception) {
            result.invoke(UiState.Failure("Error adding photo: ${e.localizedMessage}"))
        }
    }

    override suspend fun uploadFile(
        user: Driver,
        fileUri: Uri,
        result: (UiState<String>) -> Unit
    ) {
        try {
            val fileRef = storage.reference.child("files/${user.email}.jpg")

            val listResult = fileRef.parent?.listAll()?.await()
            val oldFile = listResult?.items?.firstOrNull()

            if (oldFile == null) {
                fileRef.putFile(fileUri).await()
                result.invoke(UiState.Success("File uploaded successfully"))
            } else {
                oldFile.delete().await()
                fileRef.putFile(fileUri).await()
                result.invoke(UiState.Success("File uploaded successfully"))
            }
        } catch (e: Exception) {
            result.invoke(UiState.Failure("Error uploading file: ${e.localizedMessage}"))
        }
    }


}


