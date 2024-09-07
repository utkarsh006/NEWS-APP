package com.example.newsapp.UI.util

import android.view.View

fun View.handleVisibility(visible: Boolean) {
    if (visible) this.visibility = View.VISIBLE else this.visibility = View.GONE
}