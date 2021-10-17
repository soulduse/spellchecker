package com.dave.spellchecker.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonAdapter<LocalDateTime>() {
    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        writer.value(FORMATTER.format(value))
    }

    override fun fromJson(reader: JsonReader): LocalDateTime? {
        val value = reader.nextString()
        return LocalDateTime.parse(value, FORMATTER)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}
