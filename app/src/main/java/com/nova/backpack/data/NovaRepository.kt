package com.nova.backpack.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Single source of truth for items/categories/locations/collections.
 * Screens talk to this instead of touching Room directly.
 */
class NovaRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val itemDao = db.itemDao()
    private val categoryDao = db.categoryDao()
    private val locationDao = db.locationDao()
    private val collectionDao = db.collectionDao()

    val items: Flow<List<ItemEntity>> = itemDao.observeAll()
    val categories: Flow<List<CategoryEntity>> = categoryDao.observeAll()
    val locations: Flow<List<LocationEntity>> = locationDao.observeAll()
    val collections: Flow<List<CollectionEntity>> = collectionDao.observeAll()

    /** Populates the database with starter content the first time the app runs. */
    suspend fun seedIfEmpty() {
        if (categoryDao.observeAll().first().isEmpty()) categoryDao.insertAll(DefaultData.categories)
        if (locationDao.observeAll().first().isEmpty()) locationDao.insertAll(DefaultData.locations)
        if (collectionDao.observeAll().first().isEmpty()) collectionDao.insertAll(DefaultData.collections)
        if (itemDao.observeAll().first().isEmpty()) DefaultData.seedItems().forEach { itemDao.upsert(it) }
    }

    suspend fun saveItem(item: ItemEntity) = itemDao.upsert(item)
    suspend fun deleteItem(item: ItemEntity) = itemDao.delete(item)
    suspend fun addCategory(category: CategoryEntity) = categoryDao.upsert(category)
    suspend fun addCollection(collection: CollectionEntity) = collectionDao.upsert(collection)
    suspend fun updateCollection(collection: CollectionEntity) = collectionDao.upsert(collection)

    /** Marks an item as used right now and appends a history entry — called from "Used it now". */
    suspend fun markUsedNow(item: ItemEntity) {
        val now = System.currentTimeMillis()
        saveItem(item.copy(lastUsedMillis = now, history = item.history + HistoryEntry(item.locationId, now)))
    }

    /** Moves an item to a new location and logs the change in its history. */
    suspend fun changeLocation(item: ItemEntity, newLocationId: String) {
        val now = System.currentTimeMillis()
        saveItem(item.copy(locationId = newLocationId, history = item.history + HistoryEntry(newLocationId, now)))
    }
}
