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
import kotlinx.android.synthetic.main.fragment_phone.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class PhoneFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.verifyPhoneNumber()
//            arguments?.also {
//                if (it[ARG_ACTION] == ACTION_MOBILE) {
//                    viewModel.onGoToVerifyNumberScreenClicked()
//                } else {
//                    viewModel.onGoToVerifyNumberScreenFromSocialClicked()
//                }
//            }
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        RxTextView.textChanges(phoneEditText)
//                .skipInitialValue()
                .subscribe {
                    viewModel.onPhoneInputChanged(it.toString())
                }

        viewModel.verifyPhoneNumber.observe(this, Observer {
            Timber.d("verifyPhoneNumber $it")
        })
    }

    companion object {

        const val TAG = "PhoneFragment"
        const val ACTION_MOBILE = "ACTION_MOBILE"
        const val ACTION_SOCIAL = "ACTION_SOCIAL"
        private const val ARG_ACTION = "ARG_ACTION"

        @JvmStatic
        fun newInstance(action: String) = PhoneFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ACTION, action)
            }
        }
    }
}
