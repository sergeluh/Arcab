package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.serg.arcab.R
import com.serg.arcab.ui.main.dialogs.AlertFragment
import com.serg.arcab.ui.main.dialogs.UtilFragment
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
            val alert = UtilFragment()
            alert.header = "Uh oh, Low balance."
            alert.message = "Uh oh, Low balance."
            alert.buttonText = "Action"
            alert.callback = object : UtilFragment.Callback {
                override fun alertButtonClicked() {
                    alert.dismiss()
                }

                override fun onDismiss() {
                    val fragment = PaymentFragment()
                    fragment.tokenSuccessListener = object : PaymentFragment.TokenSuccessListener {
                        override fun onTokenSuccess(tokenId: String) {
                            Toast.makeText(context, "Received token: $tokenId", Toast.LENGTH_SHORT).show()
                            viewModel.onGoToResultClicked()
                        }
                    }
                    fragment.show(childFragmentManager, "fragment")
                }
            }

            alert.show(childFragmentManager, UtilFragment.TAG)
        }

        navBar.nextBtn.isEnabled = false

        check_box_common_point.setOnClickListener {
            if (check_box_your_point.isChecked) {
                check_box_your_point.isChecked = false
            }
            checkSelected()
        }

        check_box_your_point.setOnClickListener {
            if (check_box_common_point.isChecked) {
                check_box_common_point.isChecked = false
            }
            checkSelected()
        }

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
