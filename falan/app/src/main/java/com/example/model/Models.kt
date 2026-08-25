package com.example.model

enum class GladiatorContractType(val displayName: String, val description: String) {
    SLAVE("Köle (Slave)", "Pazardan satın alınır. Düşük moralle başlar; sadece ölümle veya oyuncu azad ederse ayrılır."),
    CONTRACTED("Sözleşmeli (Auctoratus)", "Aylık/günlük maaş ve ödül payı alır. Yüksek morallidir; sözleşmesi bitince uzatılmazsa ayrılır.")
}

enum class InjurySeverity(val displayName: String, val recoveryMultiplier: Float) {
    NONE("Sağlıklı", 0f),
    LIGHT("Hafif Yaralı", 1f),
    SEVERE("Ağır Yaralı", 2f),
    CRITICAL("Kritik / Komada", 3.5f)
}

enum class GladiatorClass(val displayName: String, val weaponDesc: String, val statBonusDesc: String, val icon: String = "⚔️") {
    MURMILLO("Murmillo", "Gladius & Scutum (Büyük Kalkan)", "+STR & Dayanıklılık odaklı tank dövüşçü", "🛡️"),
    RETIARIUS("Retiarius", "Üç Dişli Mızrak & Ağ", "+AGI & Hız odaklı, kaçınma ustası", "🔱"),
    THRAEX("Thraex", "Sica (Kıvrık Kılıç) & Parma", "+Kritik vuruş ve çeviklik", "🗡️"),
    SECUTOR("Secutor", "Ağır Zırh & Miğfer", "+Yüksek zırh ve karşı saldırı gücü", "🤺"),
    DIMACHAERUS("Dimachaerus", "Çift Kılıç", "+Yüksek saldırı sıklığı ve hasar", "⚔️")
}

enum class DrillCategory(val title: String, val shortTitle: String, val icon: String, val colorHex: Long) {
    STRENGTH("Güç & Darbe İdmanları", "Güç (STR)", "💪", 0xFFE57373),
    AGILITY("Çeviklik & Hız İdmanları", "Çeviklik (AGI)", "⚡", 0xFF64B5F6),
    STAMINA("Kondisyon & Dayanıklılık", "Kondisyon (STA)", "🛡️", 0xFF81C784),
    TACTICAL_RECOVERY("Taktik & Yenilenme", "Taktik & Hamam", "🏛️", 0xFFFFB74D)
}

