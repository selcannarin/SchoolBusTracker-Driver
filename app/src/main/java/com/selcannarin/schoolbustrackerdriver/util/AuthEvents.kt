package com.selcannarin.schoolbustrackerdriver.util

sealed class AuthEvents {
    data class Message(val message: String) : AuthEvents()
    data class ErrorCode(val code: Int) : AuthEvents()
    data class Error(val error: String) : AuthEvents()
}