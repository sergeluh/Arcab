package com.serg.arcab.ui.auth

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import kotlinx.android.synthetic.main.auth_navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_phone.*
import org.koin.android.architecture.ext.sharedViewModel

class PhoneFragment : Fragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToVerifyNumberScreenClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    companion object {

        const val TAG = "PhoneFragment"

        @JvmStatic
        fun newInstance() = PhoneFragment()
    }
}