package com.nova.backpack.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import com.nova.backpack.MainActivity
import com.nova.backpack.data.AppDatabase
import kotlinx.coroutines.flow.first

/**
 * Real Android home-screen widget (Jetpack Glance) — shows the favorite items and
 * opens the app when tapped. This is Widget 2 ("⭐ Favorites") from the spec; the
 * other widgets (Backpack, Before You Go, Quick Search, Calendar) follow the same
 * pattern — a GlanceAppWidget + its own AppWidgetReceiver.
 */
class NovaGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)
        val favorites = db.itemDao().observeAll().first().filter { it.favorite }.take(3)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF171922))
                    .padding(12.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                Text(
                    "⭐ Favorites",
                    style = TextStyle(color = ColorProvider(Color(0xFFEEF0F6)))
                )
                Spacer(GlanceModifier.height(6.dp))
                if (favorites.isEmpty()) {
                    Text("No favorites yet", style = TextStyle(color = ColorProvider(Color(0xFF9AA0B4))))
                } else {
                    favorites.forEach { item ->
                        Text(
                            "• ${item.nameEn}",
                            style = TextStyle(color = ColorProvider(Color(0xFF9AA0B4)))
                        )
                    }
                }
            }
        }
    }
}
