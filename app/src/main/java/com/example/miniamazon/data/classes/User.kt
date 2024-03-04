package com.example.miniamazon.data.classes

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val profile: String = ""
) {
    constructor(): this("", "", "", "")
}
