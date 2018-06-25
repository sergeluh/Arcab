package com.serg.arcab.ui.auth

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.ACTION_MOBILE
import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.android.architecture.ext.sharedViewModel

class AuthFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mobileBtn.setOnClickListener {
            viewModel.onEnterWithMobileClicked()
        }

        socialBtn.setOnClickListener {
            viewModel.onEnterWithSocialClicked()
        }

        viewModel.goToMobileNumberLogin.observe(viewLifecycleOwner, Observer {
            callback.goToMobileNumberInput(ACTION_MOBILE)
        })

        viewModel.goToSocialLogin.observe(viewLifecycleOwner, Observer {
            callback.goToSocial()
        })
    }

    companion object {

        const val TAG = "AuthFragment"

        @JvmStatic
        fun newInstance() = AuthFragment()
    }

    interface Callback {
        fun goToMobileNumberInput(action: String)
        fun goToSocial()
    }
}
