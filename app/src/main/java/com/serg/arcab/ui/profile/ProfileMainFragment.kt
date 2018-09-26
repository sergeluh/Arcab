package com.serg.arcab.ui.profile

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import com.serg.arcab.R
import com.serg.arcab.Result
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.main.MainActivity
import com.serg.arcab.ui.profile.adapters.ProfileListAdapter
import com.serg.arcab.ui.profile.model.ProfileListItem
import com.serg.arcab.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.fragment_profile_main.*
import kotlinx.android.synthetic.main.profile_footer.view.*
import kotlinx.coroutines.experimental.withTimeoutOrNull
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ProfileMainFragment : BaseFragment() {

    private val viewModel by sharedViewModel<ProfileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.userDataProgress.observe(viewLifecycleOwner, Observer {
            when(it?.status){
                Result.Status.LOADING -> showLoading()
                Result.Status.ERROR -> {
                    hideLoading()
                    showMessage(it.message)
                }
                Result.Status.SUCCESS -> {
                    hideLoading()
                    Timber.d("MYUSER loaded")
                }
            }
        })

        show_details_button.setOnClickListener {
            viewModel.goToProfileDetails.call()
        }

        footer.main_button.setOnClickListener {
            activity?.also {
                MainActivity.start(it)
            }
        }

        viewModel.user.observe(viewLifecycleOwner, Observer {
            populateUserData()
        })

        if (viewModel.isUserEmpty()) {
            Timber.d("User empty. Requesting data")
            viewModel.requestUserData()
        }else{
            populateUserData()
        }

        sign_out_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, SplashActivity::class.java))
            activity?.finish()
        }

        profile_list.adapter = ProfileListAdapter(mutableListOf(ProfileListItem("Category", "First title", "First message first message first message first messsage", null, "SignUp"),
                ProfileListItem("Not Category", "Second title", "Second message second message second message second message", R.drawable.logo, "Other action"))){
            showMessage(it.title)
        }
        profile_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun populateUserData(){
        with(viewModel.user.value){
            date_text.text = SimpleDateFormat("EEE MMM dd", Locale.UK).format(Date())
            profile_title.text = String.format(resources.getString(R.string.profile_header, this?.first_name))
            Timber.d("MYUSER name: ${this?.first_name}, lastName: ${this?.last_name}, phoneNimber: ${this?.phone_number}, email: ${this?.email}")
        }
    }

    companion object {
        const val TAG = "profileMainFragment"
        fun newInstance() = ProfileMainFragment()
    }
}
