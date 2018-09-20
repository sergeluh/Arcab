package com.serg.arcab.ui.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.bottom_notification.view.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel

class ForgotPasswordFragment : BaseFragment() {

    val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.sendPasswordResetRequest(emailEditText.text.toString())
        }

        viewModel.user.value?.email?.also {
            emailEditText.setText(it)
        }

        emailEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (bottom_notification.visibility == View.VISIBLE){
                    bottom_notification.visibility = View.GONE
                }
            }
        })

        viewModel.passwordResetProgress.observe(viewLifecycleOwner, Observer {
            when(it?.status){
                Result.Status.ERROR -> {
                    hideLoading()
                    showBottomNotificationWithMessageAndIcon(it.message, R.drawable.ic_info)
                }
                Result.Status.LOADING -> showLoading()
                Result.Status.SUCCESS -> {
                    hideLoading()
                    showBottomNotificationWithMessageAndIcon("We sent a link to the email address " +
                            "you entered. Tap link to set new password.", R.drawable.ic_bottom_check)
                }
            }
        })

    }

    private fun showBottomNotificationWithMessageAndIcon(message: String?, icon: Int){
        bottom_notification.visibility = View.VISIBLE
        bottom_notification.bottom_notification_icon.setImageResource(icon)
        bottom_notification.bottom_notification_message.text = message
    }

    companion object {
        fun newInstance() = ForgotPasswordFragment()
        const val TAG = "forgot password fragment"
    }

}
