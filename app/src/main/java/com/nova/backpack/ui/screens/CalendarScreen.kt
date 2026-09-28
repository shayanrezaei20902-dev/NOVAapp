package com.nova.backpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nova.backpack.data.CategoryEntity
import com.nova.backpack.data.ItemEntity
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings
import java.text.DateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

private data class CalEvent(val time: Long, val item: ItemEntity, val isAdded: Boolean)

/**
 * Uses the device's real clock (java.time / System.currentTimeMillis) — never a
 * counter of "days since first opened the app" — exactly like the spec requires.
 * Item add/use/move timestamps are read straight from Room, which stores whatever
 * System.currentTimeMillis() was at the moment the action happened.
 */
@Composable
fun CalendarScreen(viewModel: NovaViewModel) {
    val settings by viewModel.settings.collectAsState()
    val items by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val lang = settings.language
    fun s(key: String) = Strings.get(lang, key)

    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf(LocalDate.now()) }

    val zone = ZoneId.systemDefault()
    val events = remember(items, month) {
        val list = mutableListOf<CalEvent>()
        items.forEach { item ->
            val addedDate = Date(item.addedAtMillis).toInstant().atZone(zone).toLocalDate()
            if (YearMonth.from(addedDate) == month) list += CalEvent(item.addedAtMillis, item, true)
            item.history.forEach { h ->
                val d = Date(h.timeMillis).toInstant().atZone(zone).toLocalDate()
                if (YearMonth.from(d) == month) list += CalEvent(h.timeMillis, item, false)
            }
        }
        list
    }
    fun eventsOn(date: LocalDate) = events.filter {
        Date(it.time).toInstant().atZone(zone).toLocalDate() == date
    }.sortedBy { it.time }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { month = month.minusMonths(1) }) { Icon(Icons.Filled.ChevronLeft, null) }
            Text("${month.month} ${month.year}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { month = month.plusMonths(1) }) { Icon(Icons.Filled.ChevronRight, null) }
        }
        Spacer(Modifier.height(8.dp))

        val firstDay = month.atDay(1)
        val leadingBlanks = firstDay.dayOfWeek.value % 7 // Sunday-first grid
        val daysInMonth = month.lengthOfMonth()

        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(260.dp)) {
            items(leadingBlanks) { Box(Modifier.padding(4.dp)) }
            items(daysInMonth) { i ->
                val date = month.atDay(i + 1)
                val hasEvents = eventsOn(date).isNotEmpty()
                val isSelected = date == selected
                val isToday = date == LocalDate.now()
                Box(
                    Modifier
                        .padding(3.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selected = date }
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${i + 1}", color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        if (hasEvents) Box(Modifier.size(4.dp).background(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(50)
                        ))
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(selected.toString(), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))
        val dayEvents = eventsOn(selected)
        if (dayEvents.isEmpty()) {
            Text(if (lang == "fa") "رویدادی ثبت نشده" else "No events logged", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            dayEvents.forEach { ev ->
                val cat = categories.find { it.id == ev.item.categoryId }
                Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(ev.time)), modifier = Modifier.width(70.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${cat?.icon ?: "📦"} ${if (lang == "fa") ev.item.nameFa else ev.item.nameEn} — " + (if (ev.isAdded) (if (lang=="fa") "ثبت شد" else "added") else (if (lang=="fa") "جابه‌جا شد" else "moved")))
                }
            }
        }
    }
}
