package com.omouravictor.invest_view.util

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.omouravictor.invest_view.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Context.getErrorMessage(e: Exception?): String {
    return when (e) {
        is FirebaseAuthWeakPasswordException -> getString(R.string.weakPassword)
        is FirebaseAuthInvalidCredentialsException -> getString(R.string.invalidCredentials)
        is FirebaseAuthUserCollisionException -> getString(R.string.userAlreadyExists)
        is FirebaseTooManyRequestsException -> getString(R.string.tooManyRequests)
        is FirebaseFirestoreException -> {
            when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND -> getString(R.string.userNotFound)
                else -> getString(R.string.somethingWentWrong)
            }
        }

        is UnknownHostException -> getString(R.string.noInternetConnection)
        is FirebaseNetworkException -> getString(R.string.noInternetConnection)
        is SocketTimeoutException -> getString(R.string.checkInternetConnection)
        is HttpException -> {
            when (e.code()) {
                429 -> getString(R.string.rateLimitPerMinuteExceeded)
                else -> getString(R.string.somethingWentWrong)
            }
        }

        else -> getString(R.string.somethingWentWrong)
    }
}