package com.serg.arcab.ui.auth.mobile

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.fragment_verify_number.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel

class VerifyNumberFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            viewModel.signIn()

            /*arguments?.also {
                if (it[ARG_ACTION] == ACTION_MOBILE) {

                    //val credential = PhoneAuthProvider.getCredential(verificationId, code)
                    viewModel.onGoToNameScreenClicked()
                } else {
                    viewModel.onGoToBirthScreenClicked()
                }
            }*/

        }

        viewModel.firebaseUser.observe(viewLifecycleOwner, Observer {
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
                .subscribe {
                    viewModel.onVerificationCodeInputChanged(it.toString())
                }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        editNumberBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    companion object {

        const val TAG = "VerifyNumberFragment"
        const val ACTION_MOBILE = "ACTION_MOBILE"
        const val ACTION_SOCIAL = "ACTION_SOCIAL"
        private const val ARG_ACTION = "ARG_ACTION"

        @JvmStatic
        fun newInstance(action: String) = VerifyNumberFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ACTION, action)
            }
        }
    }
}
