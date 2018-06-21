package com.serg.arcab.ui.auth.mobile


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_verify_number.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.jakewharton.rxbinding2.widget.RxTextView


class VerifyNumberFragment : Fragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {

            viewModel.createCredentiols(navBar.nextBtn.text.toString())

            /*arguments?.also {
                if (it[ARG_ACTION] == ACTION_MOBILE) {

                    //val credential = PhoneAuthProvider.getCredential(verificationId, code)
                    viewModel.onGoToNameScreenClicked()
                } else {
                    viewModel.onGoToBirthScreenClicked()
                }
            }*/

        }

        RxTextView.textChanges(verification_code)
                .subscribe {
                    viewModel.onVerificationCodeInputChanged(it.toString())
                }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        editNumberBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        viewModel.verifyPhoneNumber.observe(this, Observer { authModel ->

            if(authModel?.data?.authUser != null) {
                viewModel.onGoToNameScreenClicked()
            }

            if(authModel?.data?.credentials != null) {
                //TODO sign in
                viewModel.signIn(authModel.data!!.credentials!!)
            }

            if(authModel?.data?.authCode != null && authModel?.data?.authId != null ) {

            }
            Timber.d("verifyPhoneNumber $authModel")
        })

        viewModel.signInWithPhoneAuthCredential.observe(this, Observer {

        })

        viewModel.verificationCode.observe(this, Observer {
            navBar.nextBtn.isEnabled = it != null && it.length == 6
        })

        viewModel.createCredentialsWithCode.observe(this, Observer { authModel ->
            if(authModel?.data?.credentials != null) {
                //TODO sign in
                viewModel.signIn(authModel.data!!.credentials!!)
            }
        })

    }

    companion object {

        const val TAG = "VerifyNumberFragment"
        const val ACTION_MOBILE = "ACTION_MOBILE"
        const val ACTION_SOCIAL = "ACTION_SOCIAL"
        private const val ARG_ACTION = "ARG_ACTION"

        @JvmStatic
        fun newInstance(action: String) = VerifyNumberFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ACTION, action)
            }
        }
    }
}
