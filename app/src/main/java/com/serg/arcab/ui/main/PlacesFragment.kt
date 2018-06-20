package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.AutocompletePrediction
import com.jakewharton.rxbinding2.widget.RxTextView
import com.serg.arcab.PlacesManager
import com.serg.arcab.R
import com.serg.arcab.recycleradapter.BaseDelegateAdapter
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder
import com.serg.arcab.recycleradapter.section.BaseHeaderFooterDelegateAdapter
import com.serg.arcab.recycleradapter.section.Section
import com.serg.arcab.recycleradapter.section.SectionedCompositeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.list_item_places_address.*
import kotlinx.android.synthetic.main.list_item_places_header.*
import kotlinx.android.synthetic.main.list_item_places_pick.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class PlacesFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    private val placesManager: PlacesManager by inject()

    private lateinit var adapter: SectionedCompositeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToPickupPointClicked()
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

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        adapter.setHeaderDataInSection(0, getString(R.string.pick))
        adapter.setHeaderDataInSection(1, getString(R.string.nearby))
        adapter.setHeaderDataInSection(2, getString(R.string.search_result))
        adapter.swapDataInSection(0, listOf(getString(R.string.current_location), getString(R.string.location_on_map)))

        RxTextView.textChanges(editText)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    search(it.toString())
                }, {

                })
    }

    private fun search(query: String) {
        placesManager.setQuery(query, null, object : PlacesManager.Callback {
            override fun loading(isLoading: Boolean) {

            }
            override fun result(result: MutableList<AutocompletePrediction>) {
                adapter.swapDataInSection(2, result)
            }
        })
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
