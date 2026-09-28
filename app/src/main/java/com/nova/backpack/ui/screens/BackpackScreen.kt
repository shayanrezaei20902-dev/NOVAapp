package com.nova.backpack.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.nova.backpack.data.*
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings
import java.io.File
import java.io.FileOutputStream

@Composable
fun BackpackScreen(viewModel: NovaViewModel) {
    val settings by viewModel.settings.collectAsState()
    val items by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val lang = settings.language
    fun s(key: String) = Strings.get(lang, key)
    fun ItemEntity.localName() = if (lang == "fa") nameFa else nameEn
    fun CategoryEntity.localName() = if (lang == "fa") nameFa else nameEn
    fun LocationEntity.localName() = if (lang == "fa") nameFa else nameEn

    var search by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("category") } // "category" | "location"
    var filter by remember { mutableStateOf("all") }
    var showAddItem by remember { mutableStateOf(false) }
    var showNewCategory by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ItemEntity?>(null) }

    val filtered = items.filter { item ->
        val matchesSearch = search.isBlank() || listOf(
            item.localName(), item.usageFa, item.usageEn, item.notes,
            categories.find { it.id == item.categoryId }?.localName() ?: "",
            locations.find { it.id == item.locationId }?.localName() ?: ""
        ).any { it.contains(search, ignoreCase = true) }
        val matchesFilter = when (filter) {
            "all" -> true
            "fav" -> item.favorite
            else -> if (mode == "category") item.categoryId == filter else item.locationId == filter
        }
        matchesSearch && matchesFilter
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                placeholder = { Text(s("searchPh")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == "category", onClick = { mode = "category"; filter = "all" }, label = { Text(s("byCategory")) })
                FilterChip(selected = mode == "location", onClick = { mode = "location"; filter = "all" }, label = { Text(s("byLocation")) })
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = filter == "all", onClick = { filter = "all" }, label = { Text(s("all")) })
                }
                item {
                    FilterChip(selected = filter == "fav", onClick = { filter = "fav" }, label = { Text("⭐") })
                }
                val chipSource = if (mode == "category") categories else locations
                items(chipSource) { c ->
                    val id = if (c is CategoryEntity) c.id else (c as LocationEntity).id
                    val icon = if (c is CategoryEntity) c.icon else (c as LocationEntity).icon
                    val label = if (c is CategoryEntity) c.localName() else (c as LocationEntity).localName()
                    FilterChip(selected = filter == id, onClick = { filter = id }, label = { Text("$icon $label") })
                }
                if (mode == "category") item {
                    AssistChip(onClick = { showNewCategory = true }, label = { Text(s("addCategory")) })
                }
            }
            Spacer(Modifier.height(8.dp))
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s("noItems")) }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered, key = { it.id }) { item ->
                        val cat = categories.find { it.id == item.categoryId }
                        val loc = locations.find { it.id == item.locationId }
                        ItemCard(item, cat, loc, lang) { selectedItem = item }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddItem = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) { Icon(Icons.Filled.Add, null) }
    }

    if (showAddItem) {
        AddEditItemDialog(viewModel, categories, locations, lang, existing = null, onDismiss = { showAddItem = false })
    }
    selectedItem?.let { item ->
        ItemDetailDialog(viewModel, item, categories, locations, lang,
            onDismiss = { selectedItem = null },
            onEdit = { selectedItem = null /* re-open in edit mode below */; })
    }
    if (showNewCategory) {
        NewCategoryDialog(onSave = { name, icon, colorHex ->
            viewModel.addCategory(CategoryEntity(id = "c${System.currentTimeMillis()}", icon = icon, colorHex = colorHex, nameFa = name, nameEn = name))
            showNewCategory = false
        }, onDismiss = { showNewCategory = false })
    }
}

@Composable
private fun ItemCard(item: ItemEntity, cat: CategoryEntity?, loc: LocationEntity?, lang: String, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item.photoUri != null) AsyncImage(model = Uri.parse(item.photoUri), contentDescription = null, modifier = Modifier.fillMaxSize())
                else Text(cat?.icon ?: "📦", fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(if (lang == "fa") item.nameFa else item.nameEn, style = MaterialTheme.typography.titleSmall)
                Text(
                    "📍 ${if (loc != null) (if (lang == "fa") loc.nameFa else loc.nameEn) else ""}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item.favorite) Text("⭐")
        }
    }
}

