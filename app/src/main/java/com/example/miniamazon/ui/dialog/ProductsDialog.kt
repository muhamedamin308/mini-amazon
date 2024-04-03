package com.example.miniamazon.ui.dialog

import android.app.AlertDialog
import android.content.Context
import com.example.miniamazon.R

fun Context.showAlertDialog(
    title: String,
    message: String,
    positiveButtonTitle: String = "OK",
    negativeButtonTitle: String,
    positiveAction: (() -> Unit)? = null
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonTitle) { dialog, _ ->
            positiveAction?.invoke()
            dialog.dismiss()
        }.setNegativeButton(negativeButtonTitle) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}