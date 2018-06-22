package com.serg.arcab.datamanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.serg.arcab.Result
import com.serg.arcab.User
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.ui.auth.FirebaseAuthModel
import com.serg.arcab.utils.SingleLiveEvent
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

interface UserDataManager {
    fun verifyPhoneNumber(phoneNumber: String?)
    fun createCredentialsWithCode(code: String?)

    fun getUser(): LiveData<Result<User>>
    fun getOnCodeSentAction(): LiveData<Unit>
    fun getPhoneVerification(): LiveData<Result<FirebaseAuthModel>>
    fun getFirebaseUser(): LiveData<Result<FirebaseUser>>
    fun getOnSignedInAction(): SingleLiveEvent<Unit>
}

class UserDataManagerImpl constructor(val appExecutors: AppExecutors): UserDataManager {
    override fun getUser(): LiveData<Result<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val phoneVerification = MutableLiveData<Result<FirebaseAuthModel>>()
    private val onCodeSentAction = SingleLiveEvent<Unit>()
    private val firebaseUser = MutableLiveData<Result<FirebaseUser>>()
    private val onSignedInAction = SingleLiveEvent<Unit>()

    override fun getOnCodeSentAction() = onCodeSentAction
    override fun getPhoneVerification() = phoneVerification
    override fun getFirebaseUser() = firebaseUser
    override fun getOnSignedInAction() = onSignedInAction

    override fun verifyPhoneNumber(phoneNumber: String?) {
        Timber.d("verifyPhoneNumber $phoneNumber")

        if (phoneNumber == null || phoneNumber.isBlank()) {
            phoneVerification.value = Result.error("Invalid phone number")
        } else {
            phoneVerification.value = Result.loading()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    UiThreadExecutor(),
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                            Timber.d("onVerificationCompleted $credentials")
                            signInWithPhoneAuthCredential(credentials)
                        }

                        override fun onVerificationFailed(exc: FirebaseException) {
                            Timber.e(exc, "onVerificationFailed")
                            phoneVerification.value = Result.error(exc.message)
                        }

                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken?) {
                            Timber.d("onCodeSentAction $verificationId, token: $token")
                            phoneVerification.value = Result.success(FirebaseAuthModel(verificationId, token))
                            onCodeSentAction.call()
                        }

                        override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                            Timber.w("onCodeAutoRetrievalTimeOut $p0")
                            phoneVerification.value = Result.error("Timeout")
                        }
                    })
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Timber.d("signInWithPhoneAuthCredential")
        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(UiThreadExecutor(), OnCompleteListener<AuthResult> { task ->
                    Timber.d("signInWithPhoneAuthCredential complete")
                    if (task.isSuccessful) {
                        val user = task.result.user
                        Timber.d("signInWithPhoneAuthCredential user $user")
                        firebaseUser.value = Result.success(user)
                        phoneVerification.value = Result.success(null)
                        onSignedInAction.call()
                    } else {
                        Timber.e(task.exception)
                        var message = task.exception?.message ?: "Error while sign in"

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid credentials"
                        }
                        firebaseUser.value = Result.error(message)
                    }
                })
    }

    override fun createCredentialsWithCode(code: String?) {
        val verificationId = phoneVerification.value?.data?.verificationId
        if (code == null || code.isBlank() || code.length != 6) {
            firebaseUser.value = Result.error("Input verification code")
        } else if (verificationId != null) {
            signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, code))
        } else {
            firebaseUser.value = Result.error("Input verification code")
        }
    }



    private class UiThreadExecutor : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }
}