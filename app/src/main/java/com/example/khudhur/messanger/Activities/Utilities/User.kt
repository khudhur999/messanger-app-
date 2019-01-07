package com.example.khudhur.messanger.Activities.Utilities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid:String , val theusername : String, val profileImageUrl:String): Parcelable {
    constructor(): this ("","","")
}

