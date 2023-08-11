package com.selcannarin.schoolbustrackerdriver.data.datasource.profile


import android.net.Uri
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.util.UiState

interface ProfileDataSource {

    suspend fun getDriver(user: Driver, result: (UiState<Driver>) -> Unit)

    suspend fun editDriver(user: Driver, result: (UiState<String>) -> Unit)

    suspend fun getStudentNumberList(user:Driver, result: (UiState<List<Int>>) -> Unit )

    suspend fun addPhoto(user: Driver, photoUri: Uri, result: (UiState<String>) -> Unit)

    suspend fun uploadFile(user: Driver, fileUri: Uri, result: (UiState<String>) -> Unit)
}