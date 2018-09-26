package com.serg.arcab.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.serg.arcab.*
import com.serg.arcab.base.BaseActivity
import com.serg.arcab.ui.main.MainActivity
import com.serg.arcab.ui.auth.mobile.*
import com.serg.arcab.ui.auth.social.FromSocialFragment
import com.serg.arcab.ui.auth.social.SocialFragment
import com.serg.arcab.ui.profile.ProfileActivity
import com.serg.arcab.utils.SMSMonitor
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber

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
        FromSocialFragment.Callback,
        DeclineFragment.Callback {


    private val viewModel by viewModel<AuthViewModel>()

    private lateinit var smsReceiver: SMSMonitor
    private lateinit var filter: IntentFilter
    private val receiveSMSRquest = 1212
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        filter.priority = 100
        smsReceiver = SMSMonitor()

        if (savedInstanceState == null) {
            replaceFragment(AuthFragment.newInstance(), AuthFragment.TAG)
        }

        viewModel.backAction.observe(this, Observer {
            popFragment()
        })

        viewModel.goToScan.observe(this, Observer {
            addFragment(ScanFragment.newInstance(), ScanFragment.TAG)
        })

        viewModel.goToCapture.observe(this, Observer {
            addFragment(CaptureFragment.newInstance(), CaptureFragment.TAG)
        })

        viewModel.goToPassword.observe(this, Observer {
            Timber.d("NEWLINE go to password activity called")
            addFragment(PasswordFragment.newInstance(ACTION_OLD_USER), PasswordFragment.TAG)
        })

        viewModel.goToName.observe(this, Observer {
            goToName()
        })

        viewModel.goToDecline.observe(this, Observer {
            addFragment(DeclineFragment.newInstance(), DeclineFragment.TAG)
        })

        viewModel.goToGender.observe(this, Observer {
            addFragment(BirthFragment.newInstance(), BirthFragment.TAG)
        })

        viewModel.goToForgotPassword.observe(this, Observer{
            addFragment(ForgotPasswordFragment.newInstance(), ForgotPasswordFragment.TAG)
        })

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
            Timber.d("SMSMONITOR permissions granted. Register receiver")
            registerReceiver(smsReceiver, filter)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS), receiveSMSRquest)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("SMSMONITOR onActivityResult")
        if (requestCode == receiveSMSRquest && resultCode == PackageManager.PERMISSION_GRANTED){
            Timber.d("SMSMONITOR onActivityResult registering")
            registerReceiver(smsReceiver, filter)
        }
    }


    private fun popFragment() {
//        v6
        supportFragmentManager.popBackStack()
//        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        hideKeyboard()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        hideKeyboard()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment,
                        R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                .replace(R.id.container, fragment, tag)
                .addToBackStack("my_stack")
                .commit()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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

    override fun goToProfile() {
        ProfileActivity.start(this, viewModel.user.value)
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

    override fun goToScan() {
        addFragment(ScanFragment.newInstance(), ScanFragment.TAG)
    }

    override fun goToSocial() {
        addFragment(SocialFragment.newInstance(), SocialFragment.TAG)
    }

    override fun goToFillFromSocial() {
        addFragment(FromSocialFragment.newInstance(), FromSocialFragment.TAG)
    }

    override fun useEmailIntead() {
        addFragment(EmailFragment.newInstance(), EmailFragment.TAG)
    }

    override fun goToPhone() {
        addFragment(PhoneFragment.newInstance(ACTION_MOBILE), PhoneFragment.TAG)
    }

    override fun goToBegin() {
        start(this)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (SMSMonitor.isRegistered) {
            Timber.d("SMSMONITOR unregistering")
            unregisterReceiver(smsReceiver)
            SMSMonitor.isRegistered = false
        }
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, AuthActivity::class.java))
        }
    }
}