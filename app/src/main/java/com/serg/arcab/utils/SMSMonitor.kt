package com.serg.arcab.utils

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SMSMonitor : BroadcastReceiver() {
    companion object {
        val verificationCode = MutableLiveData<String>()
        var isRegistered = true
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            messages.forEach {
                val re = Regex("[^0-9]")
                val code = re.replace(it.messageBody, "")
                if (code.length == 6){
                    verificationCode.value = code
                    isRegistered = false
                    context?.unregisterReceiver(this)
                    return@forEach
                }
            }
        }
    }
}