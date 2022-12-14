package com.example.triperience.di

import com.example.triperience.features.searchedPost.domain.repository.SearchedPostsRepository
import com.example.triperience.features.searchedPost.data.repository.SearchedPostsRepositoryImpl
import com.example.triperience.features.authentication.data.repository.AuthRepositoryImpl
import com.example.triperience.features.profile.data.repository.ProfileRepositoryImpl
import com.example.triperience.features.authentication.domain.repository.AuthRepository
import com.example.triperience.features.category.data.repository.CategoryRepositoryImpl
import com.example.triperience.features.category.domain.repository.CategoryRepository
import com.example.triperience.features.comment.data.repository.CommentRepositoryImpl
import com.example.triperience.features.comment.domain.repository.CommentRepository
import com.example.triperience.features.home.data.repository.HomeRepositoryImpl
import com.example.triperience.features.home.domain.repository.HomeRepository
import com.example.triperience.features.profile.data.repository.UploadPostRepositoryImpl
import com.example.triperience.features.profile.domain.repository.ProfileRepository
import com.example.triperience.features.profile.domain.repository.UploadPostRepository
import com.example.triperience.features.search.data.repository.SearchRepositoryImpl
import com.example.triperience.features.search.domain.repository.SearchRepository
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
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
    fun provideAuthRepository(
        firebaseStorage: FirebaseStorage,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        sharedPrefUtil: SharedPrefUtil
    ): AuthRepository{
        return AuthRepositoryImpl(
            firebaseStorage = firebaseStorage,
            firestore = firestore,
            auth = auth,
            sharedPrefUtil = sharedPrefUtil
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

    @Singleton
    @Provides
    fun provideUploadPostRepository(firebaseStorage: FirebaseStorage, firestore: FirebaseFirestore): UploadPostRepository{
        return UploadPostRepositoryImpl(
            firestore = firestore,
            firebaseStorage = firebaseStorage
        )
    }

    @Singleton
    @Provides
    fun provideSearchRepository(firestore: FirebaseFirestore) : SearchRepository{
        return SearchRepositoryImpl(
            firestore = firestore
        )
    }

    @Singleton
    @Provides
    fun provideHomeRepository(firestore: FirebaseFirestore) : HomeRepository{
        return HomeRepositoryImpl(firestore = firestore)
    }

    @Singleton
    @Provides
    fun provideCommentRepository(firestore: FirebaseFirestore) : CommentRepository{
        return CommentRepositoryImpl(firestore = firestore)
    }

    @Singleton
    @Provides
    fun provideCategoryRepository(firestore: FirebaseFirestore) : CategoryRepository{
        return CategoryRepositoryImpl(firestore = firestore)
    }

    @Singleton
    @Provides
    fun provideSearchedPostsRepository(firestore: FirebaseFirestore) : SearchedPostsRepository {
        return SearchedPostsRepositoryImpl(firestore = firestore)
    }



}