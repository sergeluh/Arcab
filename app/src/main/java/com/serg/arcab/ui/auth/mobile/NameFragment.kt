package com.serg.arcab.ui.auth.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.auth_navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_name.*
import org.koin.android.architecture.ext.sharedViewModel

class NameFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToEmailScreenClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    companion object {

        const val TAG = "NameFragment"

        @JvmStatic
        fun newInstance() = NameFragment()
    }
}
