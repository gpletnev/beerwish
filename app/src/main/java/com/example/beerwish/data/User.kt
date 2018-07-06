package com.example.beerwish.data

import android.databinding.BaseObservable
import android.os.Parcel
import android.os.Parcelable

data class User(
        var uid: Int = 0,
        var user_name: String = "",
        var first_name: String = "",
        var last_name: String = "",
        var user_avatar: String = "",
        var location: String = "",
        var url: String = ""
): BaseObservable(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(user_name)
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(user_avatar)
        parcel.writeString(location)
        parcel.writeString(url)
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