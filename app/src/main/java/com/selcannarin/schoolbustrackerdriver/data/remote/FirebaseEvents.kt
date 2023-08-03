package com.selcannarin.schoolbustrackerdriver.data.remote

sealed class FirebaseEvents {
    data class Message(val message: String) : FirebaseEvents()
    data class ErrorCode(val code: Int) : FirebaseEvents()
    data class Error(val error: String) : FirebaseEvents()
    data class Success(val success: Boolean) : FirebaseEvents()
}