package com.nova.backpack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nova.backpack.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NovaViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NovaRepository(app)
    private val settingsRepo = SettingsRepository(app)

    val settings: StateFlow<AppSettings> = settingsRepo.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val items: StateFlow<List<ItemEntity>> = repo.items
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val categories: StateFlow<List<CategoryEntity>> = repo.categories
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val locations: StateFlow<List<LocationEntity>> = repo.locations
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val collections: StateFlow<List<CollectionEntity>> = repo.collections
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch { repo.seedIfEmpty() }
    }

    // ---- items ----
    fun saveItem(item: ItemEntity) = viewModelScope.launch { repo.saveItem(item) }
    fun deleteItem(item: ItemEntity) = viewModelScope.launch { repo.deleteItem(item) }
    fun markUsedNow(item: ItemEntity) = viewModelScope.launch { repo.markUsedNow(item) }
    fun changeLocation(item: ItemEntity, locationId: String) = viewModelScope.launch { repo.changeLocation(item, locationId) }
    fun addCategory(category: CategoryEntity) = viewModelScope.launch { repo.addCategory(category) }
    fun addCollection(collection: CollectionEntity) = viewModelScope.launch { repo.addCollection(collection) }
    fun updateCollection(collection: CollectionEntity) = viewModelScope.launch { repo.updateCollection(collection) }

    // ---- settings ----
    fun setLanguage(lang: String) = viewModelScope.launch { settingsRepo.setLanguage(lang) }
    fun setTheme(id: String) = viewModelScope.launch { settingsRepo.setTheme(id) }
    fun setCustomTheme(accentHex: String, bgHex: String) = viewModelScope.launch { settingsRepo.setCustomTheme(accentHex, bgHex) }
    fun setLiquidGlass(on: Boolean) = viewModelScope.launch { settingsRepo.setLiquidGlass(on) }
    fun setAnimationsEnabled(on: Boolean) = viewModelScope.launch { settingsRepo.setAnimationsEnabled(on) }
    fun setReduceMotion(on: Boolean) = viewModelScope.launch { settingsRepo.setReduceMotion(on) }
    fun setHaptic(on: Boolean) = viewModelScope.launch { settingsRepo.setHaptic(on) }
    fun setSpeed(speed: Float) = viewModelScope.launch { settingsRepo.setSpeed(speed) }
    fun setPageOpenAnimation(name: String) = viewModelScope.launch { settingsRepo.setPageOpenAnimation(name) }
    fun setLock(enabled: Boolean, pin: String?) = viewModelScope.launch {
        settingsRepo.setLock(enabled, pin?.let { com.nova.backpack.security.sha256(it) })
    }
}
