package com.serg.arcab.ui.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.google.firebase.auth.PhoneAuthCredential
import com.serg.arcab.Result
import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.UserDataManager
import com.serg.arcab.utils.SingleLiveEvent

class AuthViewModel constructor(private val userDataManager: UserDataManager): BaseViewModel() {

    val goToMobileNumberLogin = SingleLiveEvent<Unit>()
    val goToMobileNumberFomSocial = SingleLiveEvent<Unit>()
    val goToSocialLogin = SingleLiveEvent<Unit>()
    val goToVerifyNumber = SingleLiveEvent<Unit>()
    val goToVerifyNumberFomSocial = SingleLiveEvent<Unit>()
    val goToNameInput = SingleLiveEvent<Unit>()
    val goToEmailInput = SingleLiveEvent<Unit>()
    val goToPasswordInput = SingleLiveEvent<Unit>()
    val goToBirthInput = SingleLiveEvent<Unit>()
    val goToIdInput = SingleLiveEvent<Unit>()
    val goToRules = SingleLiveEvent<Unit>()
    val goToFillInfoFromSocial = SingleLiveEvent<Unit>()
    val goToMain = SingleLiveEvent<Unit>()
    val backAction = SingleLiveEvent<Unit>()

    private var phoneNumber: String? = null
    private val verifyPhoneNumberTrigger = SingleLiveEvent<String?>()
    val verifyPhoneNumber: LiveData<Result<PhoneAuthCredential>> = Transformations.switchMap(verifyPhoneNumberTrigger) {
        userDataManager.verifyPhoneNumber(it)
    }

    fun onEnterWithMobileClicked() {
        goToMobileNumberLogin.call()
    }

    fun onEnterWithSocialClicked() {
        goToSocialLogin.call()
    }

    fun onGoToVerifyNumberScreenClicked() {
        goToVerifyNumber.call()
    }

    fun onGoToNameScreenClicked() {
        goToNameInput.call()
    }

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



    fun onBackClicked() {
        backAction.call()
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

    fun verifyPhoneNumber() {
        verifyPhoneNumberTrigger.value = phoneNumber
    }

    fun onPhoneInputChanged(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }
}