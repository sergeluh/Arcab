package com.serg.arcab.ui.auth.mobile


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_email.*
import org.koin.android.architecture.ext.sharedViewModel

class EmailFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            viewModel.saveEmail()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        RxTextView.textChanges(emailEditText)
                .subscribe {
                    viewModel.onEmailInputChanged(it.toString())
                }

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.also { user ->
                titleTextView.text = String.format(getString(R.string.initial_setup_email_title), user.firstName)
                emailEditText.setText(user.email)
            }
        })

        viewModel.emailValidation.observe(viewLifecycleOwner, Observer {
            showMessage(it)
        })
    }

    companion object {

        const val TAG = "EmailFragment"

        @JvmStatic
        fun newInstance() = EmailFragment()
    }
}
