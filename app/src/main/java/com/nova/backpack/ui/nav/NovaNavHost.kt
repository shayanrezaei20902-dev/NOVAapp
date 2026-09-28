package com.nova.backpack.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings
import com.nova.backpack.ui.screens.*

private data class Tab(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val labelKey: String)

private val tabs = listOf(
    Tab("home", Icons.Filled.Home, "navHome"),
    Tab("backpack", Icons.Filled.Backpack, "navBackpack"),
    Tab("calendar", Icons.Filled.CalendarMonth, "navCalendar"),
    Tab("go", Icons.Filled.DoorFront, "navGo"),
    Tab("settings", Icons.Filled.Settings, "navSettings"),
)

@Composable
fun NovaApp(viewModel: NovaViewModel) {
    val navController = rememberNavController()
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStack by navController.currentBackStackEntryAsState()
                val current = backStack?.destination
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(Strings.get(settings.language, tab.labelKey)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(viewModel, navController) }
            composable("backpack") { BackpackScreen(viewModel) }
            composable("calendar") { CalendarScreen(viewModel) }
            composable("go") { BeforeYouGoScreen(viewModel) }
            composable("settings") { SettingsScreen(viewModel) }
        }
    }
}
