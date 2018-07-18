package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*

import com.serg.arcab.R
import com.serg.arcab.TRIPS_FIREBASE_TABLE
import com.serg.arcab.UNIVERSITIES_FIREBASE_TABLE
import com.serg.arcab.UNIVERSITIES_SUFIX_FIREBASE_TABLE
import com.serg.arcab.model.Trip
import com.serg.arcab.model.University
import kotlinx.android.synthetic.main.fragment_link_id.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class LinkIdFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_link_id, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.setOnClickListener {
            showProgressBar()
            viewModel.onHideKeyboard()
            //Add link id to model by clicking next
            viewModel.tripOrder.linkId = editText2.text.toString()
            //Read university from database
            val linkId = viewModel.tripOrder.linkId
            val whereClause = linkId?.substring(linkId.indexOf("@") + 1)
            Timber.d("Selection: $whereClause")
            FirebaseDatabase.getInstance().reference.child(UNIVERSITIES_FIREBASE_TABLE)
                    .orderByChild(UNIVERSITIES_SUFIX_FIREBASE_TABLE).equalTo(whereClause).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    hideProgressBar()
                }

                @Suppress("SENSELESS_COMPARISON")
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children){
                        Timber.d("Trip request snapshot $snapshot")
                        viewModel.university = snapshot.getValue(University::class.java)
                    }
                    if (viewModel.university != null){
                        Timber.d("Trips to")
                        val idsTo = viewModel.university?.schedule?.trips_to?.filter { it != null }?.map { it.trip_id }?.toMutableList()
                        fillTripList(idsTo, viewModel.tripsTo)
                        Timber.d("Trips from")
                        val idsFrom = viewModel.university?.schedule?.trips_from?.filter { it != null }?.map { it.trip_id }?.toMutableList()
                        fillTripList(idsFrom, viewModel.tripsFrom)
                        hideProgressBar()
                        //Go to next string if all data filled
                        viewModel.onGoToPlacesClicked()
                    } else{
                        hideProgressBar()
                        viewModel.onGoNotToAvailableFragmentClicked()
                    }
                }
            })
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
        navBar.backBtn.setImageResource(R.drawable.ic_close_black_24dp)

        //Listener that sets nextBtn enabled when email text pass the validation
        editText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                navBar.nextBtn.isEnabled = validateEmail(editText2.text.toString())
            }

        })
    }

    override fun onResume() {
        super.onResume()
        //Set the initial state of the nextBtn
        navBar.nextBtn.isEnabled = validateEmail(editText2.text.toString())
    }

    private fun fillTripList(ids: MutableList<Int?>?, trips: MutableList<Trip>) {
        ids?.forEach {
            Timber.d("Trip id: $it")
            FirebaseDatabase.getInstance().reference.child(TRIPS_FIREBASE_TABLE).child(it.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Timber.d("Trip request cancelled")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            Timber.d("Trip snapshot: $p0")
                            try {
                                trips.add(p0.getValue(Trip::class.java)!!)
                                Timber.d("Trip item is: ${p0.getValue(Trip::class.java)}")
                            } catch (exc: DatabaseException) {
                                Timber.d("Trip item is wrong: ${exc.message}")
                            }
                        }
                    })
        }
    }

    private fun showProgressBar(){
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar?.visibility = View.GONE
    }

    //Method for email validation
    private fun validateEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    companion object {

        const val TAG = "LinkIdFragment"

        @JvmStatic
        fun newInstance() = LinkIdFragment()
    }
}
