package com.example.miniamazon.util

import android.view.View
import android.widget.ProgressBar

fun View.gone() {
    visibility = View.GONE
}
fun View.show() {
    visibility = View.VISIBLE
}
fun View.hide() {
    visibility = View.INVISIBLE
}