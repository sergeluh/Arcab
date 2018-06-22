package com.serg.arcab.ui.auth

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.PhoneAuthCredential
import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.UserDataManager
import com.serg.arcab.utils.SingleLiveEvent

class AuthViewModel constructor(private val userDataManager: UserDataManager): BaseViewModel() {

    val goToMobileNumberLogin = SingleLiveEvent<Unit>()
    val goToMobileNumberFomSocial = SingleLiveEvent<Unit>()
    val goToSocialLogin = SingleLiveEvent<Unit>()
    val goToVerifyNumberFomSocial = SingleLiveEvent<Unit>()
    val goToEmailInput = SingleLiveEvent<Unit>()
    val goToPasswordInput = SingleLiveEvent<Unit>()
    val goToBirthInput = SingleLiveEvent<Unit>()
    val goToIdInput = SingleLiveEvent<Unit>()
    val goToRules = SingleLiveEvent<Unit>()
    val goToFillInfoFromSocial = SingleLiveEvent<Unit>()
    val goToMain = SingleLiveEvent<Unit>()
    val backAction = SingleLiveEvent<Unit>()

    private var phoneNumber: String? = null
    val verificationCode = MutableLiveData<String>()

    val user = userDataManager.getUser()
    val onCodeSentAction = userDataManager.getOnCodeSentAction()
    val onSignedInAction = userDataManager.getOnSignedInAction()
    val phoneVerification = userDataManager.getPhoneVerification()
    val firebaseUser = userDataManager.getFirebaseUser()

    fun onEnterWithMobileClicked() {
        goToMobileNumberLogin.call()
    }

    fun onEnterWithSocialClicked() {
        goToSocialLogin.call()
    }

    /*fun onGoToVerifyNumberScreenClicked() {
        onCodeSentAction.call()
    }*/

    /*fun onGoToNameScreenClicked() {
        onPhoneVerifiedAction.call()
    }*/

    fun onGoToEmailScreenClicked() {
        goToEmailInput.call()
    }

    fun onGoToPasswordScreenClicked() {
        goToPasswordInput.call()
    }

    fun onGoToBirthScreenClicked() {
        goToBirthInput.call()
    }

    fun onGoToVerifyIdScreenClicked() {
        goToIdInput.call()
    }

    fun onGoToRulesScreenClicked() {
        goToRules.call()
    }







    fun onLoginWithGoogleClicked() {
        goToFillInfoFromSocial.call()
    }

    fun onGoToNumberScreenFromSocialClicked() {
        goToMobileNumberFomSocial.call()
    }

    fun onGoToVerifyNumberScreenFromSocialClicked() {
        goToVerifyNumberFomSocial.call()
    }

    fun onSignUpCompleteClicked() {
        goToMain.call()
    }







    fun onPhoneInputChanged(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun onVerificationCodeInputChanged(value: String) {
        verificationCode.value = value
    }

    fun signIn() {
        userDataManager.createCredentialsWithCode(verificationCode.value)
    }

    fun verifyPhoneNumber() {
        userDataManager.verifyPhoneNumber(phoneNumber)
    }




    fun onBackClicked() {
        backAction.call()
    }
}