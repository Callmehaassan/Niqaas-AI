package com.nikaas.app.ui.common

import android.view.View
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun TextView.formatTime(timestamp: Long) {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    this.text = sdf.format(Date(timestamp))
}
