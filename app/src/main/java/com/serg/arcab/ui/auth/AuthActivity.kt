package com.serg.arcab.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.serg.arcab.R
import com.serg.arcab.base.BaseActivity
import com.serg.arcab.ui.auth.mobile.*
import com.serg.arcab.ui.auth.social.FromSocialFragment
import com.serg.arcab.ui.auth.social.SocialFragment
import org.koin.android.architecture.ext.viewModel

class AuthActivity : BaseActivity() {

    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            replaceFragment(AuthFragment.newInstance(), AuthFragment.TAG)
        }

        viewModel.goToMobileNumberLogin.observe(this, Observer {
            addFragment(PhoneFragment.newInstance(), PhoneFragment.TAG)
        })

        viewModel.goToSocialLogin.observe(this, Observer {
            addFragment(SocialFragment.newInstance(), SocialFragment.TAG)
        })

        viewModel.goToVerifyNumber.observe(this, Observer {
            addFragment(VerifyNumberFragment.newInstance(), VerifyNumberFragment.TAG)
        })

        viewModel.goToNameInput.observe(this, Observer {
            addFragment(NameFragment.newInstance(), NameFragment.TAG)
        })

        viewModel.goToEmailInput.observe(this, Observer {
            addFragment(EmailFragment.newInstance(), EmailFragment.TAG)
        })

        viewModel.goToPasswordInput.observe(this, Observer {
            addFragment(PasswordFragment.newInstance(), PasswordFragment.TAG)
        })

        viewModel.goToBirthInput.observe(this, Observer {
            addFragment(BirthFragment.newInstance(), BirthFragment.TAG)
        })

        viewModel.goToIdInput.observe(this, Observer {
            addFragment(IdNumberFragment.newInstance(), IdNumberFragment.TAG)
        })

        viewModel.goToRules.observe(this, Observer {
            addFragment(RulesFragment.newInstance(), RulesFragment.TAG)
        })

        viewModel.backAction.observe(this, Observer {
            popFragment()
        })

        viewModel.goToFillInfoFromSocial.observe(this, Observer {
            addFragment(FromSocialFragment.newInstance(), FromSocialFragment.TAG)
        })
    }

    private fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment, tag)
                .addToBackStack("my_stack")
                .commit()
    }

    companion object {

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, AuthActivity::class.java))
        }
    }
}