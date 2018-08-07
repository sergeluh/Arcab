package com.serg.arcab.ui.auth

import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import android.util.Patterns
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.serg.arcab.User
import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.datamanager.AuthDataManager
import com.serg.arcab.utils.SingleLiveEvent
import timber.log.Timber
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
    val goToScan = SingleLiveEvent<Unit>()
    val goToCapture = SingleLiveEvent<Unit>()
    val goToFillInfoFromSocial = SingleLiveEvent<Unit>()
    val backAction = SingleLiveEvent<Unit>()

    val phoneNumber = MutableLiveData<String>()
    var verificationCode: String? = null
    var captureMode: CaptureMode? = null
    var frontCapture: ByteArray? = null
    var backCapture: ByteArray? = null
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

    val phoneVerificationProgress = authDataManager.getPhoneVerificationProgress()
    val codeSentAction = authDataManager.getOnCodeSentAction()

    val codeVerificationProgress = authDataManager.getCodeVerificationProgress()
    val signedInAction = authDataManager.getSignedInAction()

    val profileUploadProgress = authDataManager.getProfileUploadProgress()
    val profileUploadedAction = authDataManager.getProfileUploadedAction()

    val checkPasswordProgress = authDataManager.getCheckPasswordProgress()
    val passwordCheckedAction = authDataManager.getPasswordCheckedAction()

    val user = authDataManager.getUser()




    fun onBackClicked() {
        backAction.call()
    }

    fun onEnterWithMobileClicked() {
        goToMobileNumberLogin.call()
    }

    fun onEnterWithSocialClicked() {
        goToSocialLogin.call()
    }









//    fun onGoToVerifyIdScreenClicked() {
//        goToIdInput.call()
//    }

    fun onGoToRulesScreenClicked() {
        goToRules.call()
    }

    fun onGoToScanClicked(){
        goToScan.call()
    }

    fun onGoToCaptureClicked(){
        goToCapture.call()
    }







//    fun onLoginWithGoogleClicked() {
//        goToFillInfoFromSocial.call()
//    }
//
//    fun onGoToNumberScreenFromSocialClicked() {
//        goToMobileNumberFomSocial.call()
//    }
//
//    fun onGoToVerifyNumberScreenFromSocialClicked() {
//        goToVerifyNumberFomSocial.call()
//    }








    fun signIn() {
        user.value?.phone_number = phoneNumber.value
        authDataManager.signInWithCode(verificationCode)
    }

    fun verifyPhoneNumber() {
        authDataManager.verifyPhoneNumber(phoneNumber.value)
    }

    fun saveName() {
        user.value?.first_name = name.value?.first
        user.value?.last_name = name.value?.second
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

    fun saveNameAndEmail() {
        if (user.value?.email.isNullOrEmpty() || Patterns.EMAIL_ADDRESS.matcher(user.value?.email).matches()) {
            user.value?.email = email.value
            user.value?.first_name = name.value?.first
            user.value?.last_name = name.value?.second
            goToMobileNumberFomSocial.call()
        } else {
            emailValidation.value = "Email is not valid"
        }
    }

    fun savePassword() {
        if (validatePassword()) {
            user.value?.password = password.value
            goToBirthInput.call()
        }
    }

    fun checkPassword() {
        if (validatePassword()) {
            authDataManager.checkPassword(password.value)
        }
    }

    private fun validatePassword(): Boolean {
        val pass = password.value
        if (pass == null || pass.isBlank()) {
            passwordValidation.value = "Enter password"
        } else if (pass.length < 8) {
            passwordValidation.value = "Password must include at least 8 characters"
        } else if (TextUtils.isDigitsOnly(pass)) {
            passwordValidation.value = "Password must include at least one symbol"
        } else {
            return true
        }

        return false
    }

    fun saveBirth() {
        when {
            birth.value?.first == null -> genderValidation.value = "Select gender"
            birth.value?.second == null -> genderValidation.value = "Select birth date"
            else -> {
                user.value?.gender = birth.value?.first
                user.value?.birth_date = birth.value?.second
                goToIdInput.call()
            }
        }
    }

    fun setDataFromGoogleAccount(account: GoogleSignInAccount) {
        user.value?.first_name = account.givenName
        user.value?.last_name = account.familyName
        user.value?.email = account.email

        name.value = Pair(account.givenName, account.familyName)
        email.value = account.email

        goToFillInfoFromSocial.call()
    }



    fun onPhoneInputChanged(phone: String) {
        user.value?.phone_number = phone
        phoneNumber.value = phone
    }

    fun onVerificationCodeInputChanged(code: String) {
        verificationCode = code
    }

    fun onFirstNameInputChanged(firstName: String) {
        user.value?.first_name = firstName
        name.value = name.value?.let {
            Pair(firstName, it.second)
        }
    }

    fun onLastNameInputChanged(lastName: String) {
        user.value?.last_name = lastName
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

        user.value?.birth_date = calendar.timeInMillis
        birth.value = birth.value?.let {
            Pair(it.first, calendar.timeInMillis)
        }
    }

    fun setDirectlyContact(value: Boolean) {
        user.value?.terms?.directly_contact = value
        terms.value = terms.value?.also {
            it.directly_contact = value
        }
    }

    fun setSearchAds(value: Boolean) {
        user.value?.terms?.search_ads = value
        terms.value = terms.value?.also {
            it.search_ads = value
        }
    }

    fun setUsageData(value: Boolean) {
        user.value?.terms?.usage_data = value
        terms.value = terms.value?.also {
            it.usage_data = value
        }
    }

    fun setUsageStats(value: Boolean) {
        user.value?.terms?.usage_stats = value
        terms.value = terms.value?.also {
            it.usage_stats = value
        }
    }

    fun setTermsAgreement(value: Boolean) {
        termsValidation.value = value
    }

    fun onGetStartedClicked() {
        authDataManager.getStarted()
    }

    enum class CaptureMode{
        FRONT, BACK
    }
}