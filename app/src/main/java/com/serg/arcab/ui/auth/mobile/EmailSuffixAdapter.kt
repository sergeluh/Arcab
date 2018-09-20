package com.serg.arcab.ui.auth.mobile

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.serg.arcab.R
import kotlinx.android.synthetic.main.item_suffix_list.view.*

class EmailSuffixAdapter(private val sufixes: MutableList<String>, private val clickLsitener: (String)->Unit) : RecyclerView.Adapter<EmailSuffixAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_suffix_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = sufixes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sufixes[position], clickLsitener)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bind(suffix: String, clickLsitener: (String) -> Unit){
            itemView.item_suffix.text = suffix
            itemView.setOnClickListener {
                clickLsitener(suffix)
            }
        }
    }
}