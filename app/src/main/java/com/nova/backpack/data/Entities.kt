package com.nova.backpack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val icon: String,
    val colorHex: String,
    val nameFa: String,
    val nameEn: String
)

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String,
    val icon: String,
    val nameFa: String,
    val nameEn: String
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val id: String,
    val icon: String,
    val nameFa: String,
    val nameEn: String,
    val itemIds: List<String> = emptyList() // stored via TypeConverter as JSON
)

data class HistoryEntry(val locationId: String, val timeMillis: Long)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val nameFa: String,
    val nameEn: String,
    val photoUri: String? = null,      // content:// uri of the saved photo, null = show category icon
    val categoryId: String,
    val locationId: String,
    val usageFa: String = "",
    val usageEn: String = "",
    val notes: String = "",
    val favorite: Boolean = false,
    val reminderNote: String? = null,
    val reminderTimeMillis: Long? = null,
    val addedAtMillis: Long,
    val lastUsedMillis: Long,
    val history: List<HistoryEntry> = emptyList()
)
