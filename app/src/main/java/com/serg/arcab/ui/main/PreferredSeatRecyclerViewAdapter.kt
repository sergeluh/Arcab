package com.serg.arcab.ui.main

import android.support.v7.widget.RecyclerView
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.serg.arcab.R
import com.serg.arcab.model.Seat
import kotlinx.android.synthetic.main.item_pickup_point.view.*


class PreferredSeatRecyclerViewAdapter(val callback: Callback, val seatList: MutableList<Seat>) : RecyclerView.Adapter<PreferredSeatRecyclerViewAdapter.ViewHolder>() {

    private var currentCheckedItem: CompoundButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferredSeatRecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_preferred_seat, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: PreferredSeatRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(seatList[position])
    }

    override fun getItemCount(): Int {
        return seatList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: Seat) {


            val btnSeat = itemView.findViewById(R.id.btnSeat) as CheckBox

            if(item.id.equals("1D") || item.id.equals("2C") || item.id.equals("3C")) {
                btnSeat.visibility = View.INVISIBLE
            }

            if(item.user_id != null) {
                btnSeat.isEnabled = false
            } else {
                btnSeat.setOnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked) {
                        changeChecked(buttonView)
                        callback.checkedChange(item)
                    } else {
                        currentCheckedItem = null
                        callback.checkedChange(null)
                    }

                }
            }

        }
        private fun changeChecked(checkBox: CompoundButton)
        {
            if(currentCheckedItem != null){
                currentCheckedItem!!.isChecked = false
            }
            currentCheckedItem = checkBox
        }
    }

    interface Callback {
        fun checkedChange(seat: Seat? = null)
    }
}