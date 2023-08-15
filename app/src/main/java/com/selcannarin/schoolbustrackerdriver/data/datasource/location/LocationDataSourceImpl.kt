package com.selcannarin.schoolbustrackerdriver.data.datasource.location

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Location
import com.selcannarin.schoolbustrackerdriver.util.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class LocationDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LocationDataSource {

    override suspend fun addLocationToFirebase(
        user: Driver,
        location: Location,
        result: (UiState<String>) -> Unit
    ) {
        val currentUserEmail = user.email

        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to FieldValue.serverTimestamp()
        )

        val userDocumentRef = firestore.collection("locations").document(currentUserEmail)

        val timestamp = locationData["timestamp"] as? Timestamp
        val timestampMillis = timestamp?.toDate()?.time ?: System.currentTimeMillis()
        val formattedDate = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date(timestampMillis))
        userDocumentRef.collection("timestamps").document(formattedDate)
            .set(locationData)
            .addOnSuccessListener {
                result(UiState.Success("Location added successfully"))
            }
            .addOnFailureListener { exception ->
                result(UiState.Failure(exception.message ?: "An error occurred"))
            }
    }
}