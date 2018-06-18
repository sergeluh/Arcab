package com.serg.arcab.recycleradapter.section

import android.support.annotation.LayoutRes
import android.view.ViewGroup
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder

interface IHeaderFooterDelegateAdapter {

    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    fun bindViewHolder(holder: BaseViewHolder, item: DataHolder)

    fun onRecycled(holder: BaseViewHolder)

    @LayoutRes
    fun getLayoutId(): Int
}