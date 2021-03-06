package com.serg.arcab.ui.main

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.serg.arcab.R
import com.serg.arcab.model.CommonPoint
import kotlinx.android.synthetic.main.item_pickup_point.view.*


class PointRecyclerViewAdapter(private val userList: MutableList<CommonPoint>, val view: RecyclerView) : RecyclerView.Adapter<PointRecyclerViewAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointRecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_point, parent, false)
        v.mapView.onCreate(null)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PointRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is holding the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: CommonPoint) {
            val textViewAddress = itemView.findViewById(R.id.textViewAddress) as TextView
            itemView.mapView.getMapAsync {
                it.setMapStyle(MapStyleOptions.loadRawResourceStyle(itemView.context, R.raw.map_style_arcab))
                val latLng = LatLng(item.latitude!!, item.longitude!!)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                it.addMarker(MarkerOptions().position(latLng))
            }
            val addressText = "From <b>${item.title}\n${item.address}</b>"
            textViewAddress.text = Html.fromHtml(addressText)

            val btnBack = itemView.findViewById(R.id.btnBack) as ImageView
            val btnNext = itemView.findViewById(R.id.btnNext) as ImageView

            if(itemCount < 2)
            {
                btnBack.visibility = View.INVISIBLE
                btnNext.visibility = View.INVISIBLE
            }

            btnBack.setOnClickListener{
                if(adapterPosition > 0)
                    view.smoothScrollToPosition(adapterPosition - 1)
            }
            btnNext.setOnClickListener{
                if(adapterPosition < itemCount - 1)
                    view.smoothScrollToPosition(adapterPosition + 1)
            }

        }
    }

//    fun getItem(position: Int) = LatLng(userList[position].latitude!!, userList[position].longitude!!)
}