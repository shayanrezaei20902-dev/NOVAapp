package com.nova.backpack.security

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.security.MessageDigest

fun sha256(text: String): String =
    MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        .joinToString("") { "%02x".format(it) }

/**
 * Shown instead of the app when Settings > Lock is enabled. Tries fingerprint/face
 * first (if the device supports it and the user has one enrolled), and always offers
 * the 4-digit PIN as a fallback — exactly like the spec's "PIN + Biometric" requirement.
 */
@Composable
fun LockScreen(
    storedPinHash: String?,
    unlockLabel: String,
    setPinLabel: String,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val activity = context as? ComponentActivity as? androidx.fragment.app.FragmentActivity
        if (activity != null && BiometricAuthHelper.canUseBiometrics(activity)) {
            BiometricAuthHelper.prompt(
                activity = activity,
                title = unlockLabel,
                subtitle = setPinLabel,
                negativeButtonText = "PIN",
                onSuccess = onUnlocked,
                onError = { /* user can still fall back to typing the PIN below */ }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(72.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text("🎒", fontSize = androidx.compose.ui.unit.TextUnit.Unspecified) }

        Spacer(Modifier.height(20.dp))
        Text(setPinLabel, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) { i ->
                Box(
                    Modifier.size(12.dp).background(
                        if (i < pin.length) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                )
            }
        }
        if (error) {
            Spacer(Modifier.height(8.dp))
            Text("✕", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))
        val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            keys.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { k ->
                        OutlinedButton(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            onClick = {
                                if (k == "") return@OutlinedButton
                                pin = if (k == "⌫") pin.dropLast(1) else if (pin.length < 4) pin + k else pin
                                if (pin.length == 4) {
                                    if (sha256(pin) == storedPinHash) onUnlocked()
                                    else { error = true; pin = "" }
                                } else error = false
                            }
                        ) { Text(k) }
                    }
                }
            }
        }
    }
}
