package com.serg.arcab.ui.main.dialogs


import android.animation.LayoutTransition
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.serg.arcab.R
import kotlinx.android.synthetic.main.achievement_details.view.*
import kotlinx.android.synthetic.main.fragment_achievment_linear.view.*

class AchievementFragment : DialogFragment() {

    companion object {
        const val TAG = "achievementFragment"
    }

    private var header: String = ""
    private var achievementIcon: Int? = null
    private var message: String = ""
    private var points: Int? = null
    private var image: Int? = null
    private var achievementMessage: String = ""
    var dismissListener: DismissListener? = null
    private var areDetailsShown = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = View.inflate(context, R.layout.fragment_achievment_linear, null)
        val adb = AlertDialog.Builder(context!!)
        adb.setView(v)

        v.header.text = header
        v.achievement_icon.setImageResource(achievementIcon!!)
        v.message.text = message
        v.points.text = "+$points"
        val container = View.inflate(context, R.layout.achievement_details, null)
        container.image.setImageResource(image!!)
        if (achievementMessage.isNotEmpty()) {
            container.achievement_message.text = achievementMessage
        } else {
            container.achievement_message.visibility = View.GONE
        }

        (v.root as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        v.button_open_close.setOnClickListener {
            areDetailsShown = if (areDetailsShown) {
                v.achievement_details_container.removeView(container)
                v.button_open_close.setImageResource(R.drawable.icon_arrow_down)
                false
            } else {
                v.achievement_details_container.addView(container)
                v.button_open_close.setImageResource(R.drawable.icon_arrow_up)
                true
            }
        }

        val dialog = adb.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissListener?.onDismess()
    }

    fun setFields(header: String, acievementIcon: Int, message: String, points: Int, image: Int, achievementMessage: String) {
        this.header = header
        this.achievementIcon = acievementIcon
        this.message = message
        this.points = points
        this.image = image
        this.achievementMessage = achievementMessage
    }

    interface DismissListener {
        fun onDismess()
    }
}