enum class SpecificDrill(
    val id: String,
    val title: String,
    val latinName: String,
    val category: DrillCategory,
    val description: String,
    val targetStat: String,
    val strXpGain: Int,
    val agiXpGain: Int,
    val staXpGain: Int,
    val hpBonus: Int,
    val fatigueCost: Int,
    val moraleBonus: Int,
    val icon: String,
    val rewardBadge: String
) {
    // STRENGTH DRILLS
    PALUS_WOODEN_POST(
        id = "palus_post",
        title = "Palus Kazık Vuruşları",
        latinName = "Palus Rudis Striking",
        category = DrillCategory.STRENGTH,
        description = "Ağır tahta kazığa çift ağırlıklı tahta kılıçla (rudis) yüzlerce darbe vurur. Darbe gücünü ve kas hafızasını pekiştirir.",
        targetStat = "STR (Güç)",
        strXpGain = 35,
        agiXpGain = 5,
        staXpGain = 10,
        hpBonus = 0,
        fatigueCost = 20,
        moraleBonus = 2,
        icon = "🪵",
        rewardBadge = "+35 STR XP"
    ),
    HEAVY_STONE_LIFT(
        id = "heavy_stone",
        title = "Ağır Kaya & Taş Taşıma",
        latinName = "Saxa Gestatio",
        category = DrillCategory.STRENGTH,
        description = "Omuzda ağır bazalt kayalarla derin kum zemin üzerinde ağırlık yürüyüşü. Saf kas kütlesi ve göğüs kuvveti inşa eder.",
        targetStat = "STR (Güç)",
        strXpGain = 50,
        agiXpGain = 0,
        staXpGain = 15,
        hpBonus = 0,
        fatigueCost = 28,
        moraleBonus = 0,
        icon = "🪨",
        rewardBadge = "+50 STR XP"
    ),
    SLEDGEHAMMER_ANVIL(
        id = "sledgehammer_anvil",
        title = "Demir Balyoz & Çekiç İdmanı",
        latinName = "Malleus Vulcanus",
        category = DrillCategory.STRENGTH,
        description = "Balyoz vuruşlarıyla üst vücut patlayıcı gücünü, bilek sertliğini ve kalkan yarma kuvvetini artırır.",
        targetStat = "STR (Güç & Silah)",
        strXpGain = 40,
        agiXpGain = 10,
        staXpGain = 10,
        hpBonus = 0,
        fatigueCost = 22,
        moraleBonus = 3,
        icon = "🔨",
        rewardBadge = "+40 STR XP"
    ),

    // AGILITY DRILLS
    ROPE_FOOTWORK(
        id = "rope_footwork",
        title = "İp Atlama & Çevik Adımlama",
        latinName = "Cursus et Saltus",
        category = DrillCategory.AGILITY,
        description = "Hızlı ayak koordinasyonu, kumda ani yön değiştirme ve denge eğitimi sağlar.",
        targetStat = "AGI (Çeviklik)",
        strXpGain = 5,
        agiXpGain = 40,
        staXpGain = 15,
        hpBonus = 0,
        fatigueCost = 18,
        moraleBonus = 2,
        icon = "👟",
        rewardBadge = "+40 AGI XP"
    ),
    SPEAR_EVASION(
        id = "spear_evasion",
        title = "Mızrak & Ağ Savuşturma",
        latinName = "Hasta Evasio",
        category = DrillCategory.AGILITY,
        description = "Eğitmenin savurduğu mızrak ve fırlattığı ağlardan refleksle kaçınma; savuşturma reflekslerini keskinleştirir.",
        targetStat = "AGI (Çeviklik)",
        strXpGain = 10,
        agiXpGain = 50,
        staXpGain = 10,
        hpBonus = 0,
        fatigueCost = 24,
        moraleBonus = 0,
        icon = "🔱",
        rewardBadge = "+50 AGI XP"
    ),
    BLINDFOLDED_REFLEX(
        id = "blindfolded_reflex",
        title = "Gözü Kapalı Refleks İdmanı",
        latinName = "Velum Caecus",
        category = DrillCategory.AGILITY,
        description = "Gözleri bağlıyken ses ve hava akımına odaklanarak darbe savuşturma; kritik vuruş ve sezgisel savunma kazandırır.",
        targetStat = "AGI (Refleks & Sezgi)",
        strXpGain = 0,
        agiXpGain = 45,
        staXpGain = 5,
        hpBonus = 0,
        fatigueCost = 20,
        moraleBonus = 4,
        icon = "👁️",
        rewardBadge = "+45 AGI XP"
    ),

    // STAMINA & CONDITIONING DRILLS
    SAND_PIT_RUN(
        id = "sand_pit_run",
        title = "Derin Kum Havuzunda Koşu",
        latinName = "Arena Cursus",
        category = DrillCategory.STAMINA,
        description = "Ayakları batıran derin arenada tempolu sprint koşusu. Bacak dayanıklılığı ve akciğer kapasitesini katlar.",
        targetStat = "STA (Dayanıklılık)",
        strXpGain = 5,
        agiXpGain = 10,
        staXpGain = 45,
        hpBonus = 5,
        fatigueCost = 22,
        moraleBonus = 1,
        icon = "🏜️",
        rewardBadge = "+45 STA XP & HP"
    ),
    WEIGHTED_SHIELD_MARCH(
        id = "weighted_shield",
        title = "Ağır Kalkanla Yürüyüş",
        latinName = "Scutum Latio",
        category = DrillCategory.STAMINA,
        description = "Ağır demir takviyeli kalkanı saatlerce omuz hizasında taşıyarak defansif direnç ve nefes disiplini kazanır.",
        targetStat = "STA (Dayanıklılık)",
        strXpGain = 15,
        agiXpGain = 0,
        staXpGain = 50,
        hpBonus = 8,
        fatigueCost = 26,
        moraleBonus = 0,
        icon = "🛡️",
        rewardBadge = "+50 STA XP"
    ),
    COLD_PLUNGE_BREATH(
        id = "cold_plunge",
        title = "Soğuk Su Havuzu & Nefes",
        latinName = "Frigidarium Disciplina",
        category = DrillCategory.STAMINA,
        description = "Soğuk hamam suyunda kalp ritmini kontrol etme, laktik asit temizleme ve kondisyon artırma.",
        targetStat = "STA & Yenilenme",
        strXpGain = 0,
        agiXpGain = 5,
        staXpGain = 30,
        hpBonus = 10,
        fatigueCost = 5,
        moraleBonus = 8,
        icon = "🧊",
        rewardBadge = "+30 STA XP"
    ),

    // TACTICAL & RECOVERY
    DOCTORE_TACTICAL_SPARRING(
        id = "doctore_sparring",
        title = "Doctore ile Taktik Sparring",
        latinName = "Doctore Magisterium",
        category = DrillCategory.TACTICAL_RECOVERY,
        description = "Başeğitmenin gözetiminde gerçek silahlarla kontrollü maç; tüm savaş disiplinlerini ve tecrübeyi dengeli geliştirir.",
        targetStat = "Tüm Statlar",
        strXpGain = 20,
        agiXpGain = 20,
        staXpGain = 20,
        hpBonus = 0,
        fatigueCost = 25,
        moraleBonus = 5,
        icon = "⚔️",
        rewardBadge = "+20 Tüm XP"
    ),
    THERMAE_MASSAGE(
        id = "thermae_massage",
        title = "Thermae Hamamı & Masaj",
        latinName = "Thermae et Oleum",
        category = DrillCategory.TACTICAL_RECOVERY,
        description = "Sıcak hamam, zeytinyağı masajı ve tam dinlenme. Yorgunluğu sıfırlar, moral ve can yeniler.",
        targetStat = "Toparlanma & Hamam",
        strXpGain = 0,
        agiXpGain = 0,
        staXpGain = 0,
        hpBonus = 25,
        fatigueCost = -45,
        moraleBonus = 18,
        icon = "🏛️",
        rewardBadge = "-45 Yorgunluk"
    );

    fun toLegacyTrainingType(): TrainingType {
        return when (category) {
            DrillCategory.STRENGTH -> TrainingType.STRENGTH
            DrillCategory.AGILITY -> TrainingType.AGILITY
            DrillCategory.STAMINA -> TrainingType.ENDURANCE
            DrillCategory.TACTICAL_RECOVERY -> if (this == THERMAE_MASSAGE) TrainingType.REST else TrainingType.SPARRING
        }
    }
}

enum class TrainingType(
    val id: String,
    val title: String,
    val description: String,
    val targetStatDesc: String,
    val fatigueCost: Int
) {
    REST("rest", "Dinlenme & Hamam (Thermae)", "Yorgunluğu sıfırlar, moral ve can yeniler.", "+15 Moral, -40 Yorgunluk", -40),
    STRENGTH("str", "Ağır Taşlar & Güç Antrenmanı", "Kas gücünü ve darbe hasarını artırır.", "+STR (Güç)", 20),
    AGILITY("agi", "Ayak Oyunları & Çeviklik", "Refleksleri, hızı ve savuşturmayı geliştirir.", "+AGI (Çeviklik)", 18),
    ENDURANCE("sta", "Kondisyon & Dayanıklılık", "Nefes kapasitesini ve maksimum canı artırır.", "+STA & +Max HP", 18),
    SPARRING("sparring", "Silah Ustalığı & Sparring", "Sınıf silah tekniğini ve kritik vuruşu eğitir.", "+STR, +AGI, +Silah Seviyesi", 30)
}

enum class DietPlan(
    val title: String,
    val dailyCostPerGladiator: Int,
    val description: String,
    val efficiencyMultiplier: Float,
    val dailyMoraleBonus: Int
) {
    BARLEY_PORRIDGE("Arpa Lapası (Standart)", 2, "Geleneksel ucuz gladyatör diyeti. Standart antrenman verimi.", 1.0f, 0),
    SAGINA_PROTEIN("Gladyatör Saginası (Yüksek Protein)", 6, "Bakla, kemik külü ve et karışımı. +%30 Antrenman stat artışı.", 1.30f, 4),
    FEAST_WINE("Ziyafet & Şarap (Lüks Patron)", 14, "Zengin Roma ziyafeti. +%20 Verim ve her gün yüksek moral artışı.", 1.20f, 12)
}

