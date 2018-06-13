package com.serg.arcab.ui.auth

import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.UserDataManager
import com.serg.arcab.utils.SingleLiveEvent

class AuthViewModel constructor(val userDataManager: UserDataManager): BaseViewModel() {

    val goToMobileNumberLogin = SingleLiveEvent<Unit>()
    val goToSocialLogin = SingleLiveEvent<Unit>()


    fun onEnterWithMobileClicked() {
        goToMobileNumberLogin.call()
    }

    fun onEnterWithSocialClicked() {
        goToSocialLogin.call()
    }
}