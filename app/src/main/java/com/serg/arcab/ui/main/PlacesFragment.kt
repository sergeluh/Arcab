package com.serg.arcab.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.*
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
import kotlinx.android.synthetic.main.list_item_places_pick.view.*
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

    private var pickDelegateAdapter: PickDelegateAdapter? = null

    private var nearByAdapter: PlaceDelegateAdapter? = null

    private var searchResultAdapter: PlaceDelegateAdapter? = null

    private var fromLatLng: LatLng? = null

    private var searchDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.text = resources.getString(R.string.button_continue)
        navBar.nextBtn.isEnabled = false
        navBar.nextBtn.setOnClickListener {
            viewModel.onHideKeyboard()
            //Initialize common points before common points screen appears
            viewModel.commonPoints = mutableListOf()
            FirebaseDatabase.getInstance().reference.child(COMMON_POINTS_FIREBASE_TABLE)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            for (snapshot in p0.children) {
                                viewModel.commonPoints?.add(snapshot.getValue(CommonPoint::class.java)!!)
                                viewModel.onGoToPickupPointClicked()
                            }
                        }

                    })
        }

        pickDelegateAdapter = PickDelegateAdapter {
            nearByAdapter?.removeSelectedItem()
            searchResultAdapter?.removeSelectedItem()
            when (it.pickTextView?.text) {
                "Current Location" -> {
                    viewModel.tripOrder.currentLocation = fromLatLng
                    Timber.d("Place is ${viewModel.tripOrder.currentLocation}")
                    if (viewModel.tripOrder.currentLocation != null) {
                        navBar.nextBtn.isEnabled = true
                    }
                }
                "Location on Map" -> {
                    showProgressBar()
                    val intent = Intent(context, LocationOnMapFragment::class.java)
                    startActivityForResult(intent, placePickerRequest)
                }
            }
        }

        nearByAdapter = PlaceDelegateAdapter {
            pickDelegateAdapter?.removeSelectedItem()
            searchResultAdapter?.removeSelectedItem()
            viewModel.tripOrder.currentLocation = it.latLng
            if (viewModel.tripOrder.currentLocation != null) {
                navBar.nextBtn.isEnabled = true
            }
        }

        searchResultAdapter = PlaceDelegateAdapter {
            pickDelegateAdapter?.removeSelectedItem()
            nearByAdapter?.removeSelectedItem()
            viewModel.tripOrder.currentLocation = it.latLng
            if (viewModel.tripOrder.currentLocation != null) {
                navBar.nextBtn.isEnabled = true
            }
        }

        adapter = SectionedCompositeAdapter.Builder()
                .add(Section.Builder()
                        .setHeader(HeaderDelegate())
                        .add(pickDelegateAdapter!!)
                        .build())
                .add(Section.Builder()
                        .setHeader(HeaderDelegate())
                        .add(nearByAdapter!!)
                        .build())
                .add(Section.Builder()
                        .setHeader(HeaderDelegate())
                        .add(searchResultAdapter!!)
                        .build())
                .build()

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        adapter.setHeaderDataInSection(0, getString(R.string.initial_setup_places_fragment_header_pick))
        adapter.setHeaderDataInSection(1, getString(R.string.initial_setup_places_fragment_header_nearby))
        adapter.setHeaderDataInSection(2, getString(R.string.initial_setup_places_fragment_header_search_result))
        adapter.swapDataInSection(0, listOf(getString(R.string.initial_setup_places_fragment_pick_current_location),
                getString(R.string.initial_setup_places_fragment_pick_location_on_map)))

        searchDisposable = RxTextView.textChanges(editText)
                .skipInitialValue()
                .debounce(350, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchQuery(it.toString())
                }, {

                })

    }

    override fun onResume() {
        super.onResume()
        locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
            override fun onSuccess(location: Location?) {
                super.onSuccess(location)
                location?.also {
                    fromLatLng = LatLng(it.latitude, it.longitude)
                    Timber.d("Current location from location manager is: $fromLatLng")
                    searchNearbyPlaces()
                }
            }
        })
    }

    //Picking places processing
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("Request nearby places: $requestCode, result - $resultCode")
        hideProgressBar()
        if (requestCode == placePickerRequest && resultCode == RESULT_OK) {
            val latitude = data?.getDoubleExtra(LocationOnMapFragment.LATITUDE, 0.0)
            val longitude = data?.getDoubleExtra(LocationOnMapFragment.LONGITUDE, 0.0)
            viewModel.tripOrder.currentLocation = LatLng(latitude!!, longitude!!)
            if (viewModel.tripOrder.currentLocation != null) {
                navBar.nextBtn.isEnabled = true
            }
            Timber.d("Result location: $latitude, $longitude")
        }
    }

    private fun searchQuery(query: String) {
        showProgressBar()
        if(query.isNotEmpty()){
            placesManager.setQuery(query, null, object : PlacesManager.Callback {
                override fun loading(isLoading: Boolean) {

                }

                override fun result(result: MutableList<AutocompletePrediction>) {
                    val list = mutableListOf<Place>()
                    result.forEach {
                        var place: Place?
                        placesManager.getPlaceById(it.placeId!!).addOnCompleteListener {
                            place = it.result[0]
                            list.add(place!!)
                            if (list.size == 5) {
                                adapter.swapDataInSection(2, list)
                                recyclerView.smoothScrollToPosition(adapter.itemCount)
                                hideProgressBar()
                            }
                            Timber.d("Found place is ${place?.address}(${place?.latLng})")
                        }
                    }

                }

                override fun emptyResult() {
                    adapter.clearDataInSection(2)
                    hideProgressBar()
                }
            })
        }else{
            adapter.clearDataInSection(2)
            hideProgressBar()
        }
    }

    private fun searchNearbyPlaces() {
        showProgressBar()
        Timber.d("Launching query")
        placesManager.setQuery("улица", fromLatLng?.getBounds(500), object : PlacesManager.Callback {
            override fun loading(isLoading: Boolean) {
                Timber.d("Loading: $isLoading")
            }

            override fun result(result: MutableList<AutocompletePrediction>) {
                Timber.d("Search nearby places result: $result")
                val list = mutableListOf<Place>()
                result.forEach {
                    var place: Place?
                    placesManager.getPlaceById(it.placeId!!).addOnCompleteListener {
                        place = it.result[0]
                        list.add(place!!)
                        if (list.size == 5) {
                            adapter.swapDataInSection(1, list)
                            hideProgressBar()
                        }
                        Timber.d("Found place is ${place?.address}(${place?.latLng})")
                    }.addOnFailureListener {
                        hideProgressBar()
                    }.addOnCanceledListener {
                        hideProgressBar()
                    }
                }
            }

            override fun emptyResult() {
                adapter.clearDataInSection(1)
                hideProgressBar()
            }
        })
    }

    private fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchDisposable?.dispose()
    }


    class HeaderDelegate : BaseHeaderFooterDelegateAdapter() {
        override fun getLayoutId(): Int {
            return R.layout.list_item_places_header
        }

        override fun bindViewHolder(holder: BaseViewHolder, item: DataHolder) {
            val text = item.data as String
            holder.hearedText.text = text
        }
    }

    class PickDelegateAdapter(val callback: (View) -> Unit) : BaseDelegateAdapter() {

        private var selectedItem: View? = null

        override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
            return true
        }

        override fun getLayoutId(): Int {
            return R.layout.list_item_places_pick
        }

        override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
            val text = dataHolder.data as String
            holder.pickTextView.text = text
            holder.itemView.setOnClickListener {
                callback(it)
                if (selectedItem != null) {
                    changeBackground(selectedItem!!)
                }
                changeBackground(it)
                selectedItem = it
            }
        }

        @Suppress("DEPRECATION")
        private fun changeBackground(v: View) {
            Timber.d("Current color = ${v.background}")
            val backGround = v.background
            if (backGround != null && backGround is ColorDrawable) {
                if (backGround.color == ColorDrawable(Color.LTGRAY).color) {
                    v.background = ColorDrawable(Color.TRANSPARENT)
                } else {
                    v.background = ColorDrawable(Color.LTGRAY)
                }
            } else {
                v.background = ColorDrawable(Color.LTGRAY)
            }
        }

        override fun removeSelectedItem() {
            if (selectedItem != null) {
                selectedItem!!.background = ColorDrawable(Color.TRANSPARENT)
                selectedItem = null
            }
        }
    }

    class PlaceDelegateAdapter(val callback: (Place) -> Unit) : BaseDelegateAdapter() {
        private var selectedItem: View? = null

        override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
            return items[position].data is Place
        }

        override fun getLayoutId(): Int {
            return R.layout.list_item_places_address
        }

        override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
            val prediction = dataHolder.data as Place
            holder.nameTextView.text = prediction.name
            holder.addressTextView.text = prediction.address
            holder.itemView.setOnClickListener {
                callback(prediction)
                if (selectedItem != null) {
                    selectedItem!!.background = ColorDrawable(Color.TRANSPARENT)
                }
                it.background = ColorDrawable(Color.LTGRAY)
                selectedItem = it
            }
        }

        override fun removeSelectedItem() {
            if (selectedItem != null) {
                selectedItem!!.background = ColorDrawable(Color.TRANSPARENT)
                selectedItem = null
            }
        }
    }


    companion object {

        const val TAG = "PlacesFragment"

        @JvmStatic
        fun newInstance() = PlacesFragment()
    }
}
