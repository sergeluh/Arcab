package com.serg.arcab.datamanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.serg.arcab.Result
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.ui.auth.FirebaseAuthModel
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.serg.arcab.utils.SingleLiveEvent


interface UserDataManager {
    fun verifyPhoneNumber(phoneNumber: String?): LiveData<Result<FirebaseAuthModel>>
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): LiveData<Result<FirebaseAuthModel>>
    fun createCredentiolsWithCode(code: String?): LiveData<Result<FirebaseAuthModel>>
}

class UserDataManagerImpl constructor(val appExecutors: AppExecutors): UserDataManager {

    override fun createCredentiolsWithCode(code: String?): LiveData<Result<FirebaseAuthModel>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private val phoneLogin = SingleLiveEvent<Result<FirebaseAuthModel>>()
    private val signIn = SingleLiveEvent<Result<FirebaseAuthModel>>()


    override fun verifyPhoneNumber(phoneNumber: String?): LiveData<Result<FirebaseAuthModel>> {

        Timber.d("verifyPhoneNumber $phoneNumber")
//        var authModel: FirebaseAuthModel = FirebaseAuthModel()

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
                        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                            Timber.d("onVerificationCompleted $credentials")
                            //phoneLogin.value =
                            phoneLogin.postValue(Result.success(FirebaseAuthModel(null, null, credentials)))
                            //signInWithPhoneAuthCredential(credentials)
                        }

                        override fun onVerificationFailed(exc: FirebaseException?) {
                            Timber.e(exc, "onVerificationFailed")
                            phoneLogin.postValue(Result.error(exc?.message))
                        }

                        override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                            Timber.d("onCodeSent $p0, token: $p1")
                            phoneLogin.postValue(Result.success(FirebaseAuthModel(p0, p1)))
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

    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): LiveData<Result<FirebaseAuthModel>> {

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(Executors.newSingleThreadExecutor(), OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = task.result.user
                        signIn.postValue(Result.success(FirebaseAuthModel(null, null, null, user)))
                    } else {

                        // Sign in failed, display a message and update the UI
                        //Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            signIn.postValue(Result.error(task?.exception?.message))
                        }
                        signIn.postValue(Result.error("something went wrong"))
                    }
                })
        return signIn
    }




}