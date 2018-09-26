package com.serg.arcab.ui.auth

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_rules.*
import org.koin.android.architecture.ext.sharedViewModel

class RulesFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rules, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            viewModel.onGetStartedClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.backBtn.setImageResource(R.drawable.ic_close_red_24dp)
//        navBar.backBtn.scaleType = ImageView.ScaleType.CENTER_CROP
        navBar.nextBtn.text = resources.getString(R.string.accept_text)

        decline_button.setOnClickListener {
            viewModel.onGoToDeclineClicked()
        }

//        switch1.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.setDirectlyContact(isChecked)
//        }
//
//        switch2.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.setSearchAds(isChecked)
//        }
//
//        switch3.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.setUsageData(isChecked)
//        }
//
//        switch4.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.setUsageStats(isChecked)
//        }
//
//        termsAgreementCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            Timber.d("setOnCheckedChangeListener $isChecked")
//            viewModel.setTermsAgreement(isChecked)
//        }
//
//        viewModel.termsValidation.observe(viewLifecycleOwner, Observer {
//            Timber.d("termsValidation $it")
//            navBar.nextBtn.isEnabled = it != null && it
//            termsAgreementCheckBox.isChecked = it != null && it
//        })
//
//        viewModel.user.observe(viewLifecycleOwner, Observer {
//            it?.also { user ->
//                switch1.isChecked = user.terms.directly_contact
//                switch2.isChecked = user.terms.search_ads
//                switch3.isChecked = user.terms.usage_data
//                switch4.isChecked = user.terms.usage_stats
//            }
//        })

        viewModel.profileUploadProgress.observe(viewLifecycleOwner, Observer {
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

        viewModel.profileUploadedAction.observe(viewLifecycleOwner, Observer {
            callback.goToProfile()
        })
    }

    interface Callback {
        fun goToProfile()
    }

    companion object {

        const val TAG = "RulesFragment"

        @JvmStatic
        fun newInstance() = RulesFragment()
    }
}
