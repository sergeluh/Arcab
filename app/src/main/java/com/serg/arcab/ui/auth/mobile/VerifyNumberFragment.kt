package com.serg.arcab.ui.auth.mobile

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.ACTION_OLD_USER
import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import com.serg.arcab.utils.SMSMonitor
import kotlinx.android.synthetic.main.fragment_verify_number.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class VerifyNumberFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var action: String

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        action = arguments!!.getString(ARG_ACTION)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("OLDNEWUSER action: $action")

        navBar.nextBtn.setOnClickListener {
            viewModel.signIn()
        }

        navBar.nextBtn.isEnabled = false

        if (action == ACTION_OLD_USER){
            textView.text = "Welcome back"
            use_password_instead.text = "Use password instead"
            use_password_instead.setOnClickListener {
                viewModel.useEmailInstead = true
                Timber.d("NEWLINE go to password clicked")
                callback.useEmailInstead()
            }
        }else{
            use_password_instead.text = "Resend sms"
            use_password_instead.setOnClickListener {
                viewModel.verifyPhoneNumber(false)
            }
        }

//        if (viewModel.useEmailInstead){
//            use_password_instead.visibility = View.GONE
//        }

//        use_password_instead.setOnClickListener {
//            viewModel.useEmailInstead = true
//            callback.useEmailInstead()
//        }

        SMSMonitor.verificationCode.observe(viewLifecycleOwner, Observer {
            verification_code.setText(it)
        })

        viewModel.codeVerificationProgress.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    hideLoading()
                    if (action == ACTION_OLD_USER){
                        callback.goToProfile()
                        Timber.d("NEWLINE phone successfull verified")
                    }else{
                        Timber.d("GOING FORTH to password. new user")
//                        callback.goToPassword(ACTION_NEW_USER)
                        callback.goToName()
                    }
                    viewModel.codeVerificationProgress.value = null
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    showMessage(it.message)
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })

        RxTextView.textChanges(verification_code)
                .skipInitialValue()
                .subscribe {
                    viewModel.onVerificationCodeInputChanged(it.toString())
                    navBar.nextBtn.isEnabled = it.length == 6
                }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.also {  user ->
                explainTextView.text = String.format(getString(R.string.auth_verify_number_explanation), user.phone_number)
            }
        })

        viewModel.signedInAction.observe(viewLifecycleOwner, Observer {
            when (action) {
                com.serg.arcab.ACTION_MOBILE -> {
                    if (it == null) {
                        callback.goToName()
                    } else {
                        callback.goToProfile()
//                        callback.goToPassword(ACTION_OLD_USER)
                    }
                }
                com.serg.arcab.ACTION_SOCIAL -> {
                    if (it == null) {
                        callback.goToBirth()
                    } else {
                        callback.goToProfile()
                    }
                }
            }
        })
    }


    interface Callback {
        fun goToPassword(action: String)
        fun goToBirth()
        fun goToProfile()
        fun goToName()
        fun useEmailInstead()
    }

    companion object {

        const val TAG = "VerifyNumberFragment"
        private const val ARG_ACTION = "ARG_ACTION"

        @JvmStatic
        fun newInstance(action: String) = VerifyNumberFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ACTION, action)
            }
        }
    }
}
