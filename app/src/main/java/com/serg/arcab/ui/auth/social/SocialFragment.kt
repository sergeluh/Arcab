package com.serg.arcab.ui.auth.social

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.serg.arcab.R
import com.serg.arcab.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_social.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.serg.arcab.base.BaseFragment

class SocialFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var client: GoogleSignInClient

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        client = GoogleSignIn.getClient(activity!!, gso)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
        navBar.nextBtn.visibility = View.GONE
        googleBtn.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                Timber.d("google existing account ${account.givenName}, ${account.familyName}, ${account.email}")
                handleGoogleAccount(account)
            } else {
                signIn()
            }
        }

        viewModel.goToFillInfoFromSocial.observe(viewLifecycleOwner, Observer {
            callback.goToFillFromSocial()
        })
    }

    private fun signIn() {
        startActivityForResult(client.signInIntent, RC_SIGN_IN)
    }

    private fun handleGoogleAccount(account: GoogleSignInAccount) {
        viewModel.setDataFromGoogleAccount(account)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            handleGoogleAccount(account)
            Timber.d("google account ${account?.givenName}, ${account?.familyName}, ${account?.email}")
        } catch (e: ApiException) {
            //showMessage(e.message)
            Timber.e(e)
        }
    }

    interface Callback {
        fun goToFillFromSocial()
    }

    companion object {

        const val TAG = "SocialFragment"
        const val RC_SIGN_IN = 0

        @JvmStatic
        fun newInstance() = SocialFragment()
    }
}
