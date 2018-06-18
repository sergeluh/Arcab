package com.serg.arcab.recycleradapter.section

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder

abstract class BaseHeaderFooterDelegateAdapter: IHeaderFooterDelegateAdapter {

    final override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(getLayoutId(), parent, false)
        return onCreateViewHolder(inflatedView)
    }

    open fun onCreateViewHolder(view: View): BaseViewHolder {
        return BaseViewHolder(view)
    }

    override fun bindViewHolder(holder: BaseViewHolder, item: DataHolder) {
        holder.onBind(item)
    }

    override fun onRecycled(holder: BaseViewHolder) {
        holder.onRecycled()
    }
}