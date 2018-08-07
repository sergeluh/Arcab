package com.serg.arcab.ui.main.dialogs


import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.Window

import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_alert.view.*

class AlertFragment : DialogFragment() {

    companion object {
        const val TAG = "alertFragment"
    }

    private var header: String = ""
    private var message: String = ""
    var dismissListener: DismissListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = View.inflate(context, R.layout.fragment_alert, null)
        val adb = AlertDialog.Builder(context!!)
        adb.setView(v)

        v.header.text = header
        v.message.text = message

        val dialog = adb.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissListener?.onDismess()
    }

    fun setHeaderAndMessage(header: String, message: String){
        this.header = header
        this.message = message
    }

    interface DismissListener {
        fun onDismess()
    }
}
