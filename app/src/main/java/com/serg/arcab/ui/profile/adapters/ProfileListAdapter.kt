package com.serg.arcab.ui.profile.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.R
import com.serg.arcab.ui.profile.model.ProfileListItem
import kotlinx.android.synthetic.main.profile_list_item.view.*

class ProfileListAdapter(private val items: MutableList<ProfileListItem>, private val clickListener: (ProfileListItem) -> Unit) : RecyclerView.Adapter<ProfileListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(item: ProfileListItem, clickListener: (ProfileListItem) -> Unit){
            itemView.header.text = item.title
            itemView.summary.text = item.summary
            itemView.message.text = item.message
            if (item.image == null){
                itemView.image.visibility = View.GONE
            }else{
                itemView.image.setImageResource(item.image)
            }
            itemView.action_button.text = item.actionName
            itemView.action_button.setOnClickListener {
                clickListener(item)
            }
        }
    }
}