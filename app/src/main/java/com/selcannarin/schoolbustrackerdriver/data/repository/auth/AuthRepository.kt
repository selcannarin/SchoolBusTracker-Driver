package com.selcannarin.schoolbustrackerdriver.data.repository.auth

import com.google.firebase.auth.FirebaseUser
import com.selcannarin.schoolbustrackerdriver.data.model.Driver

interface AuthRepository {

    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser?

    suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser?

    suspend fun saveUser(driver: Driver): Boolean

    fun signOut(): FirebaseUser?

    fun getCurrentUser(): FirebaseUser?

    suspend fun sendResetPassword(email: String): Boolean

}


