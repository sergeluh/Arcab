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
import kotlinx.android.synthetic.main.fragment_util.view.*

class UtilFragment : DialogFragment() {

    companion object {
        const val TAG = "util_fragment"
    }

    var header = ""
    var message = ""
    var buttonText = ""
    var imageResource = 0
    var callback: Callback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = View.inflate(context, R.layout.fragment_util, null)
        val adb = AlertDialog.Builder(context!!)
        adb.setView(v)

        v.alert_header.text = header
        v.alert_message.text = message
        v.alert_button.text = buttonText
        v.alert_button.setOnClickListener {
            callback?.alertButtonClicked()
        }
        if (imageResource == 0){
            v.alert_image.visibility = View.GONE
        }else{
            v.alert_image.setImageResource(imageResource)
        }

        val dialog = adb.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        callback?.onDismiss()
    }

    interface Callback{
        fun alertButtonClicked()
        fun onDismiss()
    }
}
