package com.example.triperience.utils.core

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.utils.Constants
import com.example.triperience.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class CoreRepositoryImpl@Inject constructor(
    private val firestore: FirebaseFirestore
) : CoreRepository {

    override suspend fun getPostPublisherDetail(userId: String): User? {
        return try {
            val query = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            query.toObject(User::class.java)
        }catch (e: Exception){
            null
        }
    }
}