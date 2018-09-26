package com.serg.arcab.ui.auth.mobile


import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.ACTION_NEW_USER
import com.serg.arcab.ACTION_OLD_USER

import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.bottom_notification.view.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_password.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class PasswordFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        when(action) {
            ACTION_NEW_USER -> {
                titleTextView.text = getString(R.string.auth_password_title_new)
                navBar.nextBtn.text = getString(R.string.next)
                navBar.nextBtn.setOnClickListener {
                    Timber.d("CHECKPASS new user")
                    viewModel.savePassword()
                }
            }
            ACTION_OLD_USER -> {
                titleTextView.text = getString(R.string.auth_password_title_old)
                navBar.nextBtn.text = getString(R.string.sign_in)
                navBar.nextBtn.setOnClickListener {
                    Timber.d("CHECKPASS old user")
                    viewModel.checkPassword()
                }
            }
        }

        if (viewModel.useEmailInstead){
            Timber.d("MYUSER passwordFragment auth with email")

            titleTextView.text = "Enter Your Password"

            textView9.text = "Forgot password?"
            textView9.setTextColor(resources.getColor(R.color.colorAccent))
            textView9.setOnClickListener {
                viewModel.onGoToForgotPasswordClicked()
            }
            viewModel.emailVerificationProgress.observe(viewLifecycleOwner, Observer {
                when(it?.status){
                    Result.Status.ERROR -> {
                        hideLoading()
                        /**
                         * proceed to phone fragment for registration
                         */
                        Timber.d("MYUSER error when logged in: ${it.message}")
                        it.message?.also {
                            if (it.contains("no user")){
                                callback.goToPhone()
                            }else{
                                showBottomNotificationWithMessage(it)
                            }
                        }
                    }
                    Result.Status.SUCCESS -> {
                        hideLoading()
                        callback.goToProfile()
                    }
                    Result.Status.LOADING -> {
                        showLoading()
                    }
                }
            })
            navBar.nextBtn.setOnClickListener {
                Timber.d("MYUSER starting sign in with email")
                viewModel.loginWithEmail()
            }
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        RxTextView.textChanges(passwordEditText)
                .subscribe {
                    if (bottom_notification.visibility == View.VISIBLE){
                        bottom_notification.visibility = View.GONE
                    }
                    viewModel.onPasswordInputChanged(it.toString())
                }

        showPasswordBtn.setOnClickListener {
            if (passwordEditText.transformationMethod == null){
                passwordEditText.transformationMethod = PasswordTransformationMethod()
                iconInfo.visibility = View.GONE
                showPasswordBtn.text = "Show"
            }else{
                passwordEditText.transformationMethod = null
                showPasswordBtn.text = "Hide"
                iconInfo.visibility = View.VISIBLE
            }
        }

        viewModel.password.observe(viewLifecycleOwner, Observer {
            navBar.nextBtn.isEnabled = !it.isNullOrBlank()
        })

        viewModel.passwordValidation.observe(viewLifecycleOwner, Observer {
//            showMessage(it)
            showBottomNotificationWithMessage(it)
        })

        viewModel.checkPasswordProgress.observe(viewLifecycleOwner, Observer { result ->
            when(result?.status) {
                Result.Status.ERROR -> {
                    hideLoading()
                    Timber.d("CHECKINGPASS error: showing messaage - ${result.message}")
                    showMessage(result.message)
                }
                Result.Status.SUCCESS -> {
                    hideLoading()
                    callback.goToProfile()
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })

        viewModel.passwordCheckedAction.observe(viewLifecycleOwner, Observer {
            callback.goToProfile()
        })

        viewModel.goToBirthInput.observe(viewLifecycleOwner, Observer {
            callback.goToBirth()
        })
    }

    private fun showBottomNotificationWithMessage(message: String?){
        bottom_notification.visibility = View.VISIBLE
        bottom_notification.bottom_notification_icon.setImageResource(R.drawable.ic_bottom_warning)
        bottom_notification.bottom_notification_message.text = message
    }

    interface Callback {
        fun goToBirth()
        fun goToProfile()
        fun goToPhone()
    }

    companion object {

        const val TAG = "PasswordFragment"
        const val ARG_ACTION = "ARG_ACTION"

        @JvmStatic
        fun newInstance(action: String) = PasswordFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ACTION, action)
            }
        }
    }
}