enum class TeacherSpecialty(
    val displayName: String,
    val description: String,
    val statBonusDesc: String
) {
    MURMILLO_MASTER("Kalkan & Güç Ustası (Doctore)", "Murmillo ve Secutor tarzı dövüşçülere ekstra +STR ve zırh eğitimi sağlar.", "+2 STR & Savunma"),
    RETIARIUS_MASTER("Ağ & Çeviklik Ustası (Doctore)", "Retiarius ve Thraex dövüşçülere ekstra +2 AGI ve savuşturma eğitimi sağlar.", "+2 AGI & Hız"),
    PHYSICAL_CONDITIONING("Kondisyon & Hekim Fizikçisi", "Tüm kadroya +2 STA sağlar ve sakatlanma riskini %40 azaltır.", "+2 STA & Sağlık"),
    GLADIATOR_DISCIPLINE("Disiplin & Savaş Başeğitmeni", "Moral kaybını önler ve tüm antrenmanların verimini %25 artırır.", "+Moral & Verim"),
    VETERAN_LEGEND("Emekli Efsane Gladyatör", "Kendi dövüş tecrübesini ve kritik vuruş taktiklerini yeni nesle aktarır.", "+Tüm Statlar & Tecrübe")
}

data class Teacher(
    val id: Long = 0,
    val name: String,
    val title: String,
    val specialty: TeacherSpecialty,
    val originGladiatorName: String? = null,
    val level: Int = 1,
    val statBonusMultiplier: Float = 1.35f,
    val dailySalary: Int = 10,
    val hireCost: Int = 100,
    val description: String,
    val isPromotedFromRoster: Boolean = false
)

enum class BattleTactic(
    val id: String,
    val title: String,
    val description: String,
    val dmgMod: Float,
    val critMod: Float,
    val defMod: Float,
    val hypeMod: Float
) {
    AGGRESSIVE(
        id = "aggressive",
        title = "Agresif (All-in)",
        description = "+%20 Hasar, +%15 Kritik, -%25 Savunma (yüksek sakatlanma riski).",
        dmgMod = 1.20f,
        critMod = 0.15f,
        defMod = 0.75f,
        hypeMod = 1.15f
    ),
    DEFENSIVE(
        id = "defensive",
        title = "Defansif / Kontra",
        description = "+%30 Blok/Savuşturma; rakip yorulunca +%40 Karşı Saldırı Hasarı.",
        dmgMod = 0.90f,
        critMod = 0.05f,
        defMod = 1.30f,
        hypeMod = 0.95f
    ),
    CROWD_PLEASER(
        id = "crowd_pleaser",
        title = "Gösterişli (Crowd Pleaser)",
        description = "Normal hasar; seyirci ilgisi ve maç sonu prestij/bahis ödülü +%50 artar.",
        dmgMod = 1.00f,
        critMod = 0.10f,
        defMod = 1.00f,
        hypeMod = 1.50f
    ),
    MAIMING(
        id = "maiming",
        title = "Sakatlamaya Odaklı",
        description = "Düşük öldürme; rakibin uzuvlarına oynayarak kalıcı sakatlık bırakma ve pes ettirme şansı.",
        dmgMod = 0.85f,
        critMod = 0.20f,
        defMod = 0.95f,
        hypeMod = 1.20f
    )
}

enum class MatchFormat(
    val title: String,
    val description: String,
    val rewardGoldBase: Int,
    val rewardPrestigeBase: Int,
    val riskOfDeath: String
) {
    LUSUS(
        title = "Gösteri Dövüşü (Lusus)",
        description = "Tahta ve köreltilmiş silahlar kullanılır. Ölüm riski yoktur, düzenli seyirci prestiji sağlar.",
        rewardGoldBase = 60,
        rewardPrestigeBase = 15,
        riskOfDeath = "Yok (Güvenli)"
    ),
    TEAM_2V2(
        title = "2'ye 2 Takım Dövüşü",
        description = "İki gladyatörün sinerjisine dayalı taktiksel eşleşme (Örn: Tank + Hızlı Gladyatör).",
        rewardGoldBase = 180,
        rewardPrestigeBase = 35,
        riskOfDeath = "Düşük"
    ),
    SINE_MISSIO(
        title = "Ölümüne Dövüş (Sine Missio)",
        description = "İmparator ve Senato huzurunda ölümüne dövüş. Maksimum prestij ve altın getirir!",
        rewardGoldBase = 450,
        rewardPrestigeBase = 90,
        riskOfDeath = "Yüksek (Ölüm/Ağır Sakatlık)"
    )
}

enum class CityTier(
    val tierNumber: Int,
    val cityName: String,
    val title: String,
    val minPrestigeRequired: Int,
    val description: String,
    val arenaLevelName: String
) {
    TOWN_RURAL(
        tierNumber = 1,
        cityName = "Kasaba Arenası",
        title = "Kademe 1 — Kırsal / Kasaba",
        minPrestigeRequired = 0,
        description = "Küçük ödüller, tahta kılıçlı gösteri maçları, acemi dövüşçüler.",
        arenaLevelName = "Seviye 1: Kasaba Çaylak Ligi"
    ),
    CAPUA_POMPEII(
        tierNumber = 2,
        cityName = "Capua & Pompeii",
        title = "Kademe 2 — Bölgesel (Capua)",
        minPrestigeRequired = 100,
        description = "2v2 takım savaşları, vahşi hayvan karşılaşmaları, orta düzey sponsorluklar.",
        arenaLevelName = "Seviye 2: Capua Amfitiyatrosu Oyunları"
    ),
    VERONA(
        tierNumber = 3,
        cityName = "Verona Amfitiyatrosu",
        title = "Kademe 3 — Verona",
        minPrestigeRequired = 300,
        description = "Sponsorluklar tam açılır, hekim Seviye 3 erişilebilir, usta gladyatörler.",
        arenaLevelName = "Seviye 3: Verona Büyük Munera Turnuvası"
    ),
    ROME_COLOSSEUM(
        tierNumber = 4,
        cityName = "Roma Colosseum",
        title = "Kademe 4 — Roma (İmparatorluk)",
        minPrestigeRequired = 750,
        description = "İmparator ve Senato huzurunda efsanevi şampiyonluk unvanı finali.",
        arenaLevelName = "Seviye 4: Roma Colosseum İmparatorluk Finali"
    )
}

