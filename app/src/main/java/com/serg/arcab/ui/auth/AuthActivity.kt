package com.serg.arcab.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.serg.arcab.R
import com.serg.arcab.base.BaseActivity
import com.serg.arcab.ui.main.MainActivity
import com.serg.arcab.ui.auth.mobile.*
import com.serg.arcab.ui.auth.social.FromSocialFragment
import com.serg.arcab.ui.auth.social.SocialFragment
import org.koin.android.architecture.ext.viewModel

class AuthActivity : BaseActivity(),
        AuthFragment.Callback,
        PhoneFragment.Callback,
        VerifyNumberFragment.Callback,
        NameFragment.Callback,
        EmailFragment.Callback,
        PasswordFragment.Callback,
        BirthFragment.Callback,
        IdNumberFragment.Callback,
        RulesFragment.Callback,
        SocialFragment.Callback,
        FromSocialFragment.Callback
{


    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            replaceFragment(AuthFragment.newInstance(), AuthFragment.TAG)
        }

        viewModel.backAction.observe(this, Observer {
            popFragment()
        })

        /*viewModel.goToMobileNumberLogin.observe(this, Observer {
            addFragment(PhoneFragment.newInstance(PhoneFragment.ACTION_MOBILE), PhoneFragment.TAG)
        })

        viewModel.goToSocialLogin.observe(this, Observer {
            addFragment(SocialFragment.newInstance(), SocialFragment.TAG)
        })


        viewModel.goToPasswordInput.observe(this, Observer {
            addFragment(PasswordFragment.newInstance(PasswordFragment.ACTION_NEW_USER), PasswordFragment.TAG)
        })

        viewModel.goToBirthInput.observe(this, Observer {

        })


        viewModel.goToFillInfoFromSocial.observe(this, Observer {
            addFragment(FromSocialFragment.newInstance(), FromSocialFragment.TAG)
        })

        viewModel.goToMobileNumberFomSocial.observe(this, Observer {
            addFragment(PhoneFragment.newInstance(PhoneFragment.ACTION_SOCIAL), PhoneFragment.TAG)
        })

        viewModel.goToVerifyNumberFomSocial.observe(this, Observer {
            addFragment(VerifyNumberFragment.newInstance(VerifyNumberFragment.ACTION_SOCIAL), VerifyNumberFragment.TAG)
        })

        viewModel.passwordCheckedAction.observe(this, Observer {
            goToMain()
        })

        */
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
                .replace(R.id.container, fragment, tag)
                .addToBackStack("my_stack")
                .commit()
    }





    override fun goToMobileNumberInput(action: String) {
        addFragment(PhoneFragment.newInstance(action), PhoneFragment.TAG)
    }

    override fun goToCodeVerification(action: String) {
        addFragment(VerifyNumberFragment.newInstance(action), VerifyNumberFragment.TAG)
    }

    override fun goToPassword(action: String) {
        addFragment(PasswordFragment.newInstance(action), PasswordFragment.TAG)
    }

    override fun goToBirth() {
        addFragment(BirthFragment.newInstance(), BirthFragment.TAG)
    }

    override fun goToMain() {
        MainActivity.start(this)
        finish()
    }

    override fun goToName() {
        addFragment(NameFragment.newInstance(), NameFragment.TAG)
    }

    override fun goToEmail() {
        addFragment(EmailFragment.newInstance(), EmailFragment.TAG)
    }

    override fun goToEmiratesId() {
        addFragment(IdNumberFragment.newInstance(), IdNumberFragment.TAG)
    }

    override fun goToRules() {
        addFragment(RulesFragment.newInstance(), RulesFragment.TAG)
    }

    override fun goToSocial() {
        addFragment(SocialFragment.newInstance(), SocialFragment.TAG)
    }

    override fun goToFillFromSocial() {
        addFragment(FromSocialFragment.newInstance(), FromSocialFragment.TAG)
    }


    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, AuthActivity::class.java))
        }
    }
}