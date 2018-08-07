package com.serg.arcab.ui.main.dialogs

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RatingBar

import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_up_next.view.*

class UpNextFragment : DialogFragment() {

    companion object {
        const val TAG = "upNextFragment"
    }

    var dismissListener: DismissListener? = null
    private var header = ""
    private var message = ""
    private var isOpen = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = View.inflate(context, R.layout.fragment_up_next, null)
        val adb = AlertDialog.Builder(context!!)
        adb.setView(v)

        v.header.text = header
        v.message.text = message

        val ratingContainer = View.inflate(context, R.layout.rating_layout, null)

        (v.root as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        v.button_open_close.setOnClickListener {
            isOpen = if (isOpen){
                v.container.removeAllViews()
                false
            }else{
                v.container.addView(ratingContainer)
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

    fun setFields(header: String, message: String){
        this.header = header
        this.message = message
    }

    interface DismissListener{
        fun onDismess()
    }
}
