package com.example.newsapp.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.newsapp.NewsApplication


@BindingAdapter("newsImg")
fun loadImage(imageView: ImageView, url: String) =
    Glide.with(NewsApplication.INSTANCE)
        .load(url)
        .into(imageView)

typealias CallBack<T> = (T) -> Unit

typealias BiCallBack<T, U> = (T, U) -> Unit