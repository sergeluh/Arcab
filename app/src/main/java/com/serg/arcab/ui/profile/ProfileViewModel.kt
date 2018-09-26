package com.serg.arcab.ui.profile

import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.ProfileDatamanager
import com.serg.arcab.utils.SingleLiveEvent

class ProfileViewModel constructor(private val profileDatamanager: ProfileDatamanager) : BaseViewModel(){
    val goToProfileDetails = SingleLiveEvent<Unit>()

    val backAction = SingleLiveEvent<Unit>()

    val user = profileDatamanager.getUser()
    val userDataProgress = profileDatamanager.getUserDataProgress()

    fun requestUserData(){
        profileDatamanager.requestUserData()
    }

    fun isUserEmpty(): Boolean = user.value == null
            || user.value?.email == null
            || user.value?.first_name == null
            || user.value?.last_name == null
            || user.value?.phone_number == null
}