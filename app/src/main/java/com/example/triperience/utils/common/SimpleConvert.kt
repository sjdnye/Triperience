package com.example.triperience.utils.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object SimpleConvert {

    @SuppressLint("SimpleDateFormat")
    fun convertLongToDate(time : Long) : String{
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToLong(date: String) : Long {
        val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return df.parse(date)?.time ?: 0
    }
}