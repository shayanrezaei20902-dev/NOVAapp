package com.nova.backpack.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings
import com.nova.backpack.ui.components.LiquidGlassSurface
import com.nova.backpack.ui.theme.NovaThemes

private val ANIMS = listOf("Smooth Fade", "Slide Up", "Slide Right", "Zoom", "Spring", "Liquid", "Blur Reveal", "Elastic")

@Composable
fun SettingsScreen(viewModel: NovaViewModel) {
    val settings by viewModel.settings.collectAsState()
    val lang = settings.language
    fun s(key: String) = Strings.get(lang, key)
    val context = LocalContext.current

    var showPinSetup by remember { mutableStateOf(false) }

    androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(s("navSettings"), style = MaterialTheme.typography.headlineSmall)
                SegmentedLangSwitch(lang) { viewModel.setLanguage(it) }
            }
            Spacer(Modifier.height(16.dp))
        }

        item { SectionCard(s("themes")) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(NovaThemes.builtIn) { theme ->
                    val active = settings.themeId == theme.id
                    Column(
                        Modifier
                            .background(theme.background, RoundedCornerShape(14.dp))
                            .clickable { viewModel.setTheme(theme.id) }
                            .padding(10.dp)
                    ) {
                        Box(Modifier.size(14.dp).background(theme.accent, CircleShape))
                        Spacer(Modifier.height(6.dp))
                        Text(theme.label, color = theme.text, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        } }

        item { SectionCard(s("liquidGlass")) {
            SwitchRow(s("glassLabel"), settings.liquidGlassEnabled) { viewModel.setLiquidGlass(it) }
            Spacer(Modifier.height(8.dp))
            LiquidGlassSurface(settings, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Box(Modifier.padding(18.dp), contentAlignment = Alignment.Center) { Text(s("glassDemo")) }
            }
        } }

        item { SectionCard(s("animation")) {
            SwitchRow(s("animOn"), settings.animationsEnabled) { viewModel.setAnimationsEnabled(it) }
            SwitchRow(s("reduceMotion"), settings.reduceMotion) { viewModel.setReduceMotion(it) }
            SwitchRow(s("haptic"), settings.hapticEnabled) { viewModel.setHaptic(it) }
            Spacer(Modifier.height(8.dp))
            Text(s("pageOpen"), style = MaterialTheme.typography.labelLarge)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded, { expanded = it }) {
                OutlinedTextField(
                    settings.pageOpenAnimation, {}, readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded, { expanded = false }) {
                    ANIMS.forEach { a -> DropdownMenuItem(text = { Text(a) }, onClick = { viewModel.setPageOpenAnimation(a); expanded = false }) }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(s("speed") + ": ${settings.animationSpeed}x", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = settings.animationSpeed, onValueChange = { viewModel.setSpeed(it) },
                valueRange = 0.5f..2f, steps = 5
            )
        } }

        item { SectionCard(s("lock")) {
            SwitchRow(s("lock"), settings.lockEnabled) { enabled ->
                if (enabled) showPinSetup = true else viewModel.setLock(false, null)
            }
        } }

        item {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(s("madeBy") + " ")
                Text(
                    "@dpoioIoioqb",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        val tgUri = Uri.parse("tg://resolve?domain=dpoioIoioqb")
                        val webUri = Uri.parse("https://t.me/dpoioIoioqb")
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, tgUri))
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    }
                )
            }
        }
    }

    if (showPinSetup) {
        PinSetupDialog(
            title = s("setPin"),
            onConfirm = { pin -> viewModel.setLock(true, pin); showPinSetup = false },
            onDismiss = { showPinSetup = false }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SegmentedLangSwitch(lang: String, onChange: (String) -> Unit) {
    Row {
        listOf("fa" to "فارسی", "en" to "EN").forEach { (id, label) ->
            FilterChip(selected = lang == id, onClick = { onChange(id) }, label = { Text(label) }, modifier = Modifier.padding(horizontal = 2.dp))
        }
    }
}

@Composable
private fun PinSetupDialog(title: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = pin, onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pin = it },
                singleLine = true
            )
        },
        confirmButton = { TextButton(onClick = { if (pin.length == 4) onConfirm(pin) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
