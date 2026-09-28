package com.nova.backpack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nova.backpack.data.CollectionEntity
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings

@Composable
fun BeforeYouGoScreen(viewModel: NovaViewModel) {
    val settings by viewModel.settings.collectAsState()
    val items by viewModel.items.collectAsState()
    val collections by viewModel.collections.collectAsState()
    val lang = settings.language
    fun s(key: String) = Strings.get(lang, key)

    // packed-state only needs to last for this "trip", so it's kept in memory (per
    // spec's checklists are reset each time, not permanent history).
    val checklist = remember { mutableStateMapOf<String, MutableSet<String>>() }
    var showNew by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(collections, key = { it.id }) { coll ->
            val collItems = coll.itemIds.mapNotNull { id -> items.find { it.id == id } }
            val checked = checklist.getOrPut(coll.id) { mutableStateSetOf() }
            var expanded by remember { mutableStateOf(false) }

            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        Modifier.fillMaxWidth().clickable { expanded = !expanded },
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${coll.icon} " + (if (lang == "fa") coll.nameFa else coll.nameEn), style = MaterialTheme.typography.titleSmall)
                        Text("${checked.size}/${collItems.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (expanded) {
                        Spacer(Modifier.height(8.dp))
                        if (collItems.isEmpty()) {
                            Text(s("noItems"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            collItems.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = item.id in checked,
                                        onCheckedChange = { on -> if (on) checked.add(item.id) else checked.remove(item.id) }
                                    )
                                    Text(if (lang == "fa") item.nameFa else item.nameEn)
                                }
                            }
                            TextButton(onClick = { checked.clear() }) { Text(s("reset")) }
                        }
                    }
                }
            }
        }
        item {
            OutlinedButton(onClick = { showNew = true }, modifier = Modifier.fillMaxWidth()) { Text(s("newCollection")) }
        }
    }

    if (showNew) {
        NewCollectionDialog(items, lang, onSave = { name, icon, ids ->
            viewModel.addCollection(CollectionEntity(id = "g${System.currentTimeMillis()}", icon = icon, nameFa = name, nameEn = name, itemIds = ids))
            showNew = false
        }, onDismiss = { showNew = false })
    }
}

@Composable
private fun NewCollectionDialog(
    items: List<com.nova.backpack.data.ItemEntity>,
    lang: String,
    onSave: (String, String, List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("🎒") }
    val selected = remember { mutableStateSetOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text(Strings.get(lang, "itemName")) })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(icon, { icon = it }, label = { Text("Icon") })
                Spacer(Modifier.height(10.dp))
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(item.id in selected, { on -> if (on) selected.add(item.id) else selected.remove(item.id) })
                        Text(if (lang == "fa") item.nameFa else item.nameEn)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = { if (name.isNotBlank()) onSave(name, icon, selected.toList()) }) { Text(Strings.get(lang, "save")) }
            }
        }
    }
}
