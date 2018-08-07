package com.serg.arcab.payment

import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener

/**
 * TODO must be implemented when endpoint will be ready
 */
class KeyProvider (private val progressListener: ProgressListener): EphemeralKeyProvider{
    override fun createEphemeralKey(apiVersion: String, keyUpdateListener: EphemeralKeyUpdateListener) {
        val apiMap = mutableMapOf(Pair("api_version", apiVersion))

        progressListener.onKeyReceived(apiVersion)
    }

    interface ProgressListener{
        fun onKeyReceived(key: String)
    }
}