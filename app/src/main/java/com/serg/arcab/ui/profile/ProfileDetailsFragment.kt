package com.serg.arcab.ui.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile_details.*
import org.koin.android.architecture.ext.sharedViewModel

class ProfileDetailsFragment : BaseFragment() {

    private val viewModel by sharedViewModel<ProfileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hide_details_button.setOnClickListener {
            viewModel.backAction.call()
        }

        user_name.text = "${viewModel.user.value?.first_name} ${viewModel.user.value?.last_name}"
    }

    companion object {
        const val TAG = "profileDetailsFragment"
        fun newInstance() = ProfileDetailsFragment()
    }


}
