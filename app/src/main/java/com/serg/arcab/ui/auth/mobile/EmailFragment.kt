package com.serg.arcab.ui.auth.mobile


import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.ACTION_MOBILE
import com.serg.arcab.ACTION_NEW_USER
import com.serg.arcab.ACTION_OLD_USER

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_email.*
import org.koin.android.architecture.ext.sharedViewModel

class EmailFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

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

        navBar.nextBtn.isEnabled = false

        RxTextView.textChanges(emailEditText)
                .skipInitialValue()
                .subscribe {
                    viewModel.onEmailInputChanged(it.toString())
                    navBar.nextBtn.isEnabled = viewModel.validateEmail(it.toString())
                }

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.also { user ->
                if (!viewModel.useEmailInstead) {
                    titleTextView.text = String.format(getString(R.string.auth_email_title), user.first_name)
                }
                emailEditText.setText(user.email)
            }
        })

        viewModel.user.value?.also {
            if (viewModel.useEmailInstead) {
                titleTextView.text = "Enter email"
                textView9.text = "Enter the email address for phone number ${viewModel.user.value?.phone_number}"
            }
        }

        if (arguments != null && arguments!!.containsKey(ACTION)){
            val action = arguments?.getString(ACTION)
            if (action == ACTION_OLD_USER){
                titleTextView.text = "Welcome back"
            }
        }

        viewModel.emailValidation.observe(viewLifecycleOwner, Observer {
            showMessage(it)
        })

        viewModel.goToPasswordInput.observe(viewLifecycleOwner, Observer {
            callback.goToPassword(ACTION_NEW_USER)
        })

        if (!viewModel.useEmailInstead) {
            val emailSuffixes = mutableListOf("@gmail.com", "@icloud.com", "@outlook.com", "@hotmail.com", "@yahoo.com")
            emailSuffixList.adapter = EmailSuffixAdapter(emailSuffixes) {
                emailEditText.text.append(it)
            }
            emailSuffixList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    interface Callback {
        fun goToPassword(action: String)
    }

    companion object {

        const val TAG = "EmailFragment"

        private const val ACTION = "action"

        @JvmStatic
        fun newInstance(action: String? = null): EmailFragment{
            val arguments = Bundle()
            arguments.putString(ACTION, action)
            val fragment = EmailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}
