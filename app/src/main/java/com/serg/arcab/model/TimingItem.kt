package com.serg.arcab.model

/**
 * Item that will bo show in PickupTimingFragment by TimingRecyclerViewAdapter
 */
data class TimingItem(var time: String, var daysEnabled: MutableList<Boolean>,
                      var daysChecked: MutableList<Boolean> = mutableListOf(false, false, false, false, false, false, false),
                      var tripId: Int? = null)