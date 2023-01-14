package com.example.triperience.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            if(it.exception != null){
                cont.resumeWithException(it.exception!!)
            }else{
                if (it.isSuccessful){
                    cont.resume(it.result, null)
                }
            }
        }
    }
}