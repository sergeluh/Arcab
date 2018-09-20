package com.serg.arcab.data

import android.content.SharedPreferences
import com.serg.arcab.User

interface PrefsManager {
    fun setAppWasOpen(wasOpen: Boolean)
    fun wasAppOpen(): Boolean

    fun setRescheduleTimings(reschedule: Boolean)
    fun getRescheduleTimings(): Boolean

    fun saveUser(user: User)
    fun getUser(): User
}

class PrefsManagerImpl(private val prefs: SharedPreferences) : PrefsManager {

    companion object {
        private const val WAS_APP_OPEN = "was_app_open"
        private const val RESCHEDULE = "reschedule"

        private const val KEY_BIRTH_DATE = "birth date"
        private const val KEY_EMAIL = "email"
        private const val KEY_EMIRATES_ID = "emirates id"
        private const val KEY_FIRST_NAME = "first name"
        private const val KEY_LAST_NAME = "last anme"
        private const val KEY_FLAG_IS_INITIAL_SETUP_COMPLETE = "is initial setup complete"
        private const val KEY_FLAG_IS_REGISTERED = "is registered"
        private const val KEY_GENDER = "gender"
        private const val KEY_PHONE_NUMBER = "phone number"
        private const val KEY_TERMS_DIRECTLY_CONTACT = "directly contact"
        private const val KEY_TERMS_SEARCH_ADS = "search ads"
        private const val KEY_TERMS_USAGE_DATA = "usage data"
        private const val KEY_TERMS_USAGE_STATS = "usage stats"
    }

    override fun setAppWasOpen(wasOpen: Boolean) {
        prefs.edit().putBoolean(WAS_APP_OPEN, wasOpen).apply()
    }

    override fun wasAppOpen(): Boolean {
        return prefs.getBoolean(WAS_APP_OPEN, false)
    }

    override fun setRescheduleTimings(reschedule: Boolean) {
        prefs.edit().putBoolean(RESCHEDULE, reschedule).apply()
    }

    override fun getRescheduleTimings(): Boolean {
        return prefs.getBoolean(RESCHEDULE, true)
    }

    override fun saveUser(user: User) {
        val editor = prefs.edit()
        user.birth_date?.also {
            editor.putLong(KEY_BIRTH_DATE, it)
        }
        user.email?.also {
            editor.putString(KEY_EMAIL, it)
        }
        user.emirates_id?.also {
            editor.putString(KEY_EMIRATES_ID, it)
        }
        user.first_name?.also {
            editor.putString(KEY_FIRST_NAME, it)
        }
        user.last_name?.also {
            editor.putString(KEY_LAST_NAME, it)
        }
        editor.putBoolean(KEY_FLAG_IS_INITIAL_SETUP_COMPLETE, user.flags.isInitialSetupComplete)
        editor.putBoolean(KEY_FLAG_IS_REGISTERED, user.flags.isRegistered)
        user.gender?.also {
            editor.putInt(KEY_GENDER, it)
        }
        user.phone_number?.also {
            editor.putString(KEY_PHONE_NUMBER, it)
        }
        editor.putBoolean(KEY_TERMS_DIRECTLY_CONTACT, user.terms.directly_contact)
        editor.putBoolean(KEY_TERMS_SEARCH_ADS, user.terms.search_ads)
        editor.putBoolean(KEY_TERMS_USAGE_DATA, user.terms.usage_data)
        editor.putBoolean(KEY_TERMS_USAGE_STATS, user.terms.usage_stats)
        editor.apply()
    }

    override fun getUser(): User {
        return User(
                prefs.getLong(KEY_BIRTH_DATE, 0),
                prefs.getString(KEY_EMAIL, ""),
                prefs.getString(KEY_EMIRATES_ID, ""),
                prefs.getString(KEY_FIRST_NAME, ""),
                prefs.getString(KEY_LAST_NAME, ""),
                User.Flags(
                        prefs.getBoolean(KEY_FLAG_IS_INITIAL_SETUP_COMPLETE, false),
                        prefs.getBoolean(KEY_FLAG_IS_REGISTERED, false)
                ),
                prefs.getInt(KEY_GENDER, 0),
                prefs.getString(KEY_PHONE_NUMBER, ""),
                "",
                User.Terms(
                        prefs.getBoolean(KEY_TERMS_DIRECTLY_CONTACT, false),
                        prefs.getBoolean(KEY_TERMS_SEARCH_ADS, false),
                        prefs.getBoolean(KEY_TERMS_USAGE_DATA, false),
                        prefs.getBoolean(KEY_TERMS_USAGE_STATS, false)
                )
        )
    }
}