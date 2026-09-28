package com.nova.backpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.nova.backpack.security.LockScreen
import com.nova.backpack.ui.NovaViewModel
import com.nova.backpack.ui.Strings
import com.nova.backpack.ui.nav.NovaApp
import com.nova.backpack.ui.theme.NovaTheme

/**
 * Single-activity app. Everything else is Composable screens navigated with
 * Jetpack Navigation-Compose (see ui/nav/NovaNavHost.kt).
 */
class MainActivity : androidx.fragment.app.FragmentActivity() {
    private val viewModel: NovaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.settings.collectAsState()
            var unlocked by remember { mutableStateOf(false) }

            NovaTheme(settings) {
                if (settings.lockEnabled && !unlocked) {
                    LockScreen(
                        storedPinHash = settings.pinHash,
                        unlockLabel = Strings.get(settings.language, "unlock"),
                        setPinLabel = Strings.get(settings.language, "setPin"),
                        onUnlocked = { unlocked = true }
                    )
                } else {
                    NovaApp(viewModel = viewModel)
                }
            }
        }
    }
}
