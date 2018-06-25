package com.serg.arcab.ui.auth.social

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.ACTION_SOCIAL

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_from_social.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class FromSocialFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_from_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.saveNameAndEmail()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
        viewModel.user.observe(viewLifecycleOwner, Observer {
            firstNameEditText.setText(it?.first_name)
            lastNameEditText.setText(it?.last_name)
            emailEditText.setText(it?.email)
        })

        viewModel.emailValidation.observe(viewLifecycleOwner, Observer {
            showMessage(it)
        })


        viewModel.name.observe(viewLifecycleOwner, Observer {
            navBar.nextBtn.isEnabled = !it?.first.isNullOrBlank() && !it?.second.isNullOrBlank()
        })


        RxTextView.textChanges(firstNameEditText)
                .skipInitialValue()
                .subscribe {
                    viewModel.onFirstNameInputChanged(it.toString())
                }

        RxTextView.textChanges(lastNameEditText)
                .skipInitialValue()
                .subscribe {
                    viewModel.onLastNameInputChanged(it.toString())
                }

        RxTextView.textChanges(emailEditText)
                .skipInitialValue()
                .subscribe {
                    viewModel.onEmailInputChanged(it.toString())
                }

        viewModel.goToMobileNumberFomSocial.observe(viewLifecycleOwner, Observer {
            callback.goToMobileNumberInput(ACTION_SOCIAL)
        })
    }

    interface Callback {
        fun goToMobileNumberInput(action: String)
    }

    companion object {

        const val TAG = "RulesFragment"

        @JvmStatic
        fun newInstance() = FromSocialFragment()
    }
}
