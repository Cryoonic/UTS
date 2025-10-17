package com.example.uts.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.R

enum class ToastType {
    SUCCESS, ERROR, INFO
}

fun showCustomToast(context: Context, message: String, type: ToastType = ToastType.INFO, activity: Activity? = null) {
    val inflater = LayoutInflater.from(activity ?: context)
    val layout = inflater.inflate(R.layout.custom_toast, null)

    val container = layout.findViewById<LinearLayout>(R.id.toastContainer)
    val text = layout.findViewById<TextView>(R.id.tvToastMessage)
    text.text = message

    val bgColor = when (type) {
        ToastType.SUCCESS -> context.getColor(R.color.success_green)
        ToastType.ERROR -> context.getColor(R.color.error_red)
        ToastType.INFO -> context.getColor(R.color.info_blue)
    }
    container.setBackgroundColor(bgColor)


    val fadeIn = AlphaAnimation(0f, 1f).apply {
        duration = 400
        fillAfter = true
    }


    val fadeOut = AlphaAnimation(1f, 0f).apply {
        startOffset = 1800
        duration = 400
        fillAfter = true
    }

    layout.startAnimation(fadeIn)
    layout.startAnimation(fadeOut)


    with(Toast(context.applicationContext)) {
        duration = Toast.LENGTH_SHORT
        view = layout
        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        show()
    }
}
