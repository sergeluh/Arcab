package com.serg.arcab.ui.auth


import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class ScanFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.isEnabled = false

        navBar.backBtn.setOnClickListener {
            viewModel.backCapture = null
            viewModel.frontCapture = null
            viewModel.onBackClicked()
        }

        emirates_id_front.setOnClickListener {
            viewModel.captureMode = AuthViewModel.CaptureMode.FRONT
            viewModel.onGoToCaptureClicked()
        }

        emirates_id_back.setOnClickListener {
            viewModel.captureMode = AuthViewModel.CaptureMode.BACK
            viewModel.onGoToCaptureClicked()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("EMIRATESID: ${viewModel.frontCapture == null}, ${viewModel.backCapture == null}")

        if (viewModel.frontCapture != null){
            front_hint.visibility = View.GONE
            Timber.d("EMIRATESID: set front")
            emirates_id_front.setImageBitmap(BitmapFactory.decodeByteArray(viewModel.frontCapture,
                    0, viewModel.frontCapture!!.size))
        }

        if (viewModel.backCapture != null){
            back_hint.visibility = View.GONE
            Timber.d("EMIRATESID: set back")
            emirates_id_back.setImageBitmap(BitmapFactory.decodeByteArray(viewModel.backCapture,
                    0, viewModel.backCapture!!.size))
        }

        if (viewModel.frontCapture != null && viewModel.backCapture != null){
            navBar.nextBtn.isEnabled = true
        }
    }

    companion object {
        const val TAG = "scan"

        @JvmStatic
        fun newInstance() = ScanFragment()
    }
}