enum class DayPhase(val order: Int, val title: String, val subtitle: String) {
    MORNING(1, "Sabah", "Ludus Yönetimi & Antrenman"),
    NOON(2, "Öğle", "Taktik & Eğitmen Denetimi"),
    EVENING(3, "Akşam", "Arena Karşılaşması & Müsabaka"),
    NIGHT(4, "Gece", "Tedavi, Hesap Kesimi & Güvenlik")
}

enum class ThreatStage(val title: String, val levelNumber: Int, val description: String) {
    NONE("Tehdit Yok", 0, "Bakiye güvende, ludus huzur içinde."),
    WARNING("1. Aşama: Uyarı Mektubu", 1, "Tefecinin adamları kapıda; ek faiz uygulandı. 30 gün içinde ödeme yapılmalı."),
    INFILTRATION_RISK("2. Aşama: Sızma Girişimi", 2, "Geceleyin suikastçı sızma riski (%25). Muhafızlar tetikte olmalı!"),
    ATTACK_ACTIVE("3. Aşama: Saldırı Gerçekleşti", 3, "Suikastçı engellenemedi; en değerli gladyatör veya hekim hedef alındı!")
}

enum class EnemyTier(
    val title: String,
    val starRating: String,
    val description: String,
    val hpMultiplier: Float,
    val dmgMultiplier: Float
) {
    NOVICE("Acemi Çaylak", "★☆☆☆", "Kırsal arenaların acemi dövüşçüsü.", 1.0f, 1.0f),
    VETERAN("Deneyimli Gladyatör", "★★☆☆", "Birçok karşılaşmadan sağ çıkmış kıdemli savaşçı.", 1.25f, 1.20f),
    ELITE("Seçkin Gladyatör", "★★★☆", "Özel tekniklere sahip, ölümcül darbeler vuran arena ustası.", 1.55f, 1.45f),
    CHAMPION_BOSS("ARENA ŞAMPİYONU (BOSS)", "★★★★", "Şehrin yaşayan efsanesi! Yüksek zırh, ezici güç ve özel yetenekler.", 2.10f, 1.80f)
}

enum class CrowdVerdict(val title: String, val subtitle: String, val chant: String) {
    MISSIO("BAĞIŞLA (MISSIO - 👍)", "Canını bağışla, halkın erdemini ve saygısını kazan.", "MITTE! MITTE! (BAĞIŞLA!)"),
    IUGULA("ÖLDÜR (IUGULA - 👎)", "Boynunu vur! Kan isteyen seyircileri ve sponsorları coştur.", "IUGULA! IUGULA! (BOYNUNU VUR!)")
}

enum class CrowdSentimentLevel(
    val displayName: String,
    val subtitle: String,
    val icon: String,
    val difficultyImpactDesc: String,
    val badgeColorHex: Long,
    val prestigeBonusMult: Float,
    val goldBonusMult: Float,
    val threatModifier: Float
) {
    BLOODTHIRSTY_VENDETTA(
        displayName = "Kana Susamış İntikam Çetesi",
        subtitle = "Arenada infazlar ve kan yankılandı. Seyirci daha vahşi ölümler ve intikam istiyor!",
        icon = "💀",
        difficultyImpactDesc = "🔥 Rakipler intikam yemini etti! +%25 Ekstra Can, +%20 Hasar ve Agresif Yetenekler.",
        badgeColorHex = 0xFFE53935,
        prestigeBonusMult = 0.85f,
        goldBonusMult = 1.30f,
        threatModifier = 0.25f
    ),
    RESTLESS_DEMANDING(
        displayName = "Hırslı & Şiddet Arayan Tribün",
        subtitle = "Sert dövüşler izlemek isteyen sabırsız Roma halkı.",
        icon = "🩸",
        difficultyImpactDesc = "⚡ Rakipler +%12 daha güçlü ve çetin.",
        badgeColorHex = 0xFFFF7043,
        prestigeBonusMult = 0.95f,
        goldBonusMult = 1.15f,
        threatModifier = 0.12f
    ),
    BALANCED_ATTENTIVE(
        displayName = "Dengeli & Meraklı Seyirci",
        subtitle = "Amfitiyatro adil ve heyecanlı müsabakalar bekliyor.",
        icon = "⚖️",
        difficultyImpactDesc = "🛡️ Standart zorluk; adil güç eşleşmesi.",
        badgeColorHex = 0xFFFFB300,
        prestigeBonusMult = 1.0f,
        goldBonusMult = 1.0f,
        threatModifier = 0.0f
    ),
    ENTHUSIASTIC_FAVORITE(
        displayName = "Halkın Sevgilisi & Coşkulu Tribün",
        subtitle = "Lanista'nın zaferleri ve dövüşçülerin tekniği takdir görüyor.",
        icon = "👏",
        difficultyImpactDesc = "🌿 Rakipler onurlu dövüşür, prestij kazancı +%20 artar.",
        badgeColorHex = 0xFF66BB6A,
        prestigeBonusMult = 1.20f,
        goldBonusMult = 1.05f,
        threatModifier = -0.05f
    ),
    IMPERIAL_CLEMENTIA(
        displayName = "İmparatorluk Gözdesi & Clementia",
        subtitle = "Asil bağışlamalar ve kusursuz dövüşler Senato ve İmparator'un övgüsünü aldı!",
        icon = "🏛️",
        difficultyImpactDesc = "🕊️ Rakipler saygılı ve dengeli. +%35 Prestij, +%15 Ekstra Sponsorluk Bahşişi.",
        badgeColorHex = 0xFF42A5F5,
        prestigeBonusMult = 1.35f,
        goldBonusMult = 1.15f,
        threatModifier = -0.10f
    );

    companion object {
        fun fromScore(score: Int, ruthlessness: Int, mercy: Int): CrowdSentimentLevel {
            val netMercyBias = (mercy * 6) - (ruthlessness * 8)
            val effective = (score + netMercyBias).coerceIn(0, 100)
            return when {
                effective >= 80 -> IMPERIAL_CLEMENTIA
                effective >= 60 -> ENTHUSIASTIC_FAVORITE
                effective >= 40 -> BALANCED_ATTENTIVE
                effective >= 20 -> RESTLESS_DEMANDING
                else -> BLOODTHIRSTY_VENDETTA
            }
        }
    }
}

data class ScheduledArenaEvent(
    val id: String,
    val title: String,
    val arenaName: String,
    val cityTier: CityTier,
    val targetDay: Int, // The match day when this fight takes place
    val hostPatron: String,
    val description: String,
    val matchFormat: MatchFormat,
    val enemyTier: EnemyTier,
    val featuredOpponent: EnemyGladiator,
    val rewardGold: Int,
    val rewardPrestige: Int,
    val isCompleted: Boolean = false,
    val specialRule: String = "Standart Arena Kuralları",
    val tacticalAdvice: String = "Rakip gücüne ve taktiğine karşı çeviklik ve dayanıklılık dengesi kurun.",
    val recommendedFocus: TrainingType = TrainingType.AGILITY,
    val patronBonusGold: Int = 0,
    val tierLevel: Int = 1,
    val crowdHypeText: String = "Bölgesel İlgi & Heyecan 🔥"
)

data class Gladiator(
    val id: Long = 0,
    val name: String,
    val nickname: String,
    val origin: String,
    val gladiatorClass: GladiatorClass,
    val contractType: GladiatorContractType,
    val dailySalary: Int,
    val priceValue: Int,
    
    // Stats
    val str: Int, // Güç
    val agi: Int, // Çeviklik
    val sta: Int, // Dayanıklılık
    val mor: Int, // Moral / Sadakat (0-100)
    
    // Age & Fatigue & Experience
    val age: Int = 23,
    val fatigue: Int = 0, // 0 - 100
    val experience: Int = 0,
    val isRetiredTeacher: Boolean = false,
    
    // Health & Condition
    val currentHp: Int,
    val maxHp: Int,
    val isInjured: Boolean = false,
    val injurySeverity: InjurySeverity = InjurySeverity.NONE,
    val recoveryDaysLeft: Int = 0,
    val hasDisabledLimb: Boolean = false,
    val disabledLimbDesc: String? = null,
    
    // Match mechanics
    val promiseOfFreedom: Boolean = false,
    val wins: Int = 0,
    val losses: Int = 0,
    val kills: Int = 0,
    
    // Equipment & Training
    val weaponLevel: Int = 1,
    val armorLevel: Int = 1,
    val isTraining: Boolean = false,
    val trainedStatToday: String? = null,
    val trainingFocus: TrainingType = TrainingType.STRENGTH,

    // Specific Drill Progress Tracking (Strength/Agility/Stamina)
    val assignedDrill: SpecificDrill = SpecificDrill.PALUS_WOODEN_POST,
    val strXpProgress: Int = 20,
    val agiXpProgress: Int = 15,
    val staXpProgress: Int = 25,
    val drillMasteryLevel: Int = 1,
    val drillsCompletedCount: Int = 0,
    val lastDrillSummary: String? = null,
    val hasTrainedToday: Boolean = false,

    // Equipped Armory Gear
    val equippedWeaponId: String? = null,
    val equippedArmorId: String? = null,
    val equippedRelicId: String? = null,

    // Personality Trait
    val personalityTrait: GladiatorTrait = GladiatorTrait.IRON_WILLED
) {
    val careerRank: GladiatorCareerRank
        get() = GladiatorCareerRank.fromWins(wins)

    val totalPowerScore: Int
        get() = str * 2 + agi * 2 + sta * 2 + (mor / 5) + (weaponLevel * 4) + (armorLevel * 4)

    val isVeteran: Boolean
        get() = wins >= 3 || age >= 28 || kills >= 2

    val canPromoteToTeacher: Boolean
        get() = !isRetiredTeacher && (isVeteran || age >= 26)

    val drillMasteryTitle: String
        get() = when (drillMasteryLevel) {
            1 -> "Acemi Tiro (Kademe 1)"
            2 -> "Talimli Dövüşçü (Kademe 2)"
            3 -> "Kıdemli Gladyatör (Kademe 3)"
            4 -> "Arena Ustası (Kademe 4)"
            else -> "Efsanevi Doctore Adayı (Kademe $drillMasteryLevel)"
        }
}

enum class GladiatorTrait(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val combatBonusDesc: String
) {
    BLOODTHIRSTY(
        id = "trait_bloodthirsty",
        title = "Kana Susamış",
        description = "İnfaz zaferlerinde +25 ekstra moral kazanır, kritik darbelerde seyirci coşkusunu patlatır.",
        icon = "🩸",
        combatBonusDesc = "+%15 İnfaz Morali & Yüksek Hype"
    ),
    STOIC(
        id = "trait_stoic",
        title = "Metanetli & Dayanıklı",
        description = "Ağır darbelerden sarsılmaz, revirde 1 gün daha hızlı iyileşir ve moral kaybına dirençlidir.",
        icon = "🗿",
        combatBonusDesc = "Hızlı İyileşme & Sarsılmaz Moral"
    ),
    CROWD_FAVORITE(
        id = "trait_crowd_fav",
        title = "Halkın Sevgilisi",
        description = "Arenaya adım attığı an tribünler alkışlar; her dövüşe +20 seyirci coşkusu (Hype) ile başlar.",
        icon = "🌟",
        combatBonusDesc = "+20 Başlangıç Hype & Yüksek Bahis"
    ),
    IRON_WILLED(
        id = "trait_iron_willed",
        title = "Çelik İradeli",
        description = "Canı %35'in altına düştüğünde hayatta kalma arzusuyla +%25 darbe hasarı bonusu kazanır.",
        icon = "🛡️",
        combatBonusDesc = "Düşük Canda +%25 Hasar Patlaması"
    ),
    SCHOLAR_WARRIOR(
        id = "trait_scholar",
        title = "Taktisyen Dövüşçü",
        description = "Silah tekniklerini çok çabuk kavrar; her antrenmanda +%20 daha fazla XP kazanır.",
        icon = "📜",
        combatBonusDesc = "İdmanlarda +%20 Ekstra Gelişim XP"
    ),
    SPARTAN_DISCIPLINE(
        id = "trait_spartan",
        title = "Sparta Disiplini",
        description = "Ağır idmanlara karşı çelik gibi dirençlidir; yorgunluk birikimi %25 daha az olur.",
        icon = "⚔️",
        combatBonusDesc = "-%25 Daha Az Yorgunluk Tüketimi"
    )
}

data class HallOfFameHero(
    val id: String,
    val gladiatorName: String,
    val nickname: String,
    val origin: String,
    val gladiatorClass: GladiatorClass,
    val finalWins: Int,
    val finalLosses: Int,
    val finalKills: Int,
    val rankTitle: String,
    val retirementType: String, // "Doctore Olarak Emekli", "Tahta Kılıç (Rudis) İle Azad Edildi", "Sine Missio Şehidi"
    val retiredDay: Int,
    val honorsDescription: String,
    val icon: String = "🏆"
)

data class RivalLudus(
    val id: String,
    val name: String,
    val city: String,
    val lanistaName: String,
    val points: Int,
    val wins: Int,
    val losses: Int,
    val schoolDoctrine: String,
    val championName: String,
    val badgeIcon: String,
    val isPlayerSchool: Boolean = false
)

enum class EquipmentSlot(val title: String, val icon: String) {
    WEAPON("Silah", "⚔️"),
    ARMOR("Zırh & Miğfer", "🛡️"),
    RELIC("Tılsım & Yadigâr", "📿")
}

enum class EquipmentRarity(val title: String, val colorHex: Long) {
    COMMON("Standart Roma Çeliği", 0xFF9E9E9E),
    RARE("Lejyoner İşçiliği", 0xFF0284C7),
    EPIC("Gladyatör Şampiyon Seti", 0xFF7E22CE),
    LEGENDARY("İmparatorluk & Tanrıların Armağanı", 0xFFB8860B)
}

data class EquipmentItem(
    val id: String,
    val name: String,
    val slot: EquipmentSlot,
    val rarity: EquipmentRarity,
    val damageBonus: Int = 0,
    val armorReductionPercent: Int = 0,
    val critBonusPercent: Int = 0,
    val dodgeBonusPercent: Int = 0,
    val maxHpBonus: Int = 0,
    val priceGold: Int,
    val description: String,
    val icon: String
)

enum class LanistaPerk(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val prestigeCost: Int,
    val discountPercent: Int = 0,
    val trainingXpBonusPercent: Int = 0,
    val healingSpeedBonus: Int = 0,
    val executionGoldBonusPercent: Int = 0
) {
    IRON_DISCIPLINE(
        id = "perk_iron_discipline",
        title = "Demir Disiplin",
        description = "Antrenman verimi +%25 artar ve isyan/kaçma riski tamamen sıfırlanır.",
        icon = "⛓️",
        prestigeCost = 50,
        trainingXpBonusPercent = 25
    ),
    SILVER_TONGUE(
        id = "perk_silver_tongue",
        title = "Gümüş Dil (Tüccar Lanista)",
        description = "Köle pazarı ve demirci alışverişlerinde %20 indirim sağlar.",
        icon = "🪙",
        prestigeCost = 75,
        discountPercent = 20
    ),
    AESCULAPIUS_TOUCH(
        id = "perk_aesculapius",
        title = "Aesculapius'un Şifası",
        description = "Revirdeki tüm tedaviler 2 kat hızlı tamamlanır, şifa iksir masrafı %50 düşer.",
        icon = "🌿",
        prestigeCost = 100,
        healingSpeedBonus = 2
    ),
    BLOOD_EMPEROR(
        id = "perk_blood_emperor",
        title = "Kan İmparatoru",
        description = "Seyirci coşkusu 2 kat hızlı dolar; infaz edilen rakiplerden +%50 daha fazla altın kazanılır.",
        icon = "👑",
        prestigeCost = 150,
        executionGoldBonusPercent = 50
    )
}

data class CampaignMission(
    val id: String,
    val chapterOrder: Int,
    val chapterTitle: String,
    val missionTitle: String,
    val description: String,
    val bossEnemy: EnemyGladiator,
    val rewardGold: Int,
    val rewardPrestige: Int,
    val trophyName: String,
    val isCompleted: Boolean = false
)

enum class UndergroundFightType(
    val title: String,
    val subtitle: String,
    val description: String,
    val goldReward: Int,
    val prestigeReward: Int,
    val icon: String
) {
    VENATIO_BEASTS(
        title = "Venatio (Vahşi Hayvan Avı)",
        subtitle = "Arenaya salınan aç kurtlar ve Afrika aslanı!",
        description = "Halkın en sevdiği gösteri! Vahşi hayvanlara karşı refleks ve kalkan testi.",
        goldReward = 140,
        prestigeReward = 45,
        icon = "🦁"
    ),
    NIGHT_PIT_DUEL(
        title = "Subura Kan Çukuru (Gece Dövüşü)",
        subtitle = "Kural yok, hakem yok, yasadışı yüksek bahis!",
        description = "Tefecilerin ve zengin aristokratların gizlice izlediği kanlı yeraltı düellosu.",
        goldReward = 280,
        prestigeReward = 20,
        icon = "💀"
    )
}

enum class GladiatorCareerRank(
    val title: String,
    val shortTitle: String,
    val icon: String,
    val minWins: Int,
    val purseShareBonusPercent: Int,
    val hypeMultiplier: Float
) {
    TIRO("Tiro (Acemi Çaylak)", "Tiro", "⚔️", 0, 0, 1.0f),
    GREGARIUS("Gregarius (Savaşçı)", "Gregarius", "🛡️", 3, 5, 1.15f),
    VETERANUS("Veteranus (Kıdemli)", "Veteranus", "👑", 6, 10, 1.30f),
    CHAMPION("Arena Şampiyonu", "Şampiyon", "🏆", 10, 15, 1.50f),
    PRIMUS_PALUS("Primus Palus (Colosseum Efsanesi)", "Primus Palus", "🏛️", 15, 25, 2.00f);

    companion object {
        fun fromWins(wins: Int): GladiatorCareerRank = when {
            wins >= 15 -> PRIMUS_PALUS
            wins >= 10 -> CHAMPION
            wins >= 6 -> VETERANUS
            wins >= 3 -> GREGARIUS
            else -> TIRO
        }
    }
}



data class EnemyGladiator(
    val name: String,
    val title: String,
    val ludusOrigin: String,
    val gladiatorClass: GladiatorClass,
    val tier: EnemyTier = EnemyTier.NOVICE,
    val level: Int = 1,
    val traitName: String = "Dengeli",
    val traitDescription: String = "Standart dövüş stili.",
    val str: Int,
    val agi: Int,
    val sta: Int,
    val mor: Int,
    val maxHp: Int,
    val currentHp: Int,
    val rewardGold: Int,
    val rewardPrestige: Int
) {
    val powerScore: Int
        get() = (str * 2 + agi * 2 + sta * 2 + (mor / 5)) + (level * 5)
}

data class LudusState(
    val day: Int = 1,
    val phase: DayPhase = DayPhase.MORNING,
    val gold: Int = 350,
    val prestige: Int = 20,
    val maxGladiatorSlots: Int = 4,
    
    // Staff & Upgrades
    val physicianLevel: Int = 1, // 1: Şifacı Çırağı, 2: Ordu Cerrahı, 3: Yunan Başhekimi
    val guardsHired: Int = 1,     // Ludus Muhafızları
    val maxGuards: Int = 4,
    
    // Training & Teachers & Diet
    val dietPlan: DietPlan = DietPlan.BARLEY_PORRIDGE,
    val nextScheduledMatchDay: Int = 5,
    val currentScheduledEvent: ScheduledArenaEvent? = null,
    val upcomingCalendarEvents: List<ScheduledArenaEvent> = emptyList(),
    val activeTeachers: List<Teacher> = emptyList(),
    
    // Debt & Threat
    val activeDebt: Int = 0,
    val debtDueDaysLeft: Int = 0,
    val threatStage: ThreatStage = ThreatStage.NONE,
    val lastThreatEventMessage: String? = null,
    
    // Progress
    val cityTier: CityTier = CityTier.TOWN_RURAL,
    val totalFights: Int = 0,
    val totalWins: Int = 0,
    val freedGladiatorsCount: Int = 0,
    val senatorSponsorshipDays: Int = 0,

    // Between-Cycle Buffs & Roman Activities
    val scoutedEnemyWeakness: Boolean = false,
    val sharpenedWeapons: Boolean = false,
    val crowdHypeBonus: Boolean = false,
    val rivalWeakenedByPoison: Boolean = false,
    val marsDivineBlessing: Boolean = false,
    val currentDilemma: DailyDilemma? = null,
    val activeBetweenCycleEvent: BetweenCycleEvent? = null,
    
    // Crowd Judgement, Sentiment & Opponent Difficulty Progression
    val opponentDifficultyModifier: Float = 1.0f, // 0.85f (Honor/Mercy baseline) to 1.85f (Ruthless executioner / Blood vendetta)
    val ruthlessnessScore: Int = 0,               // Executions ordered (Iugula count)
    val mercyScore: Int = 0,                      // Mercies granted (Missio count)
    val crowdSentimentScore: Int = 50,            // 0 (Bloodlust mob) to 100 (Revered imperial adoration)
    val lastMatchExcitement: Int = 50,            // 0 to 100 combat excitement rating
    val lastDecisionConsequence: String? = null,

    // Progression: Lanista Perks & Imperial Campaign
    val unlockedPerkIds: List<String> = emptyList(),
    val completedCampaignMissionIds: List<String> = emptyList()
) {

    val daysUntilNextFight: Int
        get() = (nextScheduledMatchDay - day).coerceAtLeast(0)

    val isFightDay: Boolean
        get() = day >= nextScheduledMatchDay
        
    val crowdSentimentLevel: CrowdSentimentLevel
        get() = CrowdSentimentLevel.fromScore(crowdSentimentScore, ruthlessnessScore, mercyScore)

    val dynamicOpponentDifficultyPercent: Int
        get() = ((opponentDifficultyModifier + crowdSentimentLevel.threatModifier) * 100).toInt().coerceIn(70, 250)

    val reputationTitle: String
        get() = when {
            ruthlessnessScore >= 5 -> "💀 Kanlı Cellat Lanista"
            ruthlessnessScore >= 2 -> "⚔️ Acımasız Savaş Lordu"
            mercyScore >= 5 -> "🏛️ Asil Roma Koruyucusu (Clementia)"
            mercyScore >= 2 -> "🕊️ Onurlu Lanista"
            else -> "⚖️ Dengeli Arena Yöneticisi"
        }
}

enum class BetweenCycleEventType(val displayName: String, val icon: String, val badgeColorHex: Long) {
    MARKET_FESTIVAL("Pazar & Ticaret Şenliği", "🎪", 0xFFFFB300),
    SECRET_SOCIETY("Gizli Cemiyet & Yeraltı", "🗡️", 0xFFAB47BC),
    SENATORIAL_PATRONAGE("Senato & Soylu Hamiliği", "🏛️", 0xFF42A5F5),
    TEMPLE_SACRIFICE("Tapınak & Mars Kutsaması", "🔥", 0xFFEF5350),
    REBEL_CONSPIRACY("Köle & İsyan Fısıltıları", "⛓️", 0xFFFF7043),
    FOREIGN_CARAVAN("Doğu & Mısır Kervanı", "🐫", 0xFF26A69A),
    LOCAL_MERCHANT("Yerel Tüccar & Silahçı", "🏺", 0xFFFFCA28),
    MYSTERIOUS_TRAVELER("Gizemli Gezgin & Filozof", "🔮", 0xFF8E24AA),
    BLACK_MARKET_BEASTS("Yırtıcı Canavar Simsarı", "🦁", 0xFFFF8A65),
    BLACKSMITH_GUILD("Vulcan Demirci Loncası", "🛡️", 0xFF78909C),
    UNDERGROUND_FIGHT("Subura Yeraltı Çukuru", "💀", 0xFFE53935)
}

data class EventDecisionChoice(
    val id: String,
    val title: String,
    val description: String,
    val icon: String = "👉",
    val goldCost: Int = 0,
    val goldReward: Int = 0,
    val prestigeReward: Int = 0,
    val moraleChange: Int = 0,
    val fatigueChange: Int = 0,
    val teamXpReward: Int = 0,
    val targetGladiatorStatBonus: String? = null,
    val targetStatType: String? = null, // "STR", "AGI", "STA", "MAX_HP", "WEAPON_LEVEL", "ARMOR_LEVEL"
    val statAmount: Int = 0,
    val activatesPoison: Boolean = false,
    val activatesMarsBlessing: Boolean = false,
    val activatesSharpenedWeapons: Boolean = false,
    val activatesScoutBonus: Boolean = false,
    val activatesCrowdHype: Boolean = false,
    val recruitsShadowGladiator: Boolean = false,
    val reducesDebtAmount: Int = 0,
    val healsInjuriesDays: Int = 0,
    val riskChancePercent: Int = 0,
    val riskFailureNarrative: String? = null,
    val narrativeOutcome: String
)

data class BetweenCycleEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: BetweenCycleEventType,
    val dayOccurred: Int,
    val locationName: String,
    val narrativeText: String,
    val choices: List<EventDecisionChoice>,
    val requiresTargetGladiator: Boolean = false,
    val isUrgent: Boolean = false
)

