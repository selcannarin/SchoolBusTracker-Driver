package com.selcannarin.schoolbustrackerdriver.data.repository.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.selcannarin.schoolbustrackerdriver.data.datasource.auth.AuthDataSource
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authDataSource.signInWithEmailPassword(email, password)
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser? {
        return authDataSource.signUpWithEmailPassword(email, password)
    }

    override suspend fun saveUser(driver: Driver): Boolean {
        return authDataSource.saveUser(driver)
    }

    override fun signOut(): FirebaseUser? {
        return authDataSource.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getUser()
    }

    override suspend fun sendResetPassword(email: String): Boolean {
        authDataSource.sendPasswordReset(email)
        return true
    }
}
