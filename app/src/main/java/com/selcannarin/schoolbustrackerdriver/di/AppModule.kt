package com.selcannarin.schoolbustrackerdriver.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.selcannarin.schoolbustrackerdriver.data.datasource.auth.AuthDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.auth.AuthDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.datasource.location.LocationDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.location.LocationDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.datasource.profile.ProfileDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.profile.ProfileDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.datasource.student.StudentDataSource
import com.selcannarin.schoolbustrackerdriver.data.datasource.student.StudentDataSourceImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.auth.AuthRepositoryImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.location.LocationRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.location.LocationRepositoryImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.profile.ProfileRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.profile.ProfileRepositoryImpl
import com.selcannarin.schoolbustrackerdriver.data.repository.student.StudentRepository
import com.selcannarin.schoolbustrackerdriver.data.repository.student.StudentRepositoryImpl
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
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
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


    @Singleton
    @Provides
    fun provideStudentDataSource(firestore: FirebaseFirestore): StudentDataSource {
        return StudentDataSourceImpl(firestore)
    }

    @Singleton
    @Provides
    fun provideStudentRepository(
        provideDataSource: StudentDataSource,
    ): StudentRepository {
        return StudentRepositoryImpl(provideDataSource)
    }

    @Singleton
    @Provides
    fun provideLocationDataSource(firestore: FirebaseFirestore): LocationDataSource {
        return LocationDataSourceImpl(firestore)
    }

    @Singleton
    @Provides
    fun provideLocationRepository(
        provideDataSource: LocationDataSource,
    ): LocationRepository {
        return LocationRepositoryImpl(provideDataSource)
    }


}