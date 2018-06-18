package com.serg.arcab.ui.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.R
import com.serg.arcab.recycleradapter.BaseDelegateAdapter
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder
import com.serg.arcab.recycleradapter.section.BaseHeaderFooterDelegateAdapter
import com.serg.arcab.recycleradapter.section.Section
import com.serg.arcab.recycleradapter.section.SectionedCompositeAdapter
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.list_item_places_header.*
import org.koin.android.architecture.ext.sharedViewModel
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.text.Editable
import android.text.TextWatcher
import com.google.android.gms.location.places.*
import com.google.android.gms.tasks.OnCompleteListener
import com.serg.arcab.PlacesManager
import kotlinx.android.synthetic.main.list_item_places_address.*
import kotlinx.android.synthetic.main.list_item_places_pick.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import timber.log.Timber


class PlacesFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    private lateinit var placesManager: PlacesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        placesManager = PlacesManager(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        val adapter = SectionedCompositeAdapter.Builder()
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

        adapter.setHeaderDataInSection(0, "Pick")
        adapter.setHeaderDataInSection(1, "Nearby")
        adapter.setHeaderDataInSection(2, "Search Results")
        adapter.swapDataInSection(0, listOf("Current Location", "Location on Map"))

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                placesManager.setQuery(s.toString(), object : PlacesManager.Callback {
                    override fun loading(isLoading: Boolean) {

                    }

                    override fun result(result: MutableList<AutocompletePrediction>) {
                        adapter.swapDataInSection(2, result)
                    }

                })
            }
        })
    }


    class HeaderDelegate(): BaseHeaderFooterDelegateAdapter() {
        override fun getLayoutId(): Int {
            return R.layout.list_item_places_header
        }

        override fun bindViewHolder(holder: BaseViewHolder, item: DataHolder) {
            Timber.d("bindViewHolder $item")
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
