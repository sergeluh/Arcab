package com.serg.arcab.ui.auth

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_rules.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class RulesFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rules, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.text = getString(R.string.get_started)
        navBar.nextBtn.setOnClickListener {
            viewModel.onGetStartedClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        switch1.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDirectlyContact(isChecked)
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSearchAds(isChecked)
        }

        switch3.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUsageData(isChecked)
        }

        switch4.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUsageStats(isChecked)
        }

        termsAgreementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("setOnCheckedChangeListener $isChecked")
            viewModel.setTermsAgreement(isChecked)
        }

        viewModel.termsValidation.observe(viewLifecycleOwner, Observer {
            Timber.d("termsValidation $it")
            navBar.nextBtn.isEnabled = it != null && it
            termsAgreementCheckBox.isChecked = it != null && it
        })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.also { user ->
                switch1.isChecked = user.terms.directlyContact
                switch2.isChecked = user.terms.searchAds
                switch3.isChecked = user.terms.usageData
                switch4.isChecked = user.terms.usageStats
            }
        })

        viewModel.profileUpload.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    hideLoading()
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    showMessage(it.message)
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })


    }

    companion object {

        const val TAG = "RulesFragment"

        @JvmStatic
        fun newInstance() = RulesFragment()
    }
}
