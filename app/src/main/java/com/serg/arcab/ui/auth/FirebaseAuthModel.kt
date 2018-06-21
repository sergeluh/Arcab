package com.serg.arcab.ui.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


data class FirebaseAuthModel (var authCode: String? = null, var authId: PhoneAuthProvider.ForceResendingToken? = null,
                              var credentials: PhoneAuthCredential? = null, var authUser: FirebaseUser? = null)
