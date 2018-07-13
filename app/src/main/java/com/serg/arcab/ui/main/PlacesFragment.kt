package com.serg.arcab.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.LocationManager
import com.serg.arcab.PlacesManager
import com.serg.arcab.R
import com.serg.arcab.model.CommonPoint
import com.serg.arcab.recycleradapter.BaseDelegateAdapter
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder
import com.serg.arcab.recycleradapter.section.BaseHeaderFooterDelegateAdapter
import com.serg.arcab.recycleradapter.section.Section
import com.serg.arcab.recycleradapter.section.SectionedCompositeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.list_item_places_address.*
import kotlinx.android.synthetic.main.list_item_places_header.*
import kotlinx.android.synthetic.main.list_item_places_pick.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PlacesFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    private val placesManager by inject<PlacesManager>()

    private val locationManager by inject<LocationManager>()

    private lateinit var adapter: SectionedCompositeAdapter

    private val placePickerRequest = 1002

    private var searchDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            //Initialize common points before common points screen appears
            viewModel.commonPoints = mutableListOf()
            FirebaseDatabase.getInstance().reference.child("common_points").
                    addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            for (snapshot in p0.children){
                                viewModel.commonPoints?.add(snapshot.getValue(CommonPoint::class.java)!!)
                                viewModel.onGoToPickupPointClicked()
                            }
                        }

                    })
        }

        adapter = SectionedCompositeAdapter.Builder()
                .add(Section.Builder()
                        .setHeader(HeaderDelegate())
                        .add(PickDelegateAdapter())
                        .build())
                .add(Section.Builder()
                        .setHeader(HeaderDelegate())
                        .add(PlaceDelegateAdapter())
                        .build())
                .add(Section.Builder()
                        .setHeader(HeaderDelegate())
                        .add(PlaceDelegateAdapter())
                        .build())
                .build()
        adapter.pickCallback = object : SectionedCompositeAdapter.PickCallback{
            override fun currentLocationClicked() {
                if (ActivityCompat.checkSelfPermission(context!!,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    val lm = context?.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
                    var location = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    if (location == null){
                        location = lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                    }
                    viewModel.tripOrder.currentLocation = LatLng(location.latitude, location.longitude)
                    Timber.d("Place is ${viewModel.tripOrder.currentLocation}")
                }
            }

            override fun locationOnMapClicked() {
                //Intent builder for picking places
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(activity), placePickerRequest)
            }

        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        adapter.setHeaderDataInSection(0, getString(R.string.initial_setup_places_fragment_header_pick))
        adapter.setHeaderDataInSection(1, getString(R.string.initial_setup_places_fragment_header_nearby))
        adapter.setHeaderDataInSection(2, getString(R.string.initial_setup_places_fragment_header_search_result))
        adapter.swapDataInSection(0, listOf(getString(R.string.initial_setup_places_fragment_pick_current_location),
                getString(R.string.initial_setup_places_fragment_pick_location_on_map)))

        searchDisposable = RxTextView.textChanges(editText)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchQuery(it.toString())
                }, {

                })

    }

    //Picking places processing
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("Request nearby places: $requestCode, result - $resultCode")
        if (requestCode == placePickerRequest && resultCode == RESULT_OK){
            val place = PlacePicker.getPlace(context, data)
            viewModel.tripOrder.currentLocation = place.latLng
            Timber.d("Place is: ${viewModel.tripOrder.currentLocation}")
        }
    }

    private fun searchQuery(query: String) {
        placesManager.setQuery(query, null, object : PlacesManager.Callback {
            override fun loading(isLoading: Boolean) {

            }
            override fun result(result: MutableList<AutocompletePrediction>) {
                adapter.swapDataInSection(2, result)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchDisposable?.dispose()
    }









    class HeaderDelegate: BaseHeaderFooterDelegateAdapter() {
        override fun getLayoutId(): Int {
            return R.layout.list_item_places_header
        }

        override fun bindViewHolder(holder: BaseViewHolder, item: DataHolder) {
            val text = item.data as String
            holder.hearedText.text = text
        }
    }

    class PickDelegateAdapter: BaseDelegateAdapter() {
        override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
            return true
        }

        override fun getLayoutId(): Int {
            return R.layout.list_item_places_pick
        }

        override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
            val text = dataHolder.data as String
            holder.pickTextView.text = text
        }
    }

    class PlaceDelegateAdapter: BaseDelegateAdapter() {
        override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
            return items[position].data is AutocompletePrediction
        }

        override fun getLayoutId(): Int {
            return R.layout.list_item_places_address
        }

        override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
            val prediction = dataHolder.data as AutocompletePrediction
            holder.nameTextView.text = prediction.getPrimaryText(null)
            holder.addressTextView.text = prediction.getFullText(null)
        }
    }







    companion object {

        const val TAG = "PlacesFragment"

        @JvmStatic
        fun newInstance() = PlacesFragment()
    }
}
