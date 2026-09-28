package com.nova.backpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings

@Composable
fun HomeScreen(viewModel: NovaViewModel, navController: NavController) {
    val settings by viewModel.settings.collectAsState()
    val items by viewModel.items.collectAsState()
    val lang = settings.language
    fun s(key: String) = Strings.get(lang, key)

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.size(88.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) { Text("🎒", fontSize = 40.sp) }

        Spacer(Modifier.height(12.dp))
        Text(s("appName"), style = MaterialTheme.typography.headlineSmall)
        Text(s("tagline"), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

        Spacer(Modifier.height(10.dp))
        Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(
                "${items.size} " + (if (lang == "fa") "وسیله ثبت شده" else "items tracked"),
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val quick = listOf(
                Triple("⭐", s("favorites"), "backpack"),
                Triple("🚪", s("beforeGo"), "go"),
                Triple("📍", s("locations"), "backpack"),
                Triple("🔎", s("search"), "backpack"),
            )
            items(quick) { (icon, label, route) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(route) }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(icon, fontSize = 22.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
