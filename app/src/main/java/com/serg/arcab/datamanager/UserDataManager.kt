package com.serg.arcab.datamanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.serg.arcab.Result
import com.serg.arcab.data.AppExecutors
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface UserDataManager {
    fun verifyPhoneNumber(phoneNumber: String?): LiveData<Result<PhoneAuthCredential>>
}

class UserDataManagerImpl constructor(val appExecutors: AppExecutors): UserDataManager {

    private val phoneLogin = MutableLiveData<Result<PhoneAuthCredential>>()

    override fun verifyPhoneNumber(phoneNumber: String?): LiveData<Result<PhoneAuthCredential>> {
        Timber.d("verifyPhoneNumber $phoneNumber")
        if (phoneNumber == null || phoneNumber.isBlank()) {
            phoneLogin.value = Result.error("Invalid phone number")
        } else {
            phoneLogin.value = Result.loading()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    Executors.newSingleThreadExecutor(),
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credentials: PhoneAuthCredential?) {
                            Timber.d("onVerificationCompleted $credentials")
                            phoneLogin.value = Result.success(credentials)
                        }

                        override fun onVerificationFailed(exc: FirebaseException?) {
                            Timber.e(exc, "onVerificationFailed")
                            phoneLogin.value = Result.error(exc?.message)
                        }

                        override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                            Timber.d("onCodeSent $p0, token: $p1")
                            super.onCodeSent(p0, p1)
                        }

                        override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                            Timber.w("onCodeAutoRetrievalTimeOut $p0")
                            super.onCodeAutoRetrievalTimeOut(p0)
                        }
                    })
        }

        return phoneLogin
    }
}