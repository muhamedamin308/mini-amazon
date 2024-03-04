package com.example.miniamazon.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty())
        return RegisterValidation.Failed("Email cannot be Empty!")
    if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches()))
        return RegisterValidation.Failed("Wrong Email Format!")
    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty())
        return RegisterValidation.Failed("Password cannot be Empty!")
    if (password.length < 6)
        return RegisterValidation.Failed("Password must be at least 6 characters!")
    return RegisterValidation.Success
}