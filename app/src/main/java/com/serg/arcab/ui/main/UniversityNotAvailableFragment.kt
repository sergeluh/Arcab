package com.serg.arcab.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_university_not_available.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class UniversityNotAvailableFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_university_not_available, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.text = resources.getString(R.string.initial_setup_else_reject_let_me_in)

        navBar.nextBtn.setOnClickListener{
            Timber.d("organization name: ${organization_name.text}")
            viewModel.onLetMeInClicked(organization_name.text.toString())
        }

        navBar.backBtn.setOnClickListener{
            viewModel.onBackClicked()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UniversityNotAvailableFragment()
    }
}
