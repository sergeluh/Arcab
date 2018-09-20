package com.serg.arcab.ui.splash

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.serg.arcab.USERS_FIREBASE_TABLE
import com.serg.arcab.User
import com.serg.arcab.base.BaseActivity
import com.serg.arcab.ui.auth.AuthActivity
import com.serg.arcab.ui.main.MainActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            val database = FirebaseDatabase.getInstance()
            database.reference
                    .child(USERS_FIREBASE_TABLE)
                    .child(user.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            AuthActivity.start(this@SplashActivity)
                            finish()
                        }
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val u = dataSnapshot.getValue(User::class.java)
                            if (u != null) {
                                MainActivity.start(this@SplashActivity, u)
                            } else {
                                AuthActivity.start(this@SplashActivity)
                            }
                            finish()
                        }
                    })
        } else {
            AuthActivity.start(this)
            finish()
        }
//        FirebaseAuth.getInstance().signOut()
//        AuthActivity.start(this)
//        MainActivity.start(this)
//        finish()
    }

    override fun onBackPressed() {

    }
}
