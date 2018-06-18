package com.serg.arcab.recycleradapter.section

import android.view.ViewGroup
import com.serg.arcab.recycleradapter.BaseViewHolder

interface ISection {

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder?

    fun onBindViewHolder(holder: BaseViewHolder, position: Int)

    fun getItemViewType(position: Int): Int

    fun size(): Int

    fun hasHeader(): Boolean

    fun hasFooter(): Boolean

    fun remove(position: Int)
}