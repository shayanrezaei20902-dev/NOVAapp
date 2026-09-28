package com.nova.backpack.data

object DefaultData {
    val categories = listOf(
        CategoryEntity("school", "📚", "#7c8cff", "درس و مدرسه", "School"),
        CategoryEntity("electronics", "🎧", "#4bc8ff", "لوازم الکترونیکی", "Electronics"),
        CategoryEntity("stationery", "✏️", "#f2c14e", "نوشت‌افزار", "Stationery"),
        CategoryEntity("personal", "🔑", "#b57cff", "وسایل شخصی", "Personal"),
        CategoryEntity("hygiene", "🧴", "#5fd39a", "بهداشتی", "Hygiene"),
        CategoryEntity("fun", "🎮", "#ff6fa5", "سرگرمی", "Entertainment"),
        CategoryEntity("clothing", "👕", "#ff8c6b", "لباس و پوشیدنی", "Clothing"),
    )

    val locations = listOf(
        LocationEntity("room", "🏠", "اتاق", "Room"),
        LocationEntity("drawer", "🗄", "کشوی میز", "Desk drawer"),
        LocationEntity("backpack", "🎒", "کوله", "Backpack"),
        LocationEntity("car", "🚗", "ماشین", "Car"),
        LocationEntity("school", "🏫", "مدرسه", "School"),
        LocationEntity("suitcase", "🧳", "چمدان", "Suitcase"),
    )

    val collections = listOf(
        CollectionEntity("school", "🏫", "مدرسه", "School", emptyList()),
        CollectionEntity("sport", "🏋️", "ورزش", "Sport", emptyList()),
        CollectionEntity("travel", "✈️", "سفر", "Travel", emptyList()),
        CollectionEntity("class", "💻", "کلاس", "Class", emptyList()),
        CollectionEntity("gaming", "🎮", "گیمینگ", "Gaming", emptyList()),
    )

    fun seedItems(): List<ItemEntity> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L
        return listOf(
            ItemEntity(
                id = "it1", nameFa = "هدفون JBL", nameEn = "JBL Headphones",
                categoryId = "electronics", locationId = "drawer",
                usageFa = "موسیقی / کلاس آنلاین", usageEn = "Music / online class",
                favorite = true, addedAtMillis = now - 9 * day, lastUsedMillis = now - 2 * 3_600_000L,
                history = listOf(HistoryEntry("drawer", now - 9 * day), HistoryEntry("backpack", now - 3 * day), HistoryEntry("drawer", now - 2 * 3_600_000L))
            ),
            ItemEntity(
                id = "it2", nameFa = "شارژر گوشی", nameEn = "Phone charger",
                categoryId = "electronics", locationId = "drawer",
                usageFa = "شارژ گوشی", usageEn = "Charge phone",
                favorite = true, addedAtMillis = now - 20 * day, lastUsedMillis = now - 2 * day,
                history = listOf(HistoryEntry("drawer", now - 20 * day))
            ),
            ItemEntity(
                id = "it3", nameFa = "کتاب ریاضی", nameEn = "Math book",
                categoryId = "school", locationId = "backpack",
                usageFa = "کلاس ریاضی", usageEn = "Math class",
                favorite = false, addedAtMillis = now - 15 * day, lastUsedMillis = now - 1 * day,
                history = listOf(HistoryEntry("backpack", now - 15 * day))
            ),
            ItemEntity(
                id = "it4", nameFa = "کلید", nameEn = "Keys",
                categoryId = "personal", locationId = "room",
                usageFa = "باز کردن در", usageEn = "Open door",
                favorite = true, addedAtMillis = now - 40 * day, lastUsedMillis = now - 3_600_000L,
                history = listOf(HistoryEntry("room", now - 40 * day))
            ),
        )
    }
}
