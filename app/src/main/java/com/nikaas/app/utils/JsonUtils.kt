package com.nikaas.app.utils

import com.google.gson.Gson

object JsonUtils {
    private val gson = Gson()

    fun <T> toJson(src: T): String {
        return gson.toJson(src)
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }
}
