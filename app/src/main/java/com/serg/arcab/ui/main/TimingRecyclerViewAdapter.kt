package com.serg.arcab.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.serg.arcab.R
import kotlinx.android.synthetic.main.item_pickup_point.view.*


class TimingRecyclerViewAdapter(val userList: MutableList<String>, val view: RecyclerView) : RecyclerView.Adapter<TimingRecyclerViewAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimingRecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_timing, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: TimingRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: String) {
            val textViewAddress = itemView.findViewById(R.id.textViewAddress) as TextView

            textViewAddress.text = item

            val btnBack = itemView.findViewById(R.id.btnBack) as ImageView
            val btnNext = itemView.findViewById(R.id.btnNext) as ImageView

            if(itemCount < 2)
            {
                btnBack.visibility = View.INVISIBLE
                btnNext.visibility = View.INVISIBLE
            }

            btnBack.setOnClickListener(View.OnClickListener {
                if(adapterPosition > 0)
                    view.smoothScrollToPosition(adapterPosition - 1)
            })
            btnNext.setOnClickListener(View.OnClickListener {
                if(adapterPosition < getItemCount() - 1)
                    view.smoothScrollToPosition(adapterPosition + 1)
            })

        }
    }
}