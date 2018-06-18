package com.serg.arcab.recycleradapter

import android.support.annotation.CallSuper
import android.view.View

open class BaseViewHolder(view: View): KViewHolder<DataHolder>(view) {

    private var dataHolder: DataHolder? = null

    @CallSuper
    override fun onBind(dataHolder: DataHolder) {
        this.dataHolder = dataHolder
        this.dataHolder?.listener = this
    }

    @CallSuper
    override fun onRecycled() {
        dataHolder?.listener = null
    }

    override fun onChange(params: Map<Int, Any>) {

    }
}