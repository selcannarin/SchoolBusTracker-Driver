package com.selcannarin.schoolbustrackerdriver.di

import com.google.firebase.firestore.FirebaseFirestore
import com.selcannarin.schoolbustrackerdriver.data.datasource.firebase.auth.AuthDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.firebase.auth.AuthDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuthenticator(firestore: FirebaseFirestore): AuthDataSource {
        return AuthDataSourceImpl(firestore)
    }

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideRepository(
        authenticator: AuthDataSource,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(authenticator, firestore)
    }
}