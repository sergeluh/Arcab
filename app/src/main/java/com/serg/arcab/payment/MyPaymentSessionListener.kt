package com.serg.arcab.payment

import android.content.Context
import android.widget.Toast
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentResultListener
import com.stripe.android.PaymentSession
import com.stripe.android.PaymentSessionData
import com.stripe.android.model.Customer

class MyPaymentSessionListener(val context: Context) : PaymentSession.PaymentSessionListener{
    override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
        if (isCommunicating){
            Toast.makeText(context, "MyPaymentSessionListener Communicating", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(context, "MyPaymentSessionListener Not communicating", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onError(errorCode: Int, errorMessage: String?) {
        Toast.makeText(context, "MyPaymentSessionListener Error: $errorMessage", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
        val paymentMethod= data.selectedPaymentMethodId
        CustomerSession.getInstance().retrieveCurrentCustomer(object : CustomerSession.CustomerRetrievalListener{
            override fun onCustomerRetrieved(customer: Customer) {
                val displaySource = customer.getSourceById(paymentMethod!!)
                //Display card information on the screen
            }

            override fun onError(errorCode: Int, errorMessage: String?) {

            }

        })

        if (data.isPaymentReadyToCharge && data.paymentResult == PaymentResultListener.INCOMPLETE){
            // Use the data to complete your charge
        }
    }

}