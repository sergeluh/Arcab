package com.serg.arcab.ui.auth.mobile

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.*

import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.bottom_notification.view.*
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
            viewModel.verifyPhoneNumber(true)
        }
//
//        use_password_instead.setOnClickListener {
//            callback.useEmailInstead()
//            viewModel.useEmailInstead = true
//        }

//        if (viewModel.user.value != null && viewModel.user.value!!.email != null){
//            use_password_instead.visibility = View.GONE
//        }
//        if (viewModel.useEmailInstead){
//            use_password_instead.visibility = View.GONE
//        }

        navBar.backBtn.setImageResource(R.drawable.ic_close_red_24dp)
        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.isEnabled = false

        phoneEditText.addTextChangedListener(MaskWatcher.getDefault())
        RxTextView.textChanges(phoneEditText)
                .skipInitialValue()
                .subscribe {
                    Timber.d("phone $it")
                    if (bottom_notification.visibility == View.VISIBLE){
                        bottom_notification.visibility = View.GONE
                    }
                    viewModel.onPhoneInputChanged(it.toString())
                    navBar.nextBtn.isEnabled = it.length == 11
                }

        viewModel.phoneVerificationProgress.observe(viewLifecycleOwner, Observer { result ->
            when(result?.status) {
                Result.Status.ERROR -> {
                    hideLoading()
                    showBottomNotificationWithMessage(result.message)
//                    showMessage(result.message)
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
            Timber.d("RECEIVED action is: $it")
            if (it != null) {
                callback.goToCodeVerification(it)
            }else{
                callback.goToCodeVerification(action)
            }
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
                        callback.goToProfile()
                    }
                }
            }
        })
    }

    private fun showBottomNotificationWithMessage(message: String?){
        bottom_notification.bottom_notification_icon.setImageResource(R.drawable.ic_bottom_warning)
        bottom_notification.bottom_notification_message.text = message
        bottom_notification.visibility = View.VISIBLE
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
        fun goToProfile()
        fun goToName()
    }
}
