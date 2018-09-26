package com.serg.arcab.ui.profile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.serg.arcab.R
import com.serg.arcab.User
import com.serg.arcab.base.BaseActivity
import org.koin.android.architecture.ext.viewModel

class ProfileActivity : BaseActivity() {

    private val viewModel by viewModel<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if(intent.extras != null){
            viewModel.user.value = intent.getParcelableExtra(USER_KEY) as User
        }

        viewModel.goToProfileDetails.observe(this, Observer {
            addFragment(ProfileDetailsFragment.newInstance(), ProfileDetailsFragment.TAG)
        })

        viewModel.backAction.observe(this, Observer {
            popFragment()
        })

        replaceFragment(ProfileMainFragment.newInstance(), ProfileMainFragment.TAG)
    }

    private fun popFragment(){
        supportFragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment, tag: String){
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    private fun addFragment(fragment: Fragment, tag: String){
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment,
                        R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                .replace(R.id.container, fragment, tag)
                .addToBackStack("my_stack")
                .commit()
    }

    companion object {
        private const val USER_KEY = "user"
        fun start(activity: Activity, user: User?){
            val intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra(USER_KEY, user)
            activity.startActivity(intent)
        }
    }
}
