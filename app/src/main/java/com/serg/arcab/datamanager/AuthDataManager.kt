package com.serg.arcab.datamanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.serg.arcab.Result
import com.serg.arcab.Sha1
import com.serg.arcab.USERS_FIREBASE_TABLE
import com.serg.arcab.User
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.data.PrefsManager
import com.serg.arcab.ui.auth.FirebaseAuthModel
import com.serg.arcab.utils.SingleLiveEvent
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

interface AuthDataManager {
    fun verifyPhoneNumber(phone: String?, goForth: Boolean)
    fun signInWithCode(code: String?)
    fun getStarted()
    fun checkPassword(password: String?)


    fun getPhoneVerificationProgress(): MutableLiveData<Result<Unit>>
    fun getOnCodeSentAction(): SingleLiveEvent<Unit>
    fun getCodeVerificationProgress(): MutableLiveData<Result<Unit>>
    fun getSignedInAction(): SingleLiveEvent<User>
    fun getProfileUploadProgress(): MutableLiveData<Result<Unit>>
    fun getProfileUploadedAction(): SingleLiveEvent<Unit>
    fun getUser(): MutableLiveData<User>

    fun signInWithEmail()
    fun getEmailVerificationProgress(): MutableLiveData<Result<Unit>>

    fun getCheckPasswordProgress(): MutableLiveData<Result<Unit>>
    fun getPasswordCheckedAction(): SingleLiveEvent<Unit>

    fun resetPassword(email: String)
    fun getResetPaswordProgress(): MutableLiveData<Result<Unit>>
}

class AuthDataManagerImpl constructor(val appExecutors: AppExecutors, val prefsManager: PrefsManager) : AuthDataManager {

    private var verificationId: String? = null

    private val phoneVerificationProgress = MutableLiveData<Result<Unit>>()
    private val codeSentAction = SingleLiveEvent<Unit>()

    private val codeVerificationProgress = MutableLiveData<Result<Unit>>()
    private val signedInAction = SingleLiveEvent<User>()

    private val profileUploadProgress = MutableLiveData<Result<Unit>>()
    private val profileUploadedAction = SingleLiveEvent<Unit>()

    private val checkPasswordProgress = MutableLiveData<Result<Unit>>()
    private val passwordCheckedAction = SingleLiveEvent<Unit>()

    private val emailVerificationProgress = MutableLiveData<Result<Unit>>()

    private val resetPasswordProgress = MutableLiveData<Result<Unit>>()

    private val user = MutableLiveData<User>().apply {
        value = User()
    }

    override fun getPhoneVerificationProgress() = phoneVerificationProgress
    override fun getOnCodeSentAction() = codeSentAction

    override fun getEmailVerificationProgress() = emailVerificationProgress

    override fun getCodeVerificationProgress() = codeVerificationProgress
    override fun getSignedInAction() = signedInAction

    override fun getProfileUploadProgress() = profileUploadProgress
    override fun getProfileUploadedAction() = profileUploadedAction

    override fun getCheckPasswordProgress() = checkPasswordProgress
    override fun getPasswordCheckedAction() = passwordCheckedAction

    override fun getResetPaswordProgress() = resetPasswordProgress

    override fun getUser() = user

    override fun verifyPhoneNumber(phone: String?, goForth: Boolean) {

        if (phone == null || phone.isBlank()) {
            phoneVerificationProgress.value = Result.error("Invalid phone number")
        } else {
            val validPhone = getValidPhoneNumber(phone)
            Timber.d("validPhone $validPhone")
            phoneVerificationProgress.value = Result.loading()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    validPhone,
                    60,
                    TimeUnit.SECONDS,
                    UiThreadExecutor(),
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                            Timber.d("onVerificationCompleted $credentials")
                            verificationCompleted(credentials)
                        }

                        override fun onVerificationFailed(exc: FirebaseException) {
                            Timber.e(exc, "onVerificationFailed")
                            phoneVerificationProgress.value = Result.error(exc.message)
                        }

                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken?) {
                            Timber.d("onCodeSentAction $verificationId, token: $token")
                            this@AuthDataManagerImpl.verificationId = verificationId
                            phoneVerificationProgress.value = Result.success(Unit)
                            codeSentAction.call()
                        }

