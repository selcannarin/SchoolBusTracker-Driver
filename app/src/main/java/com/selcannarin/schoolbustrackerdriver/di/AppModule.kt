package com.selcannarin.schoolbustrackerdriver.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.selcannarin.schoolbustrackerdriver.data.datasource.auth.AuthDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.auth.AuthDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.datasource.profile.ProfileDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.profile.ProfileDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepositoryImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.profile.ProfileRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.profile.ProfileRepositoryImpl
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

    @Singleton
    @Provides
    fun provideStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideProfileDataSource(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage
    ): ProfileDataSource {
        return ProfileDataSourceImpl(firestore, firebaseStorage)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(
        provideDataSource: ProfileDataSource,
    ): ProfileRepository {
        return ProfileRepositoryImpl(provideDataSource)
    }


}