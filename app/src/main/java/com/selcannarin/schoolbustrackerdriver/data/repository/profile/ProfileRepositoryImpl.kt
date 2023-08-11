package com.selcannarin.schoolbustrackerdriver.data.repository.profile

import android.net.Uri
import com.selcannarin.schoolbustrackerdriver.data.datasource.profile.ProfileDataSource
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.util.UiState
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDataSource: ProfileDataSource
) : ProfileRepository {
    override suspend fun getDriver(user: Driver, result: (UiState<Driver>) -> Unit) {
        return profileDataSource.getDriver(user, result)
    }

    override suspend fun editDriver(user: Driver, result: (UiState<String>) -> Unit) {
        return profileDataSource.editDriver(user, result)
    }

    override suspend fun getStudentNumberList(user: Driver, result: (UiState<List<Int>>) -> Unit) {
        return profileDataSource.getStudentNumberList(user, result)
    }

    override suspend fun addPhoto(user: Driver, photoUri: Uri, result: (UiState<String>) -> Unit) {
        return profileDataSource.addPhoto(user, photoUri, result)
    }

    override suspend fun uploadFile(user: Driver, fileUri: Uri, result: (UiState<String>) -> Unit) {
        return profileDataSource.uploadFile(user, fileUri, result)
    }

}