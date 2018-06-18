package com.serg.arcab.recycleradapter.section

import android.util.SparseArray
import android.view.ViewGroup
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder
import com.serg.arcab.recycleradapter.IDelegateAdapter
import java.util.concurrent.atomic.AtomicInteger

open class Section(private val delegateAdapters: SparseArray<IDelegateAdapter>,
                   private val headerDelegateAdapter: Pair<Int, IHeaderFooterDelegateAdapter>? = null,
                   private val footerDelegateAdapter: Pair<Int, IHeaderFooterDelegateAdapter>? = null) : ISection {

    private var data = mutableListOf<DataHolder>()

    private var headerData: DataHolder? = null

    private var footerData: DataHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder? {
        return when (viewType) {
            headerDelegateAdapter?.first -> headerDelegateAdapter.second.createViewHolder(parent, viewType)
            footerDelegateAdapter?.first -> footerDelegateAdapter.second.createViewHolder(parent, viewType)
            else -> delegateAdapters.get(viewType)?.createViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (headerDelegateAdapter?.first == holder.itemViewType) {
            headerDelegateAdapter.second.bindViewHolder(holder, headerData!!)
        } else if (footerDelegateAdapter?.first == holder.itemViewType) {
            footerDelegateAdapter.second.bindViewHolder(holder, footerData!!)
        } else {
            val positionForCollection = getPositionForCollection(position)
            val delegateAdapter = delegateAdapters.get(holder.itemViewType)
            if (delegateAdapter != null) {
                delegateAdapter.bindViewHolder(holder, data[positionForCollection], positionForCollection)
            } else {
                throw NullPointerException("can not find adapter for position $position")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (hasHeader() && position == 0) {
            return headerDelegateAdapter!!.first
        } else if (hasFooter() && position == size() - 1) {
            return footerDelegateAdapter!!.first
        } else {
            val positionForCollection = getPositionForCollection(position)
            for (i in 0 until delegateAdapters.size()) {
                val delegate = delegateAdapters.valueAt(i)
                if (delegate.isForViewType(data, positionForCollection)) {
                    return delegateAdapters.keyAt(i)
                }
            }
        }
        throw NullPointerException("Can not get viewType for position $position")
    }

    private fun getPositionForCollection(totalPosition: Int): Int {
        return if (hasHeader()) {
            totalPosition - 1
        } else {
            totalPosition
        }
    }

    override fun size(): Int {
        val header = if (hasHeader()) 1 else 0
        val footer = if (hasFooter()) 1 else 0
        return data.size + header + footer
    }

    fun getDelegateAdapter(viewType: Int): IDelegateAdapter? {
        return delegateAdapters.get(viewType)
    }

    fun swapData(data: List<Any>?): Boolean {
        return if (data == null) {
            false
        } else {
            val newWrapperData = mutableListOf<DataHolder>()
            for (item in data) {
                newWrapperData.add(DataHolder(item))
            }

            this.data = newWrapperData
            true
        }
    }

    fun setHeaderData(data: Any?): Boolean {
        return if (headerDelegateAdapter != null && data != null) {
            headerData = DataHolder(data)
            true
        } else {
            false
        }
    }

    fun setFooterData(data: Any?): Boolean {
        return if (footerDelegateAdapter != null && data != null) {
            footerData = DataHolder(data)
            true
        } else {
            false
        }
    }

    override fun  hasHeader(): Boolean {
        return headerData?.data != null
    }

    override fun  hasFooter(): Boolean {
        return footerData?.data != null
    }

    override fun remove(position: Int) {
        data.removeAt(position)
    }

    fun change(header: (DataHolder?) -> Unit, listData: (DataHolder?) -> Boolean, footer: (DataHolder?) -> Unit) {
        header.invoke(headerData)
        for (item in this.data) {
            if (listData.invoke(item)) {
                break
            }
        }
        footer.invoke(footerData)
    }

    fun changeHeader(header: (DataHolder?) -> Unit) {
        header.invoke(headerData)
    }

    fun changeFooter(footer: (DataHolder?) -> Unit) {
        footer.invoke(footerData)
    }

    fun changeData(listData: (DataHolder?) -> Boolean) {
        for (item in this.data) {
            if (listData.invoke(item)) {
                break
            }
        }
    }


    class Builder {

        private val delegateAdapters: SparseArray<IDelegateAdapter> = SparseArray()

        private var headerDelegateAdapter: Pair<Int, IHeaderFooterDelegateAdapter>? = null

        private var footerDelegateAdapter: Pair<Int, IHeaderFooterDelegateAdapter>? = null

        fun add(delegateAdapter: IDelegateAdapter): Builder {
            delegateAdapters.put(ViewType.nextNumber, delegateAdapter)
            return this
        }

        fun setHeader(headerDelegateAdapter: IHeaderFooterDelegateAdapter): Builder {
            this.headerDelegateAdapter = Pair(ViewType.nextNumber, headerDelegateAdapter)
            return this
        }

        fun setFooter(footerDelegateAdapter: IHeaderFooterDelegateAdapter): Builder {
            this.footerDelegateAdapter = Pair(ViewType.nextNumber, footerDelegateAdapter)
            return this
        }

        fun build(): Section {
            if (delegateAdapters.size() == 0) {
                throw IllegalArgumentException("Register at least one adapter")
            }
            return Section(delegateAdapters, headerDelegateAdapter, footerDelegateAdapter)
        }

        private object ViewType {

            private val counter = AtomicInteger(0)

            val nextNumber: Int
                get() = counter.incrementAndGet()
        }
    }
}