@Composable
private fun AddEditItemDialog(
    viewModel: NovaViewModel,
    categories: List<CategoryEntity>,
    locations: List<LocationEntity>,
    lang: String,
    existing: ItemEntity?,
    onDismiss: () -> Unit
) {
    fun s(key: String) = Strings.get(lang, key)
    val context = LocalContext.current
    var name by remember { mutableStateOf(existing?.let { if (lang == "fa") it.nameFa else it.nameEn } ?: "") }
    var categoryId by remember { mutableStateOf(existing?.categoryId ?: categories.firstOrNull()?.id ?: "") }
    var locationId by remember { mutableStateOf(existing?.locationId ?: locations.firstOrNull()?.id ?: "") }
    var usage by remember { mutableStateOf(existing?.let { if (lang == "fa") it.usageFa else it.usageEn } ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var favorite by remember { mutableStateOf(existing?.favorite ?: false) }
    var reminder by remember { mutableStateOf(existing?.reminderNote ?: "") }
    var photoUri by remember { mutableStateOf(existing?.photoUri) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { photoUri = it.toString() }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = File(context.filesDir, "item_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out -> it.compress(Bitmap.CompressFormat.PNG, 90, out) }
            photoUri = Uri.fromFile(file).toString()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Text(if (existing == null) s("addItem") else s("edit"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(60.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) AsyncImage(model = Uri.parse(photoUri), contentDescription = null, modifier = Modifier.fillMaxSize())
                        else Text("📦")
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        TextButton(onClick = { cameraLauncher.launch() }) { Text(s("takePhoto")) }
                        TextButton(onClick = { galleryLauncher.launch("image/*") }) { Text(s("chooseGallery")) }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(name, { name = it }, label = { Text(s("itemName")) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))

                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                    OutlinedTextField(
                        value = categories.find { it.id == categoryId }?.let { "${it.icon} ${if (lang=="fa") it.nameFa else it.nameEn}" } ?: "",
                        onValueChange = {}, readOnly = true, label = { Text(s("category")) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(catExpanded, { catExpanded = false }) {
                        categories.forEach { c ->
                            DropdownMenuItem(text = { Text("${c.icon} ${if (lang=="fa") c.nameFa else c.nameEn}") }, onClick = { categoryId = c.id; catExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                var locExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = locExpanded, onExpandedChange = { locExpanded = it }) {
                    OutlinedTextField(
                        value = locations.find { it.id == locationId }?.let { "${it.icon} ${if (lang=="fa") it.nameFa else it.nameEn}" } ?: "",
                        onValueChange = {}, readOnly = true, label = { Text(s("location")) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(locExpanded, { locExpanded = false }) {
                        locations.forEach { l ->
                            DropdownMenuItem(text = { Text("${l.icon} ${if (lang=="fa") l.nameFa else l.nameEn}") }, onClick = { locationId = l.id; locExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(usage, { usage = it }, label = { Text(s("usage")) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(notes, { notes = it }, label = { Text(s("notes")) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(favorite, { favorite = it }); Text(s("favorite"))
                }
                OutlinedTextField(reminder, { reminder = it }, label = { Text(s("reminder")) }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val now = System.currentTimeMillis()
                        if (existing == null) {
                            viewModel.saveItem(
                                ItemEntity(
                                    id = "it${now}", nameFa = name, nameEn = name, photoUri = photoUri,
                                    categoryId = categoryId, locationId = locationId, usageFa = usage, usageEn = usage,
                                    notes = notes, favorite = favorite, reminderNote = reminder.ifBlank { null },
                                    addedAtMillis = now, lastUsedMillis = now,
                                    history = listOf(HistoryEntry(locationId, now))
                                )
                            )
                        } else {
                            val locChanged = existing.locationId != locationId
                            viewModel.saveItem(
                                existing.copy(
                                    nameFa = name, nameEn = name, photoUri = photoUri, categoryId = categoryId,
                                    locationId = locationId, usageFa = usage, usageEn = usage, notes = notes,
                                    favorite = favorite, reminderNote = reminder.ifBlank { null },
                                    history = if (locChanged) existing.history + HistoryEntry(locationId, now) else existing.history
                                )
                            )
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(s("save")) }
            }
        }
    }
}

@Composable
private fun ItemDetailDialog(
    viewModel: NovaViewModel,
    item: ItemEntity,
    categories: List<CategoryEntity>,
    locations: List<LocationEntity>,
    lang: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    fun s(key: String) = Strings.get(lang, key)
    var editing by remember { mutableStateOf(false) }
    if (editing) {
        AddEditItemDialog(viewModel, categories, locations, lang, existing = item, onDismiss = { editing = false; onDismiss() })
        return
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Text(if (lang == "fa") item.nameFa else item.nameEn, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Text("${s("lastUsed")}: " + java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(java.util.Date(item.lastUsedMillis)))
                Text("${s("usage")}: " + (if (lang == "fa") item.usageFa else item.usageEn))
                if (item.notes.isNotBlank()) Text("${s("notes")}: ${item.notes}")
                if (item.reminderNote != null) Text("${s("reminder")}: ${item.reminderNote}")

                Spacer(Modifier.height(10.dp))
                Text(s("history"), style = MaterialTheme.typography.labelLarge)
                item.history.asReversed().forEach { h ->
                    val loc = locations.find { it.id == h.locationId }
                    Text("${loc?.icon ?: ""} ${if (lang=="fa") loc?.nameFa else loc?.nameEn} — " +
                        java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(java.util.Date(h.timeMillis)),
                        style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(14.dp))
                Button(onClick = { viewModel.markUsedNow(item); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text(s("usedNow")) }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { editing = true }, modifier = Modifier.fillMaxWidth()) { Text(s("edit")) }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.deleteItem(item); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text(s("delete")) }
            }
        }
    }
}

@Composable
private fun NewCategoryDialog(onSave: (name: String, icon: String, colorHex: String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("🏷") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(icon, { icon = it }, label = { Text("Icon") })
                Spacer(Modifier.height(12.dp))
                Button(onClick = { if (name.isNotBlank()) onSave(name, icon, "#7c8cff") }) { Text("Save") }
            }
        }
    }
}
