package com.example.newsapp.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.newsapp.R

@BindingAdapter("newsImg")
fun loadImage(imageView: ImageView, url: String?) =
    Glide.with(imageView.context)
        .load(url)
        .error(R.drawable.ic_launcher_foreground)
        .into(imageView)

typealias BiCallBack<T, U> = (T, U) -> Unit