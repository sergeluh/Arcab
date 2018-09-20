package com.serg.arcab.ui.auth


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_decline.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel

class DeclineFragment : BaseFragment() {

    lateinit var callback: Callback
    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_decline, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.text = "Cancel"
        navBar.nextBtn.setOnClickListener {
            callback.goToBegin()
        }

        navBar.backBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        go_back_btn.setOnClickListener {
            activity?.onBackPressed()
        }
    }


    interface Callback{
        fun goToBegin()
    }

    companion object {
        const val TAG = "declineFragment"

        fun newInstance() = DeclineFragment()
    }
}
