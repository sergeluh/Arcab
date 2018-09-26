package com.serg.arcab.ui.splash

import android.app.ActivityOptions
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.serg.arcab.R
import com.serg.arcab.USERS_FIREBASE_TABLE
import com.serg.arcab.User
import com.serg.arcab.base.BaseActivity
import com.serg.arcab.ui.auth.AuthActivity
import com.serg.arcab.ui.main.MainActivity
import com.serg.arcab.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance()
            database.reference
                    .child(USERS_FIREBASE_TABLE)
                    .child(user.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Timber.d("User not found starting activity")
                            val options = ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity, Pair.create<View, String>(splash_title, "splashTitle"), Pair.create<View, String>(splash_icon, "splashIcon"))
                            AuthActivity.start(this@SplashActivity, options)
                            finish()
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val u = dataSnapshot.getValue(User::class.java)
                            if (u != null) {
                                ProfileActivity.start(this@SplashActivity, u)
                            } else {
                                val options = ActivityOptions.makeSceneTransitionAnimation(
                                        this@SplashActivity,
                                        Pair.create<View, String>(splash_title, "splashTitle"),
                                        Pair.create<View, String>(splash_icon, "splashIcon"),
                                        Pair.create<View, String>(transition_container, "container"))
                                AuthActivity.start(this@SplashActivity, options)
                            }
                            finish()
                        }
                    })
        } else {
            Handler().postDelayed({
                val options = ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity, Pair.create<View, String>(splash_title, "splashTitle"), Pair.create<View, String>(splash_icon, "splashIcon"))
                AuthActivity.start(this, options)
                finish()
            }, 2000)
        }
//        FirebaseAuth.getInstance().signOut()
//        AuthActivity.start(this)
//        MainActivity.start(this)
//        finish()
    }

    override fun onBackPressed() {

    }
}
