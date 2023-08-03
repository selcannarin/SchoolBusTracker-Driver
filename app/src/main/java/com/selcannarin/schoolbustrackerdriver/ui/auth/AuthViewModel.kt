package com.selcannarin.schoolbustrackerdriver.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.remote.FirebaseEvents
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val TAG = "MainViewModel"
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = _firebaseUser

    private val eventsChannel = Channel<FirebaseEvents>()

    val allEventsFlow = eventsChannel.receiveAsFlow()

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        when {
            email.isEmpty() -> {
                eventsChannel.send(FirebaseEvents.ErrorCode(1))
            }
            password.isEmpty() -> {
                eventsChannel.send(FirebaseEvents.ErrorCode(2))
            }
            else -> {
                actualSignInUser(email, password)
            }
        }
    }

    fun signUpUser(email: String, password: String, confirmPass: String) = viewModelScope.launch {
        when {
            email.isEmpty() -> {
                eventsChannel.send(FirebaseEvents.ErrorCode(1))
            }
            password.isEmpty() -> {
                eventsChannel.send(FirebaseEvents.ErrorCode(2))
            }
            password != confirmPass -> {
                eventsChannel.send(FirebaseEvents.ErrorCode(3))
            }
            else -> {
                actualSignUpUser(email, password)
            }
        }
    }

    fun saveUser(driver: Driver) = viewModelScope.launch {
        val success = repository.saveUser(driver)
        if (success) {
            eventsChannel.send(FirebaseEvents.Message("User saved successfully"))
        } else {
            eventsChannel.send(FirebaseEvents.Error("Failed to save user"))
        }
    }


    private fun actualSignInUser(email: String, password: String) = viewModelScope.launch {
        try {
            val user = repository.signInWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.postValue(it)
                eventsChannel.send(FirebaseEvents.Message("login success"))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(FirebaseEvents.Error(error[1]))
        }
    }

    private fun actualSignUpUser(email: String, password: String) = viewModelScope.launch {
        try {
            val user = repository.signUpWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.postValue(it)
                eventsChannel.send(FirebaseEvents.Message("sign up success"))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(FirebaseEvents.Error(error[1]))
        }
    }

    fun signOut() = viewModelScope.launch {
        try {
            val user = repository.signOut()
            user?.let {
                eventsChannel.send(FirebaseEvents.Message("logout failure"))
            } ?: eventsChannel.send(FirebaseEvents.Message("sign out successful"))

            getCurrentUser()

        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(FirebaseEvents.Error(error[1]))
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getCurrentUser()
        _firebaseUser.postValue(user)
    }

    fun verifySendPasswordReset(email: String) {
        if (email.isEmpty()) {
            viewModelScope.launch {
                eventsChannel.send(FirebaseEvents.ErrorCode(1))
            }
        } else {
            sendPasswordResetEmail(email)
        }

    }

    private fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            val result = repository.sendResetPassword(email)
            if (result) {
                eventsChannel.send(FirebaseEvents.Message("Reset email sent"))
            } else {
                eventsChannel.send(FirebaseEvents.Error("Could not send password reset"))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(FirebaseEvents.Error(error[1]))
        }
    }

}
