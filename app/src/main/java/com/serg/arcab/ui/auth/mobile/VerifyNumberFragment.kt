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
import kotlinx.android.synthetic.main.fragment_verify_number.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel

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

        navBar.nextBtn.setOnClickListener {
            viewModel.signIn()
        }

        viewModel.codeVerificationProgress.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    hideLoading()
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
                }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        editNumberBtn.setOnClickListener {
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
                        callback.goToPassword(ACTION_OLD_USER)
                    }
                }
                com.serg.arcab.ACTION_SOCIAL -> {
                    if (it == null) {
                        callback.goToBirth()
                    } else {
                        callback.goToMain()
                    }
                }
            }
        })
    }


    interface Callback {
        fun goToPassword(action: String)
        fun goToBirth()
        fun goToMain()
        fun goToName()
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
