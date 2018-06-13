package com.serg.arcab.ui.auth

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.android.architecture.ext.sharedViewModel

class AuthFragment : Fragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

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
    }

    companion object {

        const val TAG = "AuthFragment"

        @JvmStatic
        fun newInstance() = AuthFragment()
    }
}
