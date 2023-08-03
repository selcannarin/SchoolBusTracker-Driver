package com.selcannarin.schoolbustrackerdriver.data.datasource.firebase.auth

import com.google.firebase.auth.FirebaseUser
import com.selcannarin.schoolbustrackerdriver.data.model.Driver

interface AuthDataSource {

    suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser?

    suspend fun saveUser(driver: Driver): Boolean

    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser?

    fun signOut(): FirebaseUser?

    fun getUser(): FirebaseUser?

    suspend fun sendPasswordReset(email: String)

}