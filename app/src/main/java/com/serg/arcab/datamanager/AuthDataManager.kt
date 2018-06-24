package com.serg.arcab.datamanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.serg.arcab.Result
import com.serg.arcab.User
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.ui.auth.FirebaseAuthModel
import com.serg.arcab.utils.SingleLiveEvent
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

interface AuthDataManager {
    fun verifyPhoneNumber(phoneNumber: String?)
    fun signInWithCode(code: String?)
    fun getStarted()

    fun getUser(): MutableLiveData<User>
    fun getOnCodeSentAction(): LiveData<Unit>
    fun getPhoneVerificationModel(): LiveData<Result<FirebaseAuthModel>>
    fun getFirebaseUser(): LiveData<Result<FirebaseUser>>
    fun getOnSignedInAction(): SingleLiveEvent<Unit>
    fun getProfileUpload(): MutableLiveData<Result<Unit>>
    fun getOnProfileUploadedAction(): SingleLiveEvent<Unit>
}

class AuthDataManagerImpl constructor(val appExecutors: AppExecutors): AuthDataManager {

    private val phoneVerificationModel = MutableLiveData<Result<FirebaseAuthModel>>()
    private val onCodeSentAction = SingleLiveEvent<Unit>()
    private val firebaseUser = MutableLiveData<Result<FirebaseUser>>()
    private val onSignedInAction = SingleLiveEvent<Unit>()
    private val user = MutableLiveData<User>().apply {
        value = User()
    }
    private val profileUpload = MutableLiveData<Result<Unit>>()
    private val onProfileUploadedAction = SingleLiveEvent<Unit>()

    override fun getOnCodeSentAction() = onCodeSentAction
    override fun getOnProfileUploadedAction() = onProfileUploadedAction
    override fun getPhoneVerificationModel() = phoneVerificationModel
    override fun getFirebaseUser() = firebaseUser
    override fun getOnSignedInAction() = onSignedInAction
    override fun getUser() = user
    override fun getProfileUpload() = profileUpload

    override fun verifyPhoneNumber(phoneNumber: String?) {
        Timber.d("verifyPhoneNumber $phoneNumber")

        if (phoneNumber == null || phoneNumber.isBlank()) {
            phoneVerificationModel.value = Result.error("Invalid phone number")
        } else {
            phoneVerificationModel.value = Result.loading()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    UiThreadExecutor(),
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                            Timber.d("onVerificationCompleted $credentials")
                            phoneVerificationModel.value = Result.success(null)
                            signInWithPhoneAuthCredential(credentials)
                        }

                        override fun onVerificationFailed(exc: FirebaseException) {
                            Timber.e(exc, "onVerificationFailed")
                            phoneVerificationModel.value = Result.error(exc.message)
                        }

                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken?) {
                            Timber.d("onCodeSentAction $verificationId, token: $token")
                            phoneVerificationModel.value = Result.success(FirebaseAuthModel(verificationId, token))
                            onCodeSentAction.call()
                        }

                        override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                            Timber.w("onCodeAutoRetrievalTimeOut $p0")
                            phoneVerificationModel.value = Result.error("Timeout")
                        }
                    })
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Timber.d("signInWithPhoneAuthCredential")
        firebaseUser.value = Result.loading()
        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(UiThreadExecutor(), OnCompleteListener<AuthResult> { task ->
                    Timber.d("signInWithPhoneAuthCredential complete")
                    if (task.isSuccessful) {
                        val user = task.result.user
                        Timber.d("signInWithPhoneAuthCredential user $user")
                        firebaseUser.value = Result.success(user)
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

    override fun signInWithCode(code: String?) {
        val verificationId = phoneVerificationModel.value?.data?.verificationId
        if (code == null || code.isBlank() || code.length != 6) {
            firebaseUser.value = Result.error("Input verification code")
        } else if (verificationId != null) {
            signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, code))
        } else {
            firebaseUser.value = Result.error("Input verification code")
        }
    }

    override fun getStarted() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference()

        profileUpload.value = Result.loading()

        myRef.child("users").child(firebaseUser.value?.data?.uid!!).setValue(user.value)
                .addOnSuccessListener {
                    profileUpload.value = Result.success(Unit)
                    onProfileUploadedAction.call()
                }
                .addOnFailureListener {
                    Timber.e(it)
                    profileUpload.value = Result.error(it.message)
                }
    }

    private class UiThreadExecutor : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }
}

/*
*
* {
  "rules": {
    "r-users":{
      ".write": true,
      ".read": true
    },
    "users": {
      "$uid": {
        ".write": "$uid === auth.uid",
        ".read": "$uid === auth.uid"
      }
    },
    "universities": {
    	".write": true,
      ".read": true,
      ".indexOn": ["id", "sufix"]
    },
      "common_points": {
        ".write": true,
        ".read": true,
        ".indexOn": ["id"]
      },
      "trips": {
        ".write": true,
        ".read": true,
        ".indexOn": ["id"]
      }
  }
}*/