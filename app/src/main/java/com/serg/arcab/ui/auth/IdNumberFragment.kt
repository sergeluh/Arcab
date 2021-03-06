package com.serg.arcab.ui.auth

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_id_number.*
import org.koin.android.architecture.ext.sharedViewModel

class IdNumberFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_id_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToRulesScreenClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        button.setOnClickListener {
//            viewModel.onGoToScanClicked()
            viewModel.onGoToCaptureClicked()
        }

        viewModel.goToRules.observe(viewLifecycleOwner, Observer {
//            callback.goToRules()
        })

        viewModel.goToScan.observe(viewLifecycleOwner, Observer {
            callback.goToScan()
        })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.emiratesId.value != null){
            editText2.setText(viewModel.emiratesId.value)
        }
    }

    interface Callback {
//        fun goToRules()
        fun goToScan()
    }

    companion object {

        const val TAG = "IdNumberFragment"

        @JvmStatic
        fun newInstance() = IdNumberFragment()
    }
}
