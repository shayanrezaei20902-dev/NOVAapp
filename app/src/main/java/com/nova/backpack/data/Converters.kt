package com.nova.backpack.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer

/** Room can only store primitive columns, so lists are (de)serialized to/from JSON text. */
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromHistoryList(list: List<HistoryEntry>): String =
        json.encodeToString(ListSerializer(HistoryEntryX.serializer()), list.map { HistoryEntryX(it.locationId, it.timeMillis) })

    @TypeConverter
    fun toHistoryList(raw: String): List<HistoryEntry> =
        if (raw.isBlank()) emptyList()
        else json.decodeFromString(ListSerializer(HistoryEntryX.serializer()), raw).map { HistoryEntry(it.locationId, it.timeMillis) }

    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(raw: String): List<String> =
        if (raw.isBlank()) emptyList() else json.decodeFromString(serializer(), raw)
}

@kotlinx.serialization.Serializable
private data class HistoryEntryX(val locationId: String, val timeMillis: Long)
