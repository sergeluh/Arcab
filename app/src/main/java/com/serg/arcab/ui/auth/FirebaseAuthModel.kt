package com.serg.arcab.ui.auth

import com.google.firebase.auth.PhoneAuthProvider

data class FirebaseAuthModel (
        var verificationId: String? = null,
        var token: PhoneAuthProvider.ForceResendingToken? = null)
