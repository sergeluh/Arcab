package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_pickup_point.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel


class PaymentPlanFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_plan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToResultClicked()
        }

        navBar.nextBtn.isEnabled = false

        check_box_common_point.setOnClickListener(View.OnClickListener {
            if(check_box_your_point.isChecked) {
                check_box_your_point.isChecked = false
            }
            checkSelected()
        })

        check_box_your_point.setOnClickListener(View.OnClickListener {
            if(check_box_common_point.isChecked) {
                check_box_common_point.isChecked = false
            }
            checkSelected()
        })

    }

    private fun checkSelected() {
        navBar.nextBtn.isEnabled = check_box_common_point.isChecked || check_box_your_point.isChecked
    }



    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PaymentPlanFragment()
    }
}
