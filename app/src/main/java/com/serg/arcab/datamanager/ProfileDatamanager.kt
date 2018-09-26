package com.serg.arcab.datamanager

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.serg.arcab.Result
import com.serg.arcab.USERS_FIREBASE_TABLE
import com.serg.arcab.User

interface ProfileDatamanager{
    fun getUser(): MutableLiveData<User>
    fun requestUserData()
    fun getUserDataProgress(): MutableLiveData<Result<Unit>>
}

class ProfileDataManagerImpl : ProfileDatamanager{
    private val currentUser = MutableLiveData<User>()
    private val getUserDataProgress = MutableLiveData<Result<Unit>>()

    override fun getUser() = currentUser
    override fun getUserDataProgress() = getUserDataProgress

    override fun requestUserData() {
        getUserDataProgress.value = Result.loading()
        FirebaseAuth.getInstance().currentUser?.uid?.also {
            FirebaseDatabase.getInstance().reference.child(USERS_FIREBASE_TABLE).child(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            getUserDataProgress.value = Result.error(p0.message)
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            currentUser.value = p0.getValue(User::class.java)
                            getUserDataProgress.value = Result.success(Unit)
                        }
                    })
        }
    }
}