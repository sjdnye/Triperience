package com.example.triperience.di

import com.example.triperience.features.authentication.data.AuthRepositoryImpl
import com.example.triperience.features.profile.data.repository.ProfileRepositoryImpl
import com.example.triperience.features.authentication.domain.repository.AuthRepository
import com.example.triperience.features.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthentication(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage{
        return FirebaseStorage.getInstance()

    }

    @Singleton
    @Provides
    fun provideFirebaseStore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(firebaseStorage: FirebaseStorage, firestore: FirebaseFirestore, auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(
            firebaseStorage = firebaseStorage,
            firestore = firestore,
            auth = auth
        )
    }

    @Singleton
    @Provides
    fun provideProfileRepository(firebaseStorage: FirebaseStorage, firestore: FirebaseFirestore, auth: FirebaseAuth): ProfileRepository {
        return ProfileRepositoryImpl(
            firebaseStorage = firebaseStorage,
            firestore = firestore,
            auth = auth
        )
    }
}