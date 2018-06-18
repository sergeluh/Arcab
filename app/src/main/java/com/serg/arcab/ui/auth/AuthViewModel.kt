package com.serg.arcab.ui.auth

import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.UserDataManager
import com.serg.arcab.utils.SingleLiveEvent

class AuthViewModel constructor(val userDataManager: UserDataManager): BaseViewModel() {

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
}