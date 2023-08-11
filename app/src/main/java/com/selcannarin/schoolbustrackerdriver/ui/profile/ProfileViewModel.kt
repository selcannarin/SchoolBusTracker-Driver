package com.selcannarin.schoolbustrackerdriver.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.repository.profile.ProfileRepository
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _driver = MutableLiveData<UiState<Driver>>()
    val driver: LiveData<UiState<Driver>>
        get() = _driver

    private val _editDriver = MutableLiveData<UiState<String>>()
    val editDriver: LiveData<UiState<String>>
        get() = _editDriver

    private val _studentNumberList = MutableLiveData<UiState<List<Int>>>()
    val studentNumberList: LiveData<UiState<List<Int>>>
        get() = _studentNumberList

    private val _addPhoto = MutableLiveData<UiState<String>>()
    val addPhoto: LiveData<UiState<String>>
        get() = _addPhoto

    private val _uploadFile = MutableLiveData<UiState<String>>()
    val uploadFile: LiveData<UiState<String>>
        get() = _uploadFile


    fun getDriver(user: Driver?) = viewModelScope.launch {
        _driver.value = UiState.Loading
        if (user != null) {
            repository.getDriver(user) { _driver.value = it }
        }
    }

    fun editDriver(user: Driver) = viewModelScope.launch {
        _editDriver.value = UiState.Loading
        repository.editDriver(user) { _editDriver.value = it }
    }

    fun getStudentNumberList(user: Driver) = viewModelScope.launch {
        _studentNumberList.value = UiState.Loading
        repository.getStudentNumberList(user) { _studentNumberList.value = it }
    }

    fun addPhoto(user: Driver, photoUri: Uri) = viewModelScope.launch {
        _addPhoto.value = UiState.Loading
        repository.addPhoto(user, photoUri) { _addPhoto.value = it }
    }

    fun uploadFile(user: Driver, fileUri: Uri) = viewModelScope.launch {
        _uploadFile.value = UiState.Loading
        repository.uploadFile(user, fileUri) { _uploadFile.value = it }
    }

}