package com.serg.arcab.ui.auth

import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import android.util.Patterns
import com.serg.arcab.User
import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.AuthDataManager
import com.serg.arcab.utils.SingleLiveEvent
import java.util.*

class AuthViewModel constructor(private val authDataManager: AuthDataManager): BaseViewModel() {

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
    val backAction = SingleLiveEvent<Unit>()

    val phoneNumber = MutableLiveData<String>()
    var verificationCode: String? = null
    val name = MutableLiveData<Pair<String?, String?>>().apply {
        value = Pair(null, null)
    }
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val birth = MutableLiveData<Pair<Int?, Long?>>().apply {
        value = Pair(null, null)
    }
    val emiratesId = MutableLiveData<String>()
    val terms = MutableLiveData<User.Terms>().apply {
        value = User.Terms()
    }

    val emailValidation = SingleLiveEvent<String>()
    val passwordValidation = SingleLiveEvent<String>()
    val genderValidation = SingleLiveEvent<String>()
    val termsValidation = MutableLiveData<Boolean>().apply {
        value = false
    }

    val onCodeSentAction = authDataManager.getOnCodeSentAction()
    val onSignedInAction = authDataManager.getOnSignedInAction()
    val onProfileUploadedAction = authDataManager.getOnProfileUploadedAction()


    val user = authDataManager.getUser()
    val phoneVerificationModel = authDataManager.getPhoneVerificationModel()
    val firebaseUser = authDataManager.getFirebaseUser()
    val profileUpload = authDataManager.getProfileUpload()

    fun onBackClicked() {
        backAction.call()
    }

    fun onEnterWithMobileClicked() {
        goToMobileNumberLogin.call()
    }

    fun onEnterWithSocialClicked() {
        goToSocialLogin.call()
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








    fun signIn() {
        user.value?.phoneNumber = phoneNumber.value
        authDataManager.signInWithCode(verificationCode)
    }

    fun verifyPhoneNumber() {
        authDataManager.verifyPhoneNumber(phoneNumber.value)
    }

    fun saveName() {
        user.value?.firstName = name.value?.first
        user.value?.lastName = name.value?.second
        goToEmailInput.call()
    }

    fun saveEmail() {
        if (user.value?.email.isNullOrEmpty() || Patterns.EMAIL_ADDRESS.matcher(user.value?.email).matches()) {
            user.value?.email = email.value
            goToPasswordInput.call()
        } else {
            emailValidation.value = "Email is not valid"
        }
    }

    fun savePassword() {
        val pass = password.value
        if (pass == null || pass.isBlank()) {
            passwordValidation.value = "Enter password"
        } else if (pass.length < 8) {
            passwordValidation.value = "Password must include at least 8 characters"
        } else if (TextUtils.isDigitsOnly(pass)) {
            passwordValidation.value = "Password must include at least one symbol"
        } else {
            user.value?.password = password.value
            goToBirthInput.call()
        }
    }

    fun saveBirth() {
        when {
            birth.value?.first == null -> genderValidation.value = "Select gender"
            birth.value?.second == null -> genderValidation.value = "Select birth date"
            else -> {
                user.value?.gender = birth.value?.first
                user.value?.birthDate = birth.value?.second
                goToIdInput.call()
            }
        }
    }





    fun onPhoneInputChanged(phone: String) {
        user.value?.phoneNumber = phone
        phoneNumber.value = phone
    }

    fun onVerificationCodeInputChanged(code: String) {
        verificationCode = code
    }

    fun onFirstNameInputChanged(firstName: String) {
        user.value?.firstName = firstName
        name.value = name.value?.let {
            Pair(firstName, it.second)
        }
    }

    fun onLastNameInputChanged(lastName: String) {
        user.value?.lastName = lastName
        name.value = name.value?.let {
            Pair(it.first, lastName)
        }
    }

    fun onEmailInputChanged(value: String) {
        user.value?.email = value
        email.value = value
    }

    fun onPasswordInputChanged(value: String) {
        user.value?.password = value
        password.value = value
    }

    fun onGenderInputChanged(gender: Int) {
        user.value?.gender = gender
        birth.value = birth.value?.let {
            Pair(gender, it.second)
        }
    }

    fun onBirthDayInputChanged(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        user.value?.birthDate = calendar.timeInMillis
        birth.value = birth.value?.let {
            Pair(it.first, calendar.timeInMillis)
        }
    }

    fun setDirectlyContact(value: Boolean) {
        user.value?.terms?.directlyContact = value
        terms.value = terms.value?.also {
            it.directlyContact = value
        }
    }

    fun setSearchAds(value: Boolean) {
        user.value?.terms?.searchAds = value
        terms.value = terms.value?.also {
            it.searchAds = value
        }
    }

    fun setUsageData(value: Boolean) {
        user.value?.terms?.usageData = value
        terms.value = terms.value?.also {
            it.usageData = value
        }
    }

    fun setUsageStats(value: Boolean) {
        user.value?.terms?.usageStats = value
        terms.value = terms.value?.also {
            it.usageStats = value
        }
    }

    fun setTermsAgreement(value: Boolean) {
        termsValidation.value = value
    }

    fun onGetStartedClicked() {
        authDataManager.getStarted()
    }
}