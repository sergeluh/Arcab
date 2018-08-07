package com.serg.arcab.ui.main


import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.Window
import android.widget.Toast
import com.serg.arcab.*
import com.serg.arcab.R
import com.serg.arcab.payment.KeyProvider
import com.stripe.android.*
import com.stripe.android.model.ShippingInformation

import com.stripe.android.model.Token
import com.stripe.android.view.PaymentFlowActivity.*
import kotlinx.android.synthetic.main.fragment_payment.view.*
import timber.log.Timber
import java.lang.Exception

class PaymentFragment : DialogFragment() {

    var tokenSuccessListener: TokenSuccessListener? = null
//    var receiver: BroadcastReceiver? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.fragment_payment, null)
        val adb = AlertDialog.Builder(context!!)
        adb.setView(view)

//        receiver = object : BroadcastReceiver(){
//            override fun onReceive(context: Context?, intent: Intent?) {
//               val shippingInformation = intent?.getParcelableExtra<ShippingInformation>(EXTRA_SHIPPING_INFO_DATA)
//                val shippingInfoProcessIntent = Intent(EVENT_SHIPPING_INFO_PROCESSED)
//                if (shippingInformation?.address == null){
//                    shippingInfoProcessIntent.putExtra(EXTRA_IS_SHIPPING_INFO_VALID, false)
//                }else{
//                    shippingInfoProcessIntent.putExtra(EXTRA_IS_SHIPPING_INFO_VALID, true)
////                    shippingInfoProcessIntent.putParcelableArrayListExtra(EXTRA_VALID_SHIPPING_METHODS, )
////                    shippingInfoProcessIntent.putExtra(EXTRA_DEFAULT_SHIPPING_METHOD, )
//                }
//                LocalBroadcastManager.getInstance(context!!).sendBroadcast(shippingInfoProcessIntent)
//            }
//        }
//
//        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver!!, IntentFilter(EVENT_SHIPPING_INFO_SUBMITTED))

        val sp = context!!.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val cardNumber = sp.getString(CARD_NUMBER, "")
        val cardExpirationMonth = sp.getInt(CARD_EXPIRATION_MONTH, 0)
        val cardExpirationYear = sp.getInt(CARD_EXPIRATION_YEAR, 0)
        val cardCVC = sp.getString(CARD_CVC, "")

        if (cardNumber.isNotEmpty() && cardExpirationMonth != 0 && cardExpirationYear != 0 && cardCVC.isNotEmpty()){
            view.card_input.setCardNumber(cardNumber)
            view.card_input.setExpiryDate(cardExpirationMonth, cardExpirationYear)
            view.card_input.setCvcCode(cardCVC)
        }

        view.ok_button.setOnClickListener {
            view.progressBar.visibility = View.VISIBLE
            val card = view.card_input.card
            card?.currency = "usd"
//            card?.
            if (card == null){
                view.progressBar.visibility = View.GONE
                Toast.makeText(context, "Card is null", Toast.LENGTH_SHORT).show()
            }else{
                if (!card.validateCard()){
                    view.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Card is invalid", Toast.LENGTH_SHORT).show()
                }else{
                    val editor = sp.edit()
                    editor.putString(CARD_NUMBER, card.number)
                    editor.putInt(CARD_EXPIRATION_MONTH, card.expMonth!!)
                    editor.putInt(CARD_EXPIRATION_YEAR, card.expYear)
                    editor.putString(CARD_CVC, card.cvc)
                    editor.apply()
                    Toast.makeText(context, "Your card is: ${card.number} ${card.expMonth}/${card.expYear} ${card.cvc}", Toast.LENGTH_LONG).show()
                    with(card){
                        Timber.d("Card data is: number $number, currency $currency, brand $brand, fingerprint $fingerprint, name $name")
                    }
                    val stripe = Stripe(context!!, "pk_test_g6do5S237ekq10r65BnxO6S0")
                    stripe.createToken(card, object : TokenCallback{
                        override fun onSuccess(token: Token?) {
                            view.progressBar.visibility = View.GONE
                            Toast.makeText(context, "Token success", Toast.LENGTH_SHORT).show()
                            tokenSuccessListener?.onTokenSuccess(token!!.id)
//                            CustomerSession.initCustomerSession(KeyProvider(object : KeyProvider.ProgressListener{
//                                override fun onKeyReceived(key: String) {
//
//                                }
//                            }))
//                            setupPaymentSEssion()
                            Timber.d("Token received: id - ${token?.id}, \ntype - ${token?.type}, \naccount holder name - ${token?.bankAccount?.accountHolderName}")
                            dismiss()
                        }

                        override fun onError(error: Exception?) {
                            view.progressBar.visibility = View.GONE
                            Toast.makeText(context, "Token error: ${error?.message}", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }
        }
        val dialog = adb.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

//    override fun onDetach() {
//        super.onDetach()
//        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
//    }

//    fun setupPaymentSEssion(){
//        val mPaymentSession = PaymentSession(context as Activity)
//        val paymentSessionInitialized = mPaymentSession.init(object : PaymentSession.PaymentSessionListener{
//            override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
//                if (isCommunicating){
//                    Toast.makeText(context, "PaymentFragment Communicating", Toast.LENGTH_SHORT).show()
//                } else{
//                    Toast.makeText(context, "PaymentFragment Not communicating", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onError(errorCode: Int, errorMessage: String?) {
//                Toast.makeText(context, "PaymentFragment Error: $errorMessage", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
//                Timber.d("PaymentFragment: payment data: ${mPaymentSession.paymentSessionData.paymentResult}, ${mPaymentSession.paymentSessionData.selectedPaymentMethodId}, ${mPaymentSession.paymentSessionData.cartTotal}")
//            }
//
//        }, PaymentSessionConfig.Builder()
//                .build())
//        if (paymentSessionInitialized){
//            mPaymentSession.completePayment { data, listener ->
//                Toast.makeText(context, "Payment data: ${data.selectedPaymentMethodId}, ${data.cartTotal}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    interface TokenSuccessListener{
        fun onTokenSuccess(tokenId: String)
    }
}
