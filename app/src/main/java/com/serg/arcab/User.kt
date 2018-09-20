package com.serg.arcab

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class User() : Parcelable{
        constructor(birthDAte: Long, email: String, emiratesId: String, firstName: String,
                    lastName: String, flags: Flags, gender: Int, password: String,
                    phoneNumber: String, terms: Terms) : this(){
                this.birth_date = birthDAte
                this.email = email
                this.emirates_id = emiratesId
                this.first_name = firstName
                this.last_name = lastName
                this.flags = flags
                this.gender = gender
                this.password = password
                this.phone_number = phoneNumber
                this.terms = terms
        }

        @SerializedName("birth_date")
        var birth_date: Long? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("emirates_id")
        var emirates_id: String? = null

        @SerializedName("first_name")
        var first_name: String? = null

        @SerializedName("last_name")
        var last_name: String? = null

        @SerializedName("flags")
        var flags: Flags = Flags()

        @SerializedName("gender")
        var gender: Int? = null

        @SerializedName("password")
        var password: String? = null

        @SerializedName("phone_number")
        var phone_number: String? = null

        @SerializedName("terms")
        var terms: Terms = Terms()

        constructor(parcel: Parcel) : this() {
                birth_date = parcel.readValue(Long::class.java.classLoader) as? Long
                email = parcel.readString()
                emirates_id = parcel.readString()
                first_name = parcel.readString()
                last_name = parcel.readString()
                flags = parcel.readParcelable(Flags::class.java.classLoader)
                gender = parcel.readValue(Int::class.java.classLoader) as? Int
                password = parcel.readString()
                phone_number = parcel.readString()
                terms = parcel.readParcelable(Terms::class.java.classLoader)
        }

        class Flags() : Parcelable {

                constructor(isRegistered: Boolean, isInitialSetupComplete: Boolean) : this(){
                        this.isRegistered = isRegistered
                        this.isInitialSetupComplete = isInitialSetupComplete
                }
                @SerializedName("isInitialSetupComplete")
                var isInitialSetupComplete: Boolean = false

                @SerializedName("isRegistered")
                var isRegistered: Boolean = false

                constructor(parcel: Parcel) : this() {
                        isInitialSetupComplete = parcel.readByte() != 0.toByte()
                        isRegistered = parcel.readByte() != 0.toByte()
                }

                override fun writeToParcel(parcel: Parcel, flags: Int) {
                        parcel.writeByte(if (isInitialSetupComplete) 1 else 0)
                        parcel.writeByte(if (isRegistered) 1 else 0)
                }

                override fun describeContents(): Int {
                        return 0
                }

                companion object CREATOR : Parcelable.Creator<Flags> {
                        override fun createFromParcel(parcel: Parcel): Flags {
                                return Flags(parcel)
                        }

                        override fun newArray(size: Int): Array<Flags?> {
                                return arrayOfNulls(size)
                        }
                }
        }

        class Terms() : Parcelable{

                constructor(directlyContact: Boolean, searchAds: Boolean, usageData: Boolean, usageStats: Boolean) : this(){
                        this.directly_contact = directlyContact
                        this.search_ads = searchAds
                        this.usage_data = usageData
                        this.usage_stats = usageStats
                }
                @SerializedName("directly_contact")
                var directly_contact: Boolean = false

                @SerializedName("search_ads")
                var search_ads: Boolean = false

                @SerializedName("usage_data")
                var usage_data: Boolean = false

                @SerializedName("usage_stats")
                var usage_stats: Boolean = false

                constructor(parcel: Parcel) : this() {
                        directly_contact = parcel.readByte() != 0.toByte()
                        search_ads = parcel.readByte() != 0.toByte()
                        usage_data = parcel.readByte() != 0.toByte()
                        usage_stats = parcel.readByte() != 0.toByte()
                }

                override fun writeToParcel(parcel: Parcel, flags: Int) {
                        parcel.writeByte(if (directly_contact) 1 else 0)
                        parcel.writeByte(if (search_ads) 1 else 0)
                        parcel.writeByte(if (usage_data) 1 else 0)
                        parcel.writeByte(if (usage_stats) 1 else 0)
                }

                override fun describeContents(): Int {
                        return 0
                }

                companion object CREATOR : Parcelable.Creator<Terms> {
                        override fun createFromParcel(parcel: Parcel): Terms {
                                return Terms(parcel)
                        }

                        override fun newArray(size: Int): Array<Terms?> {
                                return arrayOfNulls(size)
                        }
                }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(birth_date)
                parcel.writeString(email)
                parcel.writeString(emirates_id)
                parcel.writeString(first_name)
                parcel.writeString(last_name)
                parcel.writeParcelable(this.flags, flags)
                parcel.writeValue(gender)
                parcel.writeString(password)
                parcel.writeString(phone_number)
                parcel.writeParcelable(terms, flags)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<User> {
                override fun createFromParcel(parcel: Parcel): User {
                        return User(parcel)
                }

                override fun newArray(size: Int): Array<User?> {
                        return arrayOfNulls(size)
                }
        }
}