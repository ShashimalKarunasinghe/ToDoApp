package com.example.todo.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    fun formatTime(date: Date): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }
}
