package com.example.miniamazon.util

import android.view.View
import android.widget.ProgressBar

fun ProgressBar.gone() {
    visibility = View.GONE
}
fun ProgressBar.show() {
    visibility = View.VISIBLE
}
fun ProgressBar.hide() {
    visibility = View.INVISIBLE
}