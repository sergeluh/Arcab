package com.serg.arcab.recycleradapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup

class SimpleCompositeAdapter
private constructor(private val typeToAdapterMap: SparseArray<IDelegateAdapter>):
        RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        private const val FIRST_VIEW_TYPE = 0
    }

    private var data = mutableListOf<DataHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return typeToAdapterMap.get(viewType).createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val delegateAdapter = typeToAdapterMap.get(holder.itemViewType)
        if (delegateAdapter != null) {
            delegateAdapter.bindViewHolder(holder, data[position], position)
        } else {
            throw NullPointerException("can not find adapter for position $position")
        }
    }

    override fun getItemViewType(position: Int): Int {
        for (i in FIRST_VIEW_TYPE until typeToAdapterMap.size()) {
            val delegate = typeToAdapterMap.valueAt(i)
            if (delegate.isForViewType(data, position)) {
                return typeToAdapterMap.keyAt(i)
            }
        }
        throw NullPointerException("Can not get viewType for position $position")
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        typeToAdapterMap.get(holder.itemViewType).onRecycled(holder)
    }






    fun swapData(data: List<Any>?) {
        data?.let {
            val newWrapperData = mutableListOf<DataHolder>()
            for (item in data) {
                newWrapperData.add(DataHolder(item))
            }

            this.data = newWrapperData
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class Builder {

        private var count: Int = 0
        private val typeToAdapterMap: SparseArray<IDelegateAdapter> = SparseArray()

        fun add(delegateAdapter: IDelegateAdapter): Builder {
            typeToAdapterMap.put(count++, delegateAdapter)
            return this
        }

        fun build(): SimpleCompositeAdapter {
            if (count == 0) {
                throw IllegalArgumentException("Register at least one adapter")
            }
            return SimpleCompositeAdapter(typeToAdapterMap)
        }
    }

    fun removeAt(position: Int) {
        val isRemoved = data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeOne(function: (Any) -> Boolean) {
        for(i in data.indices) {
            val item = data[i]
            if (function(item)) {
                removeAt(i)
                break
            }
        }
    }

    fun removeMultiple(function: Function1<Any, Boolean>) {
        for(i in data.indices.reversed()) {
            val item = data[i]
            if (function(item)) {
                removeAt(i)
            }
        }
    }

    fun add(item: Any?) {
        item?.let {
            data.add(DataHolder(it))
            notifyItemInserted(data.lastIndex)
        }
    }

    fun addToStart(item: Any?) {
        item?.let {
            data.add(0, DataHolder(it))
            notifyItemInserted(0)
        }
    }
}