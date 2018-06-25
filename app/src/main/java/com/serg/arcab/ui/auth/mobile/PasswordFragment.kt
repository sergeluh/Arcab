package com.serg.arcab.ui.auth.mobile


import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
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
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_password.*
import org.koin.android.architecture.ext.sharedViewModel

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
                    viewModel.savePassword()
                }
            }
            ACTION_OLD_USER -> {
                titleTextView.text = getString(R.string.auth_password_title_old)
                navBar.nextBtn.text = getString(R.string.sign_in)
                navBar.nextBtn.setOnClickListener {
                    viewModel.checkPassword()
                }
            }
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        RxTextView.textChanges(passwordEditText)
                .subscribe {
                    viewModel.onPasswordInputChanged(it.toString())
                }

        showPasswordBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = null
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod()
            }
        }

        viewModel.password.observe(viewLifecycleOwner, Observer {
            navBar.nextBtn.isEnabled = !it.isNullOrBlank()
        })

        viewModel.passwordValidation.observe(viewLifecycleOwner, Observer {
            showMessage(it)
        })

        viewModel.checkPasswordProgress.observe(viewLifecycleOwner, Observer { result ->
            when(result?.status) {
                Result.Status.ERROR -> {
                    hideLoading()
                    showMessage(result.message)
                }
                Result.Status.SUCCESS -> {
                    hideLoading()
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })

        viewModel.passwordCheckedAction.observe(viewLifecycleOwner, Observer {
            callback.goToMain()
        })

        viewModel.goToBirthInput.observe(viewLifecycleOwner, Observer {
            callback.goToBirth()
        })
    }

    interface Callback {
        fun goToBirth()
        fun goToMain()
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
