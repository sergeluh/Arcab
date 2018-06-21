package com.serg.arcab.ui.auth.social

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.R
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_social.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class SocialFragment : Fragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.visibility = View.GONE
        googleBtn.setOnClickListener {
            viewModel.onLoginWithGoogleClicked()
        }
    }

    companion object {

        const val TAG = "SocialFragment"

        @JvmStatic
        fun newInstance() = SocialFragment()
    }
}
