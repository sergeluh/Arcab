package com.serg.arcab.ui.auth.mobile

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.*

import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_phone.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class PhoneFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_phone, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.verifyPhoneNumber()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        phoneEditText.addTextChangedListener(MaskWatcher.getDefault())
        RxTextView.textChanges(phoneEditText)
                .skipInitialValue()
                .subscribe {
                    Timber.d("phone $it")
                    viewModel.onPhoneInputChanged(it.toString())
                }

        viewModel.phoneVerificationProgress.observe(viewLifecycleOwner, Observer { result ->
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

        viewModel.codeSentAction.observe(this, Observer {
            callback.goToCodeVerification(action)
        })

        viewModel.signedInAction.observe(viewLifecycleOwner, Observer {
            when (action) {
                ACTION_MOBILE -> {
                    if (it == null) {
                        callback.goToName()
                    } else {
                        callback.goToPassword(ACTION_OLD_USER)
                    }
                }
                ACTION_SOCIAL -> {
                    if (it == null) {
                        callback.goToBirth()
                    } else {
                        callback.goToMain()
                    }
                }
            }
        })
    }

    companion object {

        const val TAG = "PhoneFragment"
        private const val ARG_ACTION = "ARG_ACTION"

        @JvmStatic
        fun newInstance(action: String) = PhoneFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ACTION, action)
            }
        }
    }

    interface Callback {
        fun goToCodeVerification(action: String)
        fun goToPassword(action: String)
        fun goToBirth()
        fun goToMain()
        fun goToName()
    }
}
