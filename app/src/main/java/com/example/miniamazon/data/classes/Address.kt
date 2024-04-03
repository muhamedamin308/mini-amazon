package com.example.miniamazon.data.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val homeTitle: String,
    val street: String,
    val city: String,
    val state: String,
    val fullName: String,
    val phone: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}
