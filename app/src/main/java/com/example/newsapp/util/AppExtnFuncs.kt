package com.example.newsapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.example.newsapp.NewsApplication

fun Context?.isNetworkConnected(): Boolean =
    (this?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)?.activeNetworkInfo?.run {
        val connBool = when (type) {
            ConnectivityManager.TYPE_WIFI,
            ConnectivityManager.TYPE_MOBILE ->
                true
            else ->
                false
        }
        connBool
    } ?: false


inline fun Any.showVLog(log: () -> String) =
    NLog.v("---" + this::class.java.simpleName, log())

inline fun Any.showELog(log: () -> String) =
    NLog.e("---" + this::class.java.simpleName, log())

inline fun Any.showDLog(log: () -> String) =
    NLog.d("---" + this::class.java.simpleName, log())

inline fun Any.showILog(log: () -> String) =
    NLog.i("---" + this::class.java.simpleName, log())

inline fun Any.showWLog(log: () -> String) =
    NLog.w("---" + this::class.java.simpleName, log())

object NLog {
    val DEBUG_BOOL = true

    fun v(tag: String, msg: String) {
        if (DEBUG_BOOL)
            Log.v(tag, msg)
    }

    fun e(tag: String, msg: String) {
        if (DEBUG_BOOL)
            Log.e(tag, msg)
    }

    fun d(tag: String, msg: String) {
        if (DEBUG_BOOL)
            Log.d(tag, msg)
    }

    fun i(tag: String, msg: String) {
        if (DEBUG_BOOL)
            Log.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        if (DEBUG_BOOL)
            Log.w(tag, msg)
    }
}

private var toast: Toast? = null
fun Any.showToast(
    context: Context? = NewsApplication.INSTANCE,
    duration: Int = Toast.LENGTH_SHORT
) {
    toast?.cancel()
    toast = when (this) {
        is String ->
            Toast.makeText(context, this, duration)
        is Int ->
            Toast.makeText(context, this, duration)
        else ->
            Toast.makeText(context, "Invalid input to Toast! :-(", duration)
    }
    toast?.show()
}