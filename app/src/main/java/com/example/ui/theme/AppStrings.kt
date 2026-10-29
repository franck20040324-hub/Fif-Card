package com.example.ui.theme

enum class AppLanguage {
    ENGLISH,
    ARABIC
}

object AppStrings {
    fun get(key: String, language: AppLanguage): String {
        val strings = if (language == AppLanguage.ARABIC) ARABIC_STRINGS else ENGLISH_STRINGS
        return strings[key] ?: ENGLISH_STRINGS[key] ?: key
    }

    private val ENGLISH_STRINGS = mapOf(
        "app_title" to "Fif-Cards",
        "tab_players" to "Players",
        "tab_create" to "Create Card",
        "tab_formations" to "Formations",
        "tab_settings" to "Settings",
        
        // Sorting
        "sort_by" to "Sort by:",
        "sort_nationality" to "Nationality",
        "sort_position" to "Playing Position",
        "sort_rating" to "Final Rating (OVR)",
        "sort_name" to "First Letter of Name",
        "search_players" to "Search players by name...",
        "no_players_found" to "No player cards found",
        "total_players" to "Total Players",
        
        // Positions
        "pos_gk" to "Goalkeeper",
        "pos_def" to "Defender",
        "pos_mid" to "Midfielder",
        "pos_fwd" to "Forward",
        
        // Stats
        "stat_pac" to "PAC",
        "stat_sho" to "SHO",
        "stat_pas" to "PAS",
        "stat_dri" to "DRI",
        "stat_def" to "DEF",
        "stat_phy" to "PHY",
        "stat_div" to "DIV",
        "stat_han" to "HAN",
        "stat_kic" to "KIC",
        "stat_ref" to "REF",
        "stat_spe" to "SPE",
        "stat_pos" to "POS",
        
        // Card actions & details
        "overall_rating" to "Overall Rating (OVR)",
        "player_name" to "Player Name",
        "position" to "Position",
        "nationality" to "Nationality",
        "club_name" to "Club",
        "league_name" to "League",
        "tap_to_change_photo" to "Tap photo to choose from device storage",
        "photo_private_hint" to "Photos are stored privately in app storage and won't appear in the phone's gallery.",
        "save_card" to "Save Player Card",
        "edit_card" to "Edit Card",
        "delete_card" to "Delete Card",
        "card_saved_success" to "Player card saved successfully to nominal list!",
        "card_deleted_success" to "Player card deleted",
        "create_new_card" to "Create New Player Card",
        "update_card" to "Update Player Card",
        
        // Formations
        "team_formations" to "Team Formations",
        "starting_eleven" to "Starting XI",
        "coach" to "Team Coach",
        "coach_name" to "Coach Name",
        "tactics" to "Tactical Style",
        "substitutes" to "Substitutes",
        "add_substitute" to "Add Substitute",
        "team_rating" to "Overall Team Rating",
        "empty_slot" to "Empty Slot",
        "tap_slot_to_select" to "Tap an empty slot to choose a player from the nominal list",
        "select_player_for_slot" to "Select Player for %s",
        "select_coach" to "Select Coach",
        "remove_player" to "Remove from Formation",
        "formation_433" to "4-3-3 Attack",
        "formation_442" to "4-4-2 Classic",
        "formation_4231" to "4-2-3-1 Wide",
        "formation_352" to "3-5-2 Wing Play",
        "formation_532" to "5-3-2 Defensive",
        "formation_343" to "3-4-3 Diamond",
        "new_formation" to "New Formation",
        "formation_name" to "Formation Name",
        
        // Settings
        "settings_title" to "Settings",
        "language_section" to "Language / اللغة",
        "lang_english" to "English",
        "lang_arabic" to "العربية (Arabic)",
        "music_section" to "Background Music",
        "add_music_btn" to "Upload Music from Storage",
        "music_hint" to "Add unlimited music files from phone storage. Files play continuously without interruption.",
        "volume_level" to "Music Volume: %d%%",
        "vol_increase" to "+10%",
        "vol_decrease" to "-10%",
        "no_music_loaded" to "No music added yet. Tap above to add audio files.",
        "now_playing" to "Now Playing: %s",
        "theme_section" to "Display Theme",
        "theme_dark" to "Dark Mode (Black)",
        "theme_light" to "Day Mode (White)",
        "theme_system" to "System Default",
        "backup_section" to "Precautionary Storage Backup",
        "backup_desc" to "Save a manual backup of all player cards, coaches, and formations into phone storage or share sheet.",
        "export_backup" to "Export Backup (JSON)",
        "restore_backup" to "Restore Backup",
        "privacy_section" to "Storage & Privacy Notice",
        "privacy_desc" to "All player cards, custom photos, and music files are stored in the app's isolated private sandbox. They will never appear in your phone's public gallery or file browsers.",
        "snapdragon_badge" to "Optimized for Snapdragon 662 • 100% Offline"
    )

    private val ARABIC_STRINGS = mapOf(
        "app_title" to "فيفا كاردز",
        "tab_players" to "قائمة اللاعبين",
        "tab_create" to "إنشاء بطاقة",
        "tab_formations" to "التشكيلات",
        "tab_settings" to "الإعدادات",
        
        // Sorting
        "sort_by" to "الترتيب حسب:",
        "sort_nationality" to "الجنسية",
        "sort_position" to "مركز اللعب",
        "sort_rating" to "التقييم النهائي (الطاقة)",
        "sort_name" to "الحرف الأول لاسم اللاعب",
        "search_players" to "ابحث عن لاعب بالاسم...",
        "no_players_found" to "لا توجد بطاقات لاعبين",
        "total_players" to "إجمالي اللاعبين",
        
        // Positions
        "pos_gk" to "حارس مرمى",
        "pos_def" to "مدافع",
        "pos_mid" to "خط وسط",
        "pos_fwd" to "مهاجم",
        
        // Stats
        "stat_pac" to "سرعة",
        "stat_sho" to "تسديد",
        "stat_pas" to "تمرير",
        "stat_dri" to "مراوغة",
        "stat_def" to "دفاع",
        "stat_phy" to "بدنية",
        "stat_div" to "ارتماء",
        "stat_han" to "إمساك",
        "stat_kic" to "ركل",
        "stat_ref" to "رد فعل",
        "stat_spe" to "سرعة",
        "stat_pos" to "تمركز",
        
        // Card actions & details
        "overall_rating" to "التقييم النهائي (OVR)",
        "player_name" to "اسم اللاعب",
        "position" to "المركز",
        "nationality" to "الجنسية",
        "club_name" to "النادي",
        "league_name" to "الدوري",
        "tap_to_change_photo" to "انقر على الصورة لاختيار صورة من ذاكرة الهاتف",
        "photo_private_hint" to "تُحفظ الصور في ذاكرة التطبيق الخاصة فقط ولن تظهر في معرض صور الهاتف إطلاقاً.",
        "save_card" to "حفظ بطاقة اللاعب",
        "edit_card" to "تعديل البطاقة",
        "delete_card" to "حذف البطاقة",
        "card_saved_success" to "تم حفظ بطاقة اللاعب بنجاح في القائمة الاسمية الدائمة!",
        "card_deleted_success" to "تم حذف البطاقة بنجاح",
        "create_new_card" to "إنشاء بطاقة لاعب جديدة",
        "update_card" to "تحديث بيانات البطاقة",
        
        // Formations
        "team_formations" to "تشكيلات الفرق",
        "starting_eleven" to "التشكيلة الأساسية (11 لاعب)",
        "coach" to "مدرب الفريق",
        "coach_name" to "اسم المدرب",
        "tactics" to "الأسلوب التكتيكي",
        "substitutes" to "دكة البدلاء (غير محدود)",
        "add_substitute" to "إضافة بديل",
        "team_rating" to "التقييم العام للفريق",
        "empty_slot" to "خانة فارغة",
        "tap_slot_to_select" to "المس الخانة الفارغة لاختيار لاعب من القائمة الاسمية",
        "select_player_for_slot" to "اختر لاعباً لمركز %s",
        "select_coach" to "اختر المدرب",
        "remove_player" to "إزالة من التشكيلة",
        "formation_433" to "4-3-3 هجومي",
        "formation_442" to "4-4-2 كلاسيكي",
        "formation_4231" to "4-2-3-1 متوازن",
        "formation_352" to "3-5-2 أجنحة",
        "formation_532" to "5-3-2 دفاعي",
        "formation_343" to "3-4-3 ماسي",
        "new_formation" to "تشكيلة جديدة",
        "formation_name" to "اسم التشكيلة",
        
        // Settings
        "settings_title" to "الإعدادات",
        "language_section" to "اللغة / Language",
        "lang_english" to "English (الإنجليزية)",
        "lang_arabic" to "العربية (Arabic)",
        "music_section" to "الموسيقى الخلفية",
        "add_music_btn" to "رفع موسيقى من ذاكرة الهاتف",
        "music_hint" to "أضف عدداً غير محدود من ملفات الموسيقى من الهاتف، تعمل بشكل مستمر بدون انقطاع.",
        "volume_level" to "مستوى صوت الأغاني: %d%%",
        "vol_increase" to "رفع +10%",
        "vol_decrease" to "خفض -10%",
        "no_music_loaded" to "لم يتم إضافة ملفات صوتية بعد. اضغط في الأعلى لإضافة أغانٍ.",
        "now_playing" to "المشغل الآن: %s",
        "theme_section" to "مظهر التطبيق",
        "theme_dark" to "الوضع الداكن (أسود)",
        "theme_light" to "الوضع النهاري (أبيض)",
        "theme_system" to "تلقائي حسب النظام",
        "backup_section" to "حفظ يدوي احتياطي في الذاكرة",
        "backup_desc" to "زر حفظ احتياطي وقائي لجميع بطاقات اللاعبين والمدربين والتشكيلات كملف في الهاتف.",
        "export_backup" to "تصدير نسخة احتياطية (JSON)",
        "restore_backup" to "استعادة نسخة سابقة",
        "privacy_section" to "الخصوصية والتخزين المحلي",
        "privacy_desc" to "جميع البطاقات والصور والموسيقى محفوظة في ذاكرة التطبيق المعزولة، ولن تظهر إطلاقاً في معرض الصور أو وسائط الهاتف.",
        "snapdragon_badge" to "متوافق ومُحسّن لمعالج Snapdragon 662 • يعمل 100% بدون إنترنت"
    )
}
