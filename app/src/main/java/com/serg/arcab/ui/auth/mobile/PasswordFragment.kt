package com.serg.arcab.ui.auth.mobile


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_password.*
import org.koin.android.architecture.ext.sharedViewModel

class PasswordFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            viewModel.savePassword()
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
    }

    companion object {

        const val TAG = "PasswordFragment"

        @JvmStatic
        fun newInstance() = PasswordFragment()
    }
}