                        override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                            Timber.w("onCodeAutoRetrievalTimeOut $p0")
                            phoneVerificationProgress.value = Result.error("Timeout")
                        }
                    })
        }
    }

    private fun verificationCompleted(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(UiThreadExecutor(), OnCompleteListener<AuthResult> { task ->
                    Timber.d("signInWithPhoneAuthCredential complete")
                    if (task.isSuccessful) {
                        Timber.d("signInWithPhoneAuthCredential user ${task.result.user}")
                        fetchProfileFromCPhoneVerification(task.result.user)
                    } else {
                        Timber.e(task.exception)
                        var message = task.exception?.message ?: "Error while sign in"

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid credentials"
                        }
                        phoneVerificationProgress.value = Result.error(message)
                    }
                })
    }

    private fun fetchProfileFromCPhoneVerification(firebaseUser: FirebaseUser) {
        val database = FirebaseDatabase.getInstance()
        database.reference
                .child(USERS_FIREBASE_TABLE)
                .child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        phoneVerificationProgress.value = Result.error(error.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val u = dataSnapshot.getValue(User::class.java)
                        Timber.d("user from db: $u")
                        phoneVerificationProgress.value = Result.success(Unit)
                        signedInAction.value = u
                    }
                })
    }


    override fun signInWithCode(code: String?) {
        Timber.d("Sign in with code")
        val vId = verificationId
        if (code == null || code.isBlank() || code.length != 6) {
            Timber.d("co null or blank or to short")
            codeVerificationProgress.value = Result.error("Input verification code")
        } else if (vId != null) {
            Timber.d("vid !null")
            signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(vId, code))
        } else {
            Timber.d("else in signInWithCode")
            codeVerificationProgress.value = Result.error("Input verification code")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Timber.d("signInWithPhoneAuthCredential")
        codeVerificationProgress.value = Result.loading()
        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(UiThreadExecutor(), OnCompleteListener<AuthResult> { task ->
                    Timber.d("signInWithPhoneAuthCredential complete")
                    if (task.isSuccessful) {
                        Timber.d("signInWithPhoneAuthCredential user ${task.result.user}")
                        fetchProfileFromCodeVerification(task.result.user)

                    } else {
                        Timber.e(task.exception)
                        var message = task.exception?.message ?: "Error while sign in"

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid credentials"
                        }
                        codeVerificationProgress.value = Result.error(message)
                    }
                })
    }

    private fun fetchProfileFromCodeVerification(firebaseUser: FirebaseUser) {
        val database = FirebaseDatabase.getInstance()
        database.reference
                .child(USERS_FIREBASE_TABLE)
                .child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        codeVerificationProgress.value = Result.error(error.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val u = dataSnapshot.getValue(User::class.java)
                        Timber.d("user from db: $u")
                        codeVerificationProgress.value = Result.success(Unit)
                        signedInAction.value = u
                    }
                })
    }


    override fun getStarted() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference()

        profileUploadProgress.value = Result.loading()

        val user = user.value
        user?.flags?.isInitialSetupComplete = true
        user?.flags?.isRegistered = true
        user?.password = Sha1.getHash(user?.password)
        user?.phone_number = getValidPhoneNumber(user?.phone_number)

        Timber.d("WRITING USER, ${getUser().value?.email}, ${getUser().value?.password}")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.uid?.also { uid ->
            myRef.child(USERS_FIREBASE_TABLE).child(uid).setValue(user)
                    .addOnSuccessListener {
                        user?.also {
                            Timber.d("User writed: ${it.first_name}, ${it.last_name}, ${it.email}, ${it.phone_number}")
                            prefsManager.saveUser(it)
                            if (it.email != null && it.password != null) {
                                val emailCredentials = EmailAuthProvider.getCredential(it.email!!, it.password!!)
                                FirebaseAuth.getInstance().currentUser?.linkWithCredential(emailCredentials)?.addOnSuccessListener {
                                    Timber.d("MYLINKCREDS success linking with credentials")
                                }?.addOnFailureListener {
                                    Timber.d("MYLINKCREDS failure: ${it.message}")
                                }
                            }
                        }

                        profileUploadProgress.value = Result.success(Unit)
                        profileUploadedAction.call()
                    }
                    .addOnFailureListener {
                        Timber.e(it)
                        profileUploadProgress.value = Result.error(it.message)
                    }
        }
    }

    override fun signInWithEmail() {
        emailVerificationProgress.value = Result.loading()
        if (user.value != null && user.value!!.email != null && user.value!!.password != null) {
            Timber.d("MYUSER ${user.value!!.email}, ${user.value!!.password}")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(user.value!!.email!!, Sha1.getHash(user.value!!.password!!))
                    .addOnSuccessListener {
                        Timber.d("MYUSER auth with email success")
                        emailVerificationProgress.value = Result.success(Unit)
                    }.addOnFailureListener {
                        Timber.d("MYUSER auth with email failure: ${it.message}")
                        emailVerificationProgress.value = Result.error(it.message)
                    }
        }else{
            emailVerificationProgress.value = Result.error("User data is empty")
        }
    }

    private fun getValidPhoneNumber(phone: String?): String {
//        return "+971" + phone?.replace(" ", "")
        return "+380" + phone?.replace(" ", "")
    }


    override fun checkPassword(password: String?) {
        Timber.d("checkPassword $password")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            checkPasswordProgress.value = Result.loading()
            val database = FirebaseDatabase.getInstance()
            database.reference
                    .child(USERS_FIREBASE_TABLE)
                    .child(firebaseUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            checkPasswordProgress.value = Result.error(error.message)
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val u = dataSnapshot.getValue(User::class.java)
                            Timber.d("checkPassword: $u")
                            val existingPasswordHash = u?.password
                            val currentPasswordHash = Sha1.getHash(password)
                            Timber.d("password: $existingPasswordHash, $currentPasswordHash")
                            if (existingPasswordHash?.toLowerCase().equals(currentPasswordHash?.toLowerCase())) {
                                checkPasswordProgress.value = Result.success(Unit)
                                passwordCheckedAction.call()
                            } else {
                                checkPasswordProgress.value = Result.error("Incorrect password")
                            }
                        }
                    })
        } else {
            checkPasswordProgress.value = Result.error("Can not find firebase user")
        }
    }

    override fun resetPassword(email: String) {
        resetPasswordProgress.value = Result.loading()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnFailureListener {
            resetPasswordProgress.value = Result.error(it.message)
            Timber.d("MYRESETPASS failure: ${it.message}")
        }.addOnSuccessListener {
            resetPasswordProgress.value = Result.success(Unit)
            Timber.d("MYRESETPASS success")
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

