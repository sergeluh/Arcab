package com.serg.arcab.recycleradapter

class DataHolder(val data: Any) {

    var listener: EventListener? = null

    fun change(params: Map<Int, Any>) {
        listener?.onChange(params)
    }

    interface EventListener {
        fun onChange(params: Map<Int, Any>)
    }
}