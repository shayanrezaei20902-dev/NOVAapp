package com.nova.backpack.ui

/**
 * The app switches language from an in-app Settings toggle (not the phone's system
 * language), so strings live here rather than in res/values-fa — this dictionary is
 * looked up through Strings.get(lang, key) from every screen.
 */
object Strings {
    private val fa = mapOf(
        "appName" to "NOVA", "tagline" to "همه وسایلت، همیشه یادت هست.",
        "navHome" to "خانه", "navBackpack" to "کوله", "navCalendar" to "تقویم",
        "navGo" to "قبل از خروج", "navSettings" to "تنظیمات",
        "favorites" to "⭐ وسایل مهم", "beforeGo" to "🚪 قبل از خروج", "locations" to "📍 مکان‌ها",
        "search" to "🔎 جست‌وجو", "searchPh" to "چی رو می‌خوای پیدا کنی؟",
        "all" to "همه", "byCategory" to "دسته‌بندی", "byLocation" to "مکان",
        "addCategory" to "＋ دسته‌بندی جدید", "noItems" to "وسیله‌ای پیدا نشد",
        "addItem" to "افزودن وسیله", "itemName" to "نام وسیله", "category" to "دسته‌بندی",
        "location" to "مکان نگهداری", "usage" to "کاربرد", "notes" to "توضیحات",
        "takePhoto" to "📷 گرفتن عکس", "chooseGallery" to "🖼 انتخاب از گالری",
        "favorite" to "⭐ افزودن به موارد مهم", "reminder" to "یادآوری (اختیاری)",
        "save" to "✓ ذخیره", "history" to "🔄 تاریخچه", "usedNow" to "الان استفاده کردم",
        "edit" to "ویرایش", "delete" to "حذف وسیله", "lastUsed" to "آخرین استفاده",
        "themes" to "🎨 تم", "liquidGlass" to "🫧 Liquid Glass",
        "glassLabel" to "فعال‌سازی ظاهر شیشه‌ای", "glassDemo" to "این دکمه را نگه دارید",
        "animation" to "✨ انیمیشن", "animOn" to "فعال بودن انیمیشن‌ها",
        "reduceMotion" to "Reduce Motion", "haptic" to "لرزش هنگام تعامل",
        "pageOpen" to "🚪 انیمیشن باز شدن صفحه", "speed" to "⚡ سرعت انیمیشن",
        "lock" to "قفل با PIN / اثر انگشت", "setPin" to "کد ۴ رقمی را وارد کنید",
        "unlock" to "باز کردن قفل", "newCollection" to "＋ مجموعه جدید", "reset" to "ریست چک‌لیست",
        "madeBy" to "ساخته شده توسط"
    )
    private val en = mapOf(
        "appName" to "NOVA", "tagline" to "Everything you own, always remembered.",
        "navHome" to "Home", "navBackpack" to "Backpack", "navCalendar" to "Calendar",
        "navGo" to "Before You Go", "navSettings" to "Settings",
        "favorites" to "⭐ Favorites", "beforeGo" to "🚪 Before You Go", "locations" to "📍 Locations",
        "search" to "🔎 Search", "searchPh" to "What are you looking for?",
        "all" to "All", "byCategory" to "Category", "byLocation" to "Location",
        "addCategory" to "＋ New category", "noItems" to "No items found",
        "addItem" to "Add item", "itemName" to "Item name", "category" to "Category",
        "location" to "Location", "usage" to "Used for", "notes" to "Notes",
        "takePhoto" to "📷 Take photo", "chooseGallery" to "🖼 Choose from gallery",
        "favorite" to "⭐ Mark as favorite", "reminder" to "Reminder (optional)",
        "save" to "✓ Save", "history" to "🔄 History", "usedNow" to "Used it now",
        "edit" to "Edit", "delete" to "Delete item", "lastUsed" to "Last used",
        "themes" to "🎨 Theme", "liquidGlass" to "🫧 Liquid Glass",
        "glassLabel" to "Enable glass surfaces", "glassDemo" to "Press & hold to preview",
        "animation" to "✨ Animation", "animOn" to "Animations enabled",
        "reduceMotion" to "Reduce Motion", "haptic" to "Haptic feedback",
        "pageOpen" to "🚪 Page Open Animation", "speed" to "⚡ Animation speed",
        "lock" to "PIN / Fingerprint lock", "setPin" to "Enter a 4-digit code",
        "unlock" to "Unlock", "newCollection" to "＋ New collection", "reset" to "Reset checklist",
        "madeBy" to "Made by"
    )

    fun get(lang: String, key: String): String = (if (lang == "fa") fa else en)[key] ?: key
}
