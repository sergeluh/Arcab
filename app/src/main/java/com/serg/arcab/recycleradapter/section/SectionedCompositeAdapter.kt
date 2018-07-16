package com.serg.arcab.recycleradapter.section

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.serg.arcab.recycleradapter.BaseViewHolder
import com.serg.arcab.recycleradapter.DataHolder
import kotlinx.android.synthetic.main.list_item_places_address.view.*
import kotlinx.android.synthetic.main.list_item_places_pick.view.*
import timber.log.Timber

class SectionedCompositeAdapter
private constructor(private val sectionList: List<Section>): RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        for (section in sectionList) {
            val viewHolder = section.onCreateViewHolder(parent, viewType)
            if (viewHolder != null) {
                return viewHolder
            }
        }
        throw IllegalStateException("can not find adapter for viewType $viewType")
    }

    override fun getItemCount(): Int {
        Timber.d("getItemCount %s", sectionList.sumBy { it.size() })
        return sectionList.sumBy { it.size() }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getSectionForPosition(position).onBindViewHolder(holder, getLocalPositionInSection(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getSectionForPosition(position).getItemViewType(getLocalPositionInSection(position))
    }

    private fun getSectionForPosition(position: Int): ISection {
        var sectionStartPosition = 0
        for (section in sectionList) {
            val sectionTotalSize = section.size()
            if (position >= sectionStartPosition && position <= (sectionStartPosition + sectionTotalSize - 1)) {
                return section
            }
            sectionStartPosition += sectionTotalSize
        }
        throw IndexOutOfBoundsException("Invalid position")
    }

    private fun getLocalPositionInSection(globalPosition: Int): Int {
        var sectionStartPosition = 0
        for (section in sectionList) {
            val sectionTotalSize = section.size()
            if (globalPosition >= sectionStartPosition && globalPosition <= (sectionStartPosition + sectionTotalSize - 1)) {
                return globalPosition - sectionStartPosition
            }
            sectionStartPosition += sectionTotalSize
        }
        throw IndexOutOfBoundsException("Invalid position")
    }

    private fun getFirstGlobalPositionInSection(sectionPosition: Int): Int {
        return 0
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        for (section in sectionList) {
            val delegate = section.getDelegateAdapter(holder.itemViewType)
            if (delegate != null) {
                delegate.onRecycled(holder)
                return
            }
        }
    }

    /*fun removeAt(position: Int) {
        val section = getSectionForPosition(position)
        val positionInSection = getLocalPositionInSection(position)
        section.remove(positionInSection)
        notifyItemRemoved(position)
    }*/


    class Builder {

        private val sectionList = mutableListOf<Section>()

        fun add(section: Section): Builder {
            sectionList.add(section)
            return this
        }

        fun build(): SectionedCompositeAdapter {
            if (sectionList.size == 0) {
                throw IllegalArgumentException("Register at least one section")
            }
            return SectionedCompositeAdapter(sectionList)
        }
    }

    fun change(sectionNumber: Int, header: (DataHolder?) -> Unit, listData: (DataHolder?) -> Boolean, footer: (DataHolder?) -> Unit) {
        if (sectionNumber < sectionList.size) {
            sectionList[sectionNumber].change(header, listData, footer)
        }
    }

    fun changeHeader(sectionNumber: Int, header: (DataHolder?) -> Unit) {
        if (sectionNumber < sectionList.size) {
            sectionList[sectionNumber].changeHeader(header)
        }
    }

    fun changeFooter(sectionNumber: Int, footer: (DataHolder?) -> Unit) {
        if (sectionNumber < sectionList.size) {
            sectionList[sectionNumber].changeFooter(footer)
        }
    }

    fun changeData(sectionNumber: Int, listData: (DataHolder?) -> Boolean) {
        if (sectionNumber < sectionList.size) {
            sectionList[sectionNumber].changeData(listData)
        }
    }




    fun swapDataInSection(sectionPosition: Int, data: List<Any>?) {
        if (sectionList[sectionPosition].swapData(data)) {
            notifyDataSetChanged()
        }
    }

    fun setHeaderDataInSection(sectionPosition: Int, data: Any?) {
        if (sectionList[sectionPosition].setHeaderData(data)) {
            notifyDataSetChanged()
        }
    }

    fun setFooterDataInSection(sectionPosition: Int, data: Any) {
        if(sectionList[sectionPosition].setFooterData(data)) {
            notifyDataSetChanged()
        }
    }
}