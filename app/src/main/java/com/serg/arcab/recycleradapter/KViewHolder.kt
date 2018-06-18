package com.serg.arcab.recycleradapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

abstract class KViewHolder<DH>(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer, DataHolder.EventListener {

    abstract fun onBind(dataHolder: DH)

    abstract fun onRecycled()

    abstract override fun onChange(params: Map<Int, Any>)
}