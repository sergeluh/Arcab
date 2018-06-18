package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_link_id.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel

class LinkIdFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_link_id, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToPlacesClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
        navBar.backBtn.setImageResource(R.drawable.ic_close_black_24dp)


    }

    companion object {

        const val TAG = "LinkIdFragment"

        @JvmStatic
        fun newInstance() = LinkIdFragment()
    }
}
