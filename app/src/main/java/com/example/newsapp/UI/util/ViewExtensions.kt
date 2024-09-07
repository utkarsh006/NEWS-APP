package com.example.newsapp.UI.util

import android.view.View

fun View.handleVisibility(visible: Boolean) {
    if (visible) this.visibility = View.VISIBLE else this.visibility = View.GONE
}


object ProgressBarManager {

    fun hideProgressBar(progressBar: View, isLoadingSetter: (Boolean) -> Unit) {
        progressBar.handleVisibility(false)
        isLoadingSetter(false)
    }

    fun showProgressBar(progressBar: View, isLoadingSetter: (Boolean) -> Unit) {
        progressBar.handleVisibility(true)
        isLoadingSetter(true)
    }
}