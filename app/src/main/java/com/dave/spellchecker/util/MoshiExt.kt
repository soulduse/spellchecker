package com.dave.spellchecker.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import timber.log.Timber

inline fun <reified T> Moshi.parseList(jsonString: String): List<T>? {
    return adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java)).fromJson(
        jsonString,
    )
}

inline fun <reified T> Moshi.listToJson(list: List<T>): String {
    Timber.d("listToJson: $list")
    return adapter<List<T>>(
        Types.newParameterizedType(
            List::class.java,
            T::class.java,
        ),
    ).toJson(list)
}

inline fun <reified T> Moshi.parse(jsonString: String): T? {
    return adapter<T>(T::class.java).fromJson(jsonString)
}

inline fun <reified T> Moshi.toJson(obj: T): String {
    return adapter(T::class.java).toJson(obj)
}
