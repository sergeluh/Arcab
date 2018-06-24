package com.serg.arcab.ui.auth.mobile

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_name.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class NameFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
//            viewModel.onGoToEmailScreenClicked()
            viewModel.saveName()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }



        RxTextView.textChanges(firstNameEditText)
                .subscribe {
                    viewModel.onFirstNameInputChanged(it.toString())
                }

        RxTextView.textChanges(lastNameEditText)
                .subscribe {
                    viewModel.onLastNameInputChanged(it.toString())
                }

        viewModel.name.observe(viewLifecycleOwner, Observer {
            Timber.d("name $it")
            navBar.nextBtn.isEnabled = !it?.first.isNullOrBlank() && !it?.second.isNullOrBlank()
        })
    }

    companion object {

        const val TAG = "NameFragment"

        @JvmStatic
        fun newInstance() = NameFragment()
    }
}
