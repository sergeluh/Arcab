package com.serg.arcab.ui.main

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.serg.arcab.R
import com.serg.arcab.model.TimingItem


class TimingRecyclerViewAdapter(private val userList: MutableList<TimingItem>, val view: RecyclerView, val header: String) : RecyclerView.Adapter<TimingRecyclerViewAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimingRecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_timing, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: TimingRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position], position)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is holding the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: TimingItem, position: Int) {
            val textViewAddress = itemView.findViewById(R.id.textViewAddress) as TextView

            //Block that populates data of every week day button and binds it data to the TimingItem list that stores in trip model
            itemView.findViewById<ToggleButton>(R.id.tog_sunday).also {
                toggleButtonProcessing(it, position, 0)
            }
            itemView.findViewById<ToggleButton>(R.id.tog_monday).also {
                toggleButtonProcessing(it, position, 1)
            }
            itemView.findViewById<ToggleButton>(R.id.tog_tuesday).also {
                toggleButtonProcessing(it, position, 2)
            }
            itemView.findViewById<ToggleButton>(R.id.tog_wednesday).also {
                toggleButtonProcessing(it, position, 3)
            }
            itemView.findViewById<ToggleButton>(R.id.tog_thursday).also {
                toggleButtonProcessing(it, position, 4)
            }
            itemView.findViewById<ToggleButton>(R.id.tog_friday).also {
                toggleButtonProcessing(it, position, 5)
            }
            itemView.findViewById<ToggleButton>(R.id.tog_saturday).also {
                toggleButtonProcessing(it, position, 6)
            }

            val str = String.format(header, item.time)
            val spannableString = SpannableStringBuilder(str)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), str.length - item.time.length, str.length, 0)
            textViewAddress.text = spannableString

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

    //Method for populating data of week day button and binds it data to the TimingItem list that stores in trip model
    private fun toggleButtonProcessing(button: ToggleButton, position: Int, buttonIndex: Int){
        button.isEnabled = userList[position].daysEnabled[buttonIndex]
        button.isChecked = userList[position].daysChecked[buttonIndex]
        button.setOnCheckedChangeListener{_, isChecked ->
            userList[position].daysChecked[buttonIndex] = isChecked
        }
    }

    fun getItem(position: Int) = userList[position]

    fun getItems() = userList
}