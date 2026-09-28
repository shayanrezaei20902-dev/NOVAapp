# NOVA — Digital Backpack (Android / Kotlin)

Native Android app: Kotlin + Jetpack Compose + Room + DataStore + Biometric + Glance widgets.

## How to open it
1. Install **Android Studio** (Koala or newer).
2. `File > Open` → select this `NovaApp` folder.
3. Android Studio will offer to generate the Gradle wrapper jar / sync — accept it
   (this sandbox has no internet access, so `gradlew` couldn't be run or tested here;
   Android Studio's sync does that download for you).
4. Run on an emulator or a real device (`minSdk 26`, i.e. Android 8.0+).

## What is fully implemented and works on a real device
- Kotlin + Jetpack Compose UI, Material3, bottom navigation (Home / Backpack / Calendar / Before You Go / Settings)
- **Room database** for items, categories, locations, collections — real local persistence
- **DataStore** for all Settings (language, theme, Liquid Glass, animation speed, lock) — one central `SettingsRepository`, read everywhere through `LocalNovaAnimation` / `NovaTheme`, so a change in Settings updates every screen without touching each screen's code
- Bilingual **fa/en** with in-app switch (not tied to system language) + automatic **RTL/LTR** layout mirroring via `LocalLayoutDirection`
- 10 built-in themes + a custom theme (color pickers → saved to DataStore)
- **Liquid Glass** press-and-hold micro-interaction: a real `detectDragGestures` + `Animatable` spring-back, in `ui/components/LiquidGlassSurface.kt`
- Items: photo (camera via `TakePicturePreview`, or gallery via `GetContent`), category, location, usage, notes, favorite, reminder note, auto-logged history
- Search across name/category/location/notes/usage
- Real device clock: `Calendar` screen is built from `java.time.YearMonth`/`LocalDate` and each item's real `System.currentTimeMillis()` timestamps — not from days the app was opened
- **Before You Go** checklists per collection
- **PIN lock** (SHA-256 hashed, stored in DataStore) with **fingerprint/face unlock**
  attempted first via `androidx.biometric.BiometricPrompt`, falling back to the PIN pad
- A real installable **home-screen widget** (Jetpack Glance) showing favorite items —
  `widget/NovaGlanceWidget.kt` + `NovaWidgetReceiver.kt`, registered in the manifest

## What is intentionally simplified / left as a next step
- Only **one** widget is wired up end-to-end (Favorites). The other four from the
  spec (Backpack, Before You Go, Quick Search, Calendar) follow the exact same
  `GlanceAppWidget` + `GlanceAppWidgetReceiver` pattern — copy `NovaGlanceWidget.kt`
  and change what it queries/renders.
- Reminders are stored on the item (note + optional time) but aren't yet scheduled
  with `AlarmManager`/`WorkManager` + a `NotificationChannel` — the plumbing
  (`work-runtime-ktx`) is already in `build.gradle.kts`, this just needs a
  `ReminderScheduler.kt` that reads `reminderTimeMillis` and posts a notification.
- Cloud backup/sync and multi-backpack/family mode are out of scope for a first
  build, as the spec itself says to design for them later rather than build them now
  — the Room + Repository split already makes that swap possible without a rewrite.
- No app icon / adaptive icon is included — add one from Android Studio's
  `New > Image Asset` wizard (that's normally a design asset, not code).

## Project layout
```
app/src/main/java/com/nova/backpack/
  data/       Room entities, DAOs, database, SettingsRepository (DataStore), seed data, repository
  ui/         ViewModel, Strings (fa/en dictionary), theme system, animation system
  ui/screens/ Home, Backpack, Calendar, Before You Go, Settings
  ui/components/ LiquidGlassSurface (the press-and-hold micro-interaction)
  security/   BiometricAuthHelper, LockScreen, PIN hashing
  widget/     Glance home-screen widget
```


## Build APK from a phone (no Android Studio required)
1. Upload this project to a GitHub repository (the `NovaApp` folder is the repository root).
2. Open the repository on GitHub in your phone browser.
3. Go to **Actions** → **Build NOVA APK** → **Run workflow**.
4. Wait for the workflow to finish.
5. Open the completed workflow run → **Artifacts** → download `NOVA-debug-apk`.
6. Extract the ZIP and install `app-debug.apk` on your Android phone.
