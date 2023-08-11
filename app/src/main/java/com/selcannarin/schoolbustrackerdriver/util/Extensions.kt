package com.selcannarin.schoolbustrackerdriver.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.selcannarin.schoolbustrackerdriver.R

fun ImageView.loadUrl(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.user_image)
        .circleCrop()
        .into(this)
}

