package com.serg.arcab.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseDelegateAdapter: IDelegateAdapter {

    final override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(getLayoutId(), parent, false)
        return onCreateViewHolder(inflatedView)
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        holder.onBind(dataHolder)
    }

    open fun onCreateViewHolder(view: View): BaseViewHolder {
        return BaseViewHolder(view)
    }

    override fun onRecycled(holder: BaseViewHolder) {
        holder.onRecycled()
    }
}