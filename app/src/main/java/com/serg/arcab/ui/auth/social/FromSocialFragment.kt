package com.serg.arcab.ui.auth.social

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.auth_navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_from_social.*
import org.koin.android.architecture.ext.sharedViewModel

class FromSocialFragment : Fragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_from_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToNumberScreenFromSocialClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    companion object {

        const val TAG = "RulesFragment"

        @JvmStatic
        fun newInstance() = FromSocialFragment()
    }
}