data class EventResolutionResult(
    val title: String,
    val story: String,
    val wasRiskTriggered: Boolean = false,
    val summaryChanges: List<String> = emptyList()
)

data class DilemmaOption(
    val id: String,
    val label: String,
    val effectDescription: String,
    val goldCost: Int = 0,
    val goldReward: Int = 0,
    val prestigeReward: Int = 0,
    val moraleChange: Int = 0,
    val fatigueChange: Int = 0,
    val outcomeStory: String
)

data class DailyDilemma(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val description: String,
    val options: List<DilemmaOption>
)

data class SparringLog(
    val text: String,
    val isGladiatorOneAction: Boolean,
    val isDecisiveBlow: Boolean = false
)

data class SparringState(
    val gladiatorOne: Gladiator,
    val gladiatorTwo: Gladiator, // Could be another gladiator or Doctore Sparring Dummy
    val rounds: List<SparringLog> = emptyList(),
    val winnerName: String? = null,
    val xpGained: Int = 20,
    val statBoostSummary: String? = null,
    val isComplete: Boolean = false
)

data class TesseraeGameState(
    val betAmount: Int = 25,
    val playerDice: List<Int> = listOf(1, 1, 1),
    val rivalDice: List<Int> = listOf(1, 1, 1),
    val playerTotal: Int = 3,
    val rivalTotal: Int = 3,
    val rollName: String = "Zar Atılmadı",
    val isPlayerWinner: Boolean? = null,
    val goldReward: Int = 0,
    val hasPlayedThisCycle: Boolean = false,
    val isRolling: Boolean = false
)

data class BattleActionLog(
    val text: String,
    val isPlayerAction: Boolean,
    val isCritical: Boolean = false,
    val damageDealt: Int = 0,
    val crowdReaction: String? = null
)

data class BattleState(
    val playerGladiator: Gladiator,
    val secondaryPlayerGladiator: Gladiator? = null,
    val enemyGladiator: EnemyGladiator,
    val secondaryEnemyGladiator: EnemyGladiator? = null,
    val tactic: BattleTactic,
    val matchFormat: MatchFormat,
    val betAmount: Int = 0,
    val scheduledEvent: ScheduledArenaEvent? = null,
    
    val playerCurrentHp: Int,
    val playerCurrentStamina: Int,
    val enemyCurrentHp: Int,
    val enemyCurrentStamina: Int,
    
    val crowdHype: Int = 50, // 0 - 100
    val turnCount: Int = 0,
    val logs: List<BattleActionLog> = emptyList(),
    val isAwaitingCrowdJudgement: Boolean = false,
    val crowdJudgementDecision: CrowdVerdict? = null,
    val isEnemySpared: Boolean? = null,
    val isFinished: Boolean = false,
    val isPlayerVictorious: Boolean = false,
    val playerSurrenderedOrDied: Boolean = false,
    val outcomeSummary: String? = null,
    val earnedGold: Int = 0,
    val earnedPrestige: Int = 0,
    val wasFreedByPromise: Boolean = false,
    val sufferedPermanentInjury: Boolean = false,
    val injurySufferedDesc: String? = null,
    val physicianInterventionSavedLife: Boolean = false,
    val campaignMissionId: String? = null
) {
    val playerHealthPercent: Float
        get() = if (playerGladiator.maxHp > 0) (playerCurrentHp.toFloat() / playerGladiator.maxHp).coerceIn(0f, 1f) else 0f

    val enemyHealthPercent: Float
        get() = if (enemyGladiator.maxHp > 0) (enemyCurrentHp.toFloat() / enemyGladiator.maxHp).coerceIn(0f, 1f) else 0f

    // Sentiment Calculation (0% Full Clemency / Mercy <---> 100% Extreme Bloodlust / Execution)
    val crowdBloodlustPercent: Int
        get() {
            var score = crowdHype
            if (tactic == BattleTactic.AGGRESSIVE) score += 15
            if (tactic == BattleTactic.MAIMING) score += 20
            if (tactic == BattleTactic.CROWD_PLEASER) score -= 10
            if (tactic == BattleTactic.DEFENSIVE) score -= 15
            if (matchFormat == MatchFormat.SINE_MISSIO) score += 25
            if (matchFormat == MatchFormat.LUSUS) score -= 35
            // If the enemy fought valiantly (long fight > 5 turns or close health), spectators develop respect for the fighter
            if (turnCount >= 6) score -= 15
            if (playerHealthPercent < 0.35f) score -= 10 // Great close duel
            return score.coerceIn(5, 95)
        }

    val crowdDemandedVerdict: CrowdVerdict
        get() = if (crowdBloodlustPercent >= 55) CrowdVerdict.IUGULA else CrowdVerdict.MISSIO

    val enemyValorTitle: String
        get() = when {
            turnCount >= 7 || playerHealthPercent < 0.40f -> "🦁 Destansı Direniş & Onurlu Savaşçı"
            turnCount >= 4 -> "🛡️ Cesur Mücadele"
            else -> "⚡ Hızlıca Devrildi"
        }

    val calculatedMercyPrestigeGain: Int
        get() {
            val baseP = matchFormat.rewardPrestigeBase + (scheduledEvent?.rewardPrestige ?: enemyGladiator.rewardPrestige)
            val sentimentAlignmentBonus = if (crowdBloodlustPercent < 55) 45 else 20
            val valorBonus = if (turnCount >= 6) 15 else 5
            return baseP + sentimentAlignmentBonus + valorBonus
        }

    val calculatedExecutionGoldGain: Int
        get() {
            val baseG = matchFormat.rewardGoldBase + (scheduledEvent?.rewardGold ?: enemyGladiator.rewardGold)
            val bloodLustBonus = if (crowdBloodlustPercent >= 55) 80 else 40
            val betGain = if (betAmount > 0) betAmount * 2 else 0
            return baseG + bloodLustBonus + betGain
        }

    val calculatedExecutionPrestigeGain: Int
        get() {
            val baseP = matchFormat.rewardPrestigeBase + (scheduledEvent?.rewardPrestige ?: enemyGladiator.rewardPrestige)
            // If crowd wanted mercy but you executed, prestige is reduced due to unprovoked cruelty
            return if (crowdBloodlustPercent < 45) {
                (baseP * 0.70f).toInt().coerceAtLeast(10)
            } else {
                baseP + 30
            }
        }
}

data class ShopItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val priceGold: Int? = null,
    val priceIapDesc: String? = null,
    val iconName: String
)
