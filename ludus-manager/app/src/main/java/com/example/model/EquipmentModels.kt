package com.example.model

import kotlin.math.roundToInt
import kotlin.random.Random

enum class EquipmentSlot(val displayName: String) {
    HELMET("Miğfer (Galea)"),
    BODY_ARMOR("Gövde Zırhı (Lorica)"),
    LEFT_ARM("Sol Kol (Galerus/Manica)"),
    RIGHT_ARM("Sağ Kol (Manica)"),
    MAIN_HAND("Ana El Silahı"),
    OFF_HAND("İkincil El / Hançer"),
    SHIELD("Kalkan (Scutum/Parma)"),
    LEGS("Bacak Zırhı (Ocreae)"),
    ACCESSORY("Aksesuar (Balteus/Caligae)")
}

enum class EquipmentCategory(val displayName: String, val iconName: String) {
    WEAPONS("Silahlar", "ic_weapon"),
    SHIELDS("Kalkanlar", "ic_shield"),
    ARMOR("Zırhlar", "ic_armor"),
    HELMETS("Miğferler", "ic_helmet"),
    ACCESSORIES("Aksesuarlar", "ic_accessory")
}

enum class EquipmentType(
    val category: EquipmentCategory,
    val defaultSlot: EquipmentSlot,
    val displayName: String,
    val description: String
) {
    // WEAPONS
    GLADIUS(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Gladius Hispaniensis", "Roma lejyoner ve gladyatör kılıcı. İtme ve yakın mesafe saplamada ölümcül."),
    SPATHA(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Spatha Kılıcı", "Daha uzun kesici kılıç. Yüksek erişim ve biçme gücü, süvari ve veteranların tercihi."),
    SICA(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Sica Eğri Kılıcı", "Trak eğri kılıcı. Kalkanların arkasına dolanarak boyun ve sırt kesiği atar."),
    FALCATA(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Falcata / Kopis", "Ağır kavisli İber palası. Kafataslarını ve kalkanları parçalayan ağırlık merkezi."),
    SPEAR(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Hasta / Venabulum Mızrağı", "Uzun erişimli avcı ve Hoplomachus mızrağı. Düşmanı uzakta tutar."),
    TRIDENT(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Tridens (Üçlü Yaba)", "Retiarius'un imzası. Rakibin kılıcını kilitleme ve deşme kabiliyeti."),
    DAGGER(EquipmentCategory.WEAPONS, EquipmentSlot.OFF_HAND, "Pugio Hançeri", "Güreş ve yakın sarılma esnasında zırh aralıklarına saplanan hançer."),
    TWIN_SWORDS(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Dimachaerus Çift Kılıç", "Kalkansız, çift elle kullanılan amansız saldırı bıçakları."),
    TRAINING_WEAPON(EquipmentCategory.WEAPONS, EquipmentSlot.MAIN_HAND, "Rudis Tahta Kılıç", "Ağır meşe talim kılıcı. Yaralama riski olmadan kas ve refleks geliştirir."),

    // SHIELDS
    SCUTUM(EquipmentCategory.SHIELDS, EquipmentSlot.SHIELD, "Büyük Scutum Kalkanı", "Murmillo ve lejyonerlerin kullandığı devasa eğri kalkan. Muazzam koruma."),
    PARMA(EquipmentCategory.SHIELDS, EquipmentSlot.SHIELD, "Parma Yuvarlak Kalkan", "Hafif ve çevik yuvarlak kalkan. Hızlı karşı manevralara imkan tanır."),
    BUCKLER(EquipmentCategory.SHIELDS, EquipmentSlot.SHIELD, "Tunç Buckler", "Çok küçük yumruk kalkanı. Saptırma ve çene darbesi için ideal."),
    THRAEX_SHIELD(EquipmentCategory.SHIELDS, EquipmentSlot.SHIELD, "Thraex Dikdörtgen Kalkanı", "Kompakt kare-dikdörtgen kalkan. Sica ile kusursuz uyum sağlar."),
    HOPLON(EquipmentCategory.SHIELDS, EquipmentSlot.SHIELD, "Yunan Hoplon Kalkanı", "Ağır bronz kaplama yuvarlak kalkan. Mızrak dövüşünde sağlam duvar."),

    // ARMOR
    LORICA_HAMATA(EquipmentCategory.ARMOR, EquipmentSlot.BODY_ARMOR, "Lorica Hamata (Zincir Zırh)", "Demir halkalardan örülü esnek zırh. Kesiklere karşı mükemmel."),
    LORICA_SEGMENTATA(EquipmentCategory.ARMOR, EquipmentSlot.BODY_ARMOR, "Lorica Segmentata (Plaka Zırh)", "Çelik şeritli ağır zırh. Azami darbe ve ezilme koruması, yüksek ağırlık."),
    LEATHER_ARMOR(EquipmentCategory.ARMOR, EquipmentSlot.BODY_ARMOR, "Sertleştirilmiş Deri Zırh", "Hafif ve terletmeyen deri yelek. Hız ve çevikliği kısıtlamaz."),
    PADDED_SUBARMALIS(EquipmentCategory.ARMOR, EquipmentSlot.BODY_ARMOR, "Dolgulu Subarmalis", "Keten ve yün dolgulu darbe emici koruyucu. Acıyı ve morlukları azaltır."),
    SHOULDER_GALERUS(EquipmentCategory.ARMOR, EquipmentSlot.LEFT_ARM, "Galerus Omuzluk", "Retiarius'un sol omzunu ve boynunu koruyan yüksek bronz siperlik."),
    ARM_MANICA(EquipmentCategory.ARMOR, EquipmentSlot.RIGHT_ARM, "Demir/Deri Manica Kol Zırhı", "Kılıç tutan kolu boydan boya koruyan mafsallı zırh."),
    GREAVES_OCREAE(EquipmentCategory.ARMOR, EquipmentSlot.LEGS, "Ocreae Dizçek & Baldırlık", "Bacakları koruyan bronz veya demir tozluklar."),
    HIGH_GREAVES(EquipmentCategory.ARMOR, EquipmentSlot.LEGS, "Yüksek Bronz Dizçekler", "Diz kapağını ve uyluk altını koruyan uzun Trak tipi dizçekler."),

    // HELMETS
    MURMILLO_HELMET(EquipmentCategory.HELMETS, EquipmentSlot.HELMET, "Murmillo Miğferi", "Geniş siperlikli ve balık armalı ızgaralı ağır bronz miğfer."),
    THRAEX_HELMET(EquipmentCategory.HELMETS, EquipmentSlot.HELMET, "Thraex Grifon Miğferi", "Korkutucu grifon tepelikli, dar göz delikli Trak miğferi."),
    SECUTOR_HELMET(EquipmentCategory.HELMETS, EquipmentSlot.HELMET, "Secutor Pürüzsüz Miğfer", "Ağın takılmaması için tamamen yuvarlak ve pürüzsüz, dar görüşlü miğfer."),
    OPEN_FACE_HELMET(EquipmentCategory.HELMETS, EquipmentSlot.HELMET, "Açık Yüz Miğferi", "Yüzü açık bırakan, nefes almayı ve görüşü engellemeyen hafif miğfer."),
    REINFORCED_HELMET(EquipmentCategory.HELMETS, EquipmentSlot.HELMET, "Gladyatör Ağır Miğferi", "Çift kat dövülmüş demir siperlik. Ezici darbelere tam direnç."),
    CUSTOM_HELMET(EquipmentCategory.HELMETS, EquipmentSlot.HELMET, "Özel İmalat Miğfer", "Lanista veya usta demirci tarafından özel dövülmüş gladyatör başlığı."),

    // ACCESSORIES
    BALTEUS_BELT(EquipmentCategory.ACCESSORIES, EquipmentSlot.ACCESSORY, "Balteus Deri Kemer", "Geniş bronz perçinli dövüş kemeri. Karın bölgesine destek ve kılıç kılıfı sağlar."),
    CALIGAE_SANDALS(EquipmentCategory.ACCESSORIES, EquipmentSlot.ACCESSORY, "Çivili Caligae Sandaletleri", "Arenanın kanlı kumlarında kaymayı önleyen çivili askeri sandalet."),
    FASCIAE_STRAPS(EquipmentCategory.ACCESSORIES, EquipmentSlot.ACCESSORY, "Fasciae Yün Sarıkları", "Ayak bileği ve dizleri burkulmalara karşı sıkan yün bandajlar."),
    LEATHER_GLOVES(EquipmentCategory.ACCESSORIES, EquipmentSlot.ACCESSORY, "Deri Dövüş Sargısı / Eldiveni", "Kavramayı güçlendiren ve el bileğini koruyan sertleştirilmiş deri sargı."),
    TRAINING_GEAR(EquipmentCategory.ACCESSORIES, EquipmentSlot.ACCESSORY, "Ağır Kurşunlu Ağırlık Kemeri", "Talimde bacak kaslarını ve dayanıklılığı artıran idman teçhizatı.")
}

enum class EquipmentQuality(
    val title: String,
    val statMultiplier: Float,
    val durabilityMultiplier: Float,
    val priceMultiplier: Float,
    val colorHex: Long,
    val prestigeBonus: Int
) {
    CRUDE("Kaba (Crude)", 0.75f, 0.65f, 0.50f, 0xFF8D6E63, 0),
    COMMON("Standart (Common)", 1.0f, 1.0f, 1.0f, 0xFFD7CCC8, 5),
    FINE("Usta İşi (Fine)", 1.25f, 1.30f, 1.80f, 0xFF60A5FA, 20),
    MASTERWORK("Şaheser (Masterwork)", 1.55f, 1.65f, 3.20f, 0xFFA78BFA, 50),
    FAMOUS("Şöhretli (Famous)", 1.85f, 2.00f, 5.50f, 0xFFF59E0B, 100),
    NAMED("Efsanevi & İsimli (Named)", 2.20f, 2.50f, 9.00f, 0xFFEF4444, 200)
}

enum class EquipmentMaterial(
    val displayName: String,
    val damageMod: Float,
    val protectionMod: Float,
    val durabilityMod: Float,
    val weightMod: Float,
    val priceMod: Float,
    val prestige: Int
) {
    STANDARD_IRON("Dövme Roma Demiri", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 5),
    HIGH_QUALITY_IRON("Campania Kaliteli Demiri", 1.15f, 1.15f, 1.25f, 0.95f, 1.6f, 15),
    IBERIAN_STEEL("Toledo / İber Çeliği", 1.35f, 1.35f, 1.50f, 0.90f, 2.8f, 35),
    ORNATE_BRONZE("İşlemeli Korint Tuncu", 1.05f, 1.10f, 1.20f, 1.10f, 2.2f, 40),
    RARE_DAMASCUS_STEEL("Parthian / Şam Çeliği (Crucible)", 1.60f, 1.55f, 1.80f, 0.85f, 5.0f, 80),
    GILDED_STEEL("Altın Yaldızlı Senatör Çeliği", 1.25f, 1.30f, 1.40f, 1.05f, 4.2f, 120)
}

enum class EquipmentConditionState(val displayName: String, val performanceRatio: Float) {
    PRISTINE("Kusursuz (Pristine)", 1.0f),
    GOOD("İyi (Good)", 0.95f),
    WORN("Aşınmış (Worn)", 0.80f),
    DAMAGED("Hasarlı (Damaged)", 0.60f),
    BROKEN("Kırık / Parçalanmış (Broken)", 0.20f)
}

data class EquipmentProvenance(
    val previousOwner: String? = null,
    val previousLudus: String? = null,
    val victoriesRecorded: Int = 0,
    val fatalKills: Int = 0,
    val battleLore: String = "",
    val bonusPrestige: Int = 0
)

data class EquipmentItem(
    val id: String,
    val name: String,
    val category: EquipmentCategory,
    val type: EquipmentType,
    val slot: EquipmentSlot,
    val quality: EquipmentQuality = EquipmentQuality.COMMON,
    val material: EquipmentMaterial = EquipmentMaterial.STANDARD_IRON,
    val maxDurability: Int = 100,
    var currentDurability: Int = 100,

    // WEAPON SPECIFIC PROPERTIES
    val damage: Int = 12,
    val reachCm: Int = 65,
    val attackSpeed: Int = 60,              // 1 - 100
    val armorPenetrationPercent: Int = 15,  // 0 - 100%
    val weightKg: Float = 1.4f,
    val balance: Int = 75,                  // 1 - 100
    val reliability: Int = 90,              // 1 - 100% (jam/twist resistance)
    val staminaCost: Float = 6.0f,
    val specialProperty: String = "Standart kesik ve saplama",

    // ARMOR / SHIELD / HELMET PROPERTIES
    val protection: Int = 10,
    val coveragePercent: Int = 40,
    val mobilityPenalty: Int = 2,           // Subtracted from speed/agility
    val staminaDrainPercent: Int = 5,       // Extra fatigue per second
    val heatResistancePenalty: Int = 4,     // Heat buildup in arena
    val painResistanceBonus: Int = 4,

    // SHIELD SPECIFIC
    val blockEffectiveness: Int = 50,       // 0 - 100%
    val bashEffectiveness: Int = 10,        // Stagger / stun potential

    // HELMET SPECIFIC
    val visibilityPercent: Int = 85,        // 100% is unobstructed
    val hearingPercent: Int = 80,           // Obeying shouted tactical orders

    // ECONOMY & PROVENANCE
    val basePriceDenarii: Int = 180,
    val prestigeBonus: Int = 5,
    val isUsed: Boolean = false,
    val provenance: EquipmentProvenance? = null,
    val commissionedSmith: String? = null,
    val isNamedArtifact: Boolean = false,
    val sellerMerchantId: String? = null
) {
    val conditionState: EquipmentConditionState
        get() {
            val ratio = if (maxDurability > 0) currentDurability.toFloat() / maxDurability else 0f
            return when {
                ratio >= 0.95f -> EquipmentConditionState.PRISTINE
                ratio >= 0.70f -> EquipmentConditionState.GOOD
                ratio >= 0.40f -> EquipmentConditionState.WORN
                ratio > 0.05f -> EquipmentConditionState.DAMAGED
                else -> EquipmentConditionState.BROKEN
            }
        }

    val effectiveDamage: Int
        get() = ((damage * quality.statMultiplier * material.damageMod * conditionState.performanceRatio)).roundToInt()

    val effectiveProtection: Int
        get() = ((protection * quality.statMultiplier * material.protectionMod * conditionState.performanceRatio)).roundToInt()

    val effectiveBlock: Int
        get() = ((blockEffectiveness * quality.statMultiplier * conditionState.performanceRatio)).roundToInt().coerceIn(0, 95)

    val currentMarketValue: Int
        get() {
            val qualMod = quality.priceMultiplier
            val matMod = material.priceMod
            val condMod = if (conditionState == EquipmentConditionState.BROKEN) 0.15f else conditionState.performanceRatio
            val usedDiscount = if (isUsed) 0.65f else 1.0f
            val provBonus = if (provenance != null) 1.25f + (provenance.victoriesRecorded * 0.04f) else 1.0f
            val namedBonus = if (isNamedArtifact) 2.2f else 1.0f
            return (basePriceDenarii * qualMod * matMod * condMod * usedDiscount * provBonus * namedBonus).roundToInt().coerceAtLeast(15)
        }

    val repairCost: Int
        get() {
            val missing = maxDurability - currentDurability
            if (missing <= 0) return 0
            val costPerPoint = (basePriceDenarii * 0.006f * material.priceMod).coerceAtLeast(0.5f)
            return (missing * costPerPoint).roundToInt().coerceAtLeast(5)
        }
}

data class GladiatorLoadout(
    val gladiatorId: String,
    var helmet: EquipmentItem? = null,
    var bodyArmor: EquipmentItem? = null,
    var leftArm: EquipmentItem? = null,
    var rightArm: EquipmentItem? = null,
    var mainHand: EquipmentItem? = null,
    var offHand: EquipmentItem? = null,
    var shield: EquipmentItem? = null,
    var legs: EquipmentItem? = null,
    var accessory: EquipmentItem? = null
) {
    val totalWeightKg: Float
        get() = listOfNotNull(helmet, bodyArmor, leftArm, rightArm, mainHand, offHand, shield, legs, accessory).sumOf { it.weightKg.toDouble() }.toFloat()

    val totalProtection: Int
        get() = listOfNotNull(helmet, bodyArmor, leftArm, rightArm, shield, legs, accessory).sumOf { it.effectiveProtection }

    val totalMobilityPenalty: Int
        get() = listOfNotNull(helmet, bodyArmor, leftArm, rightArm, shield, legs, accessory).sumOf { it.mobilityPenalty }

    val totalStaminaDrainPercent: Int
        get() = listOfNotNull(helmet, bodyArmor, leftArm, rightArm, shield, legs, accessory).sumOf { it.staminaDrainPercent }

    val totalHeatPenalty: Int
        get() = listOfNotNull(helmet, bodyArmor, leftArm, rightArm, shield, legs, accessory).sumOf { it.heatResistancePenalty }

    val totalPrestige: Int
        get() = listOfNotNull(helmet, bodyArmor, leftArm, rightArm, mainHand, offHand, shield, legs, accessory).sumOf { it.prestigeBonus + (it.provenance?.bonusPrestige ?: 0) }

    fun getItemInSlot(slot: EquipmentSlot): EquipmentItem? = when (slot) {
        EquipmentSlot.HELMET -> helmet
        EquipmentSlot.BODY_ARMOR -> bodyArmor
        EquipmentSlot.LEFT_ARM -> leftArm
        EquipmentSlot.RIGHT_ARM -> rightArm
        EquipmentSlot.MAIN_HAND -> mainHand
        EquipmentSlot.OFF_HAND -> offHand
        EquipmentSlot.SHIELD -> shield
        EquipmentSlot.LEGS -> legs
        EquipmentSlot.ACCESSORY -> accessory
    }

    fun setItemInSlot(slot: EquipmentSlot, item: EquipmentItem?): GladiatorLoadout {
        return when (slot) {
            EquipmentSlot.HELMET -> copy(helmet = item)
            EquipmentSlot.BODY_ARMOR -> copy(bodyArmor = item)
            EquipmentSlot.LEFT_ARM -> copy(leftArm = item)
            EquipmentSlot.RIGHT_ARM -> copy(rightArm = item)
            EquipmentSlot.MAIN_HAND -> copy(mainHand = item)
            EquipmentSlot.OFF_HAND -> copy(offHand = item)
            EquipmentSlot.SHIELD -> copy(shield = item)
            EquipmentSlot.LEGS -> copy(legs = item)
            EquipmentSlot.ACCESSORY -> copy(accessory = item)
        }
    }
}

data class GladiatorEquipmentPreference(
    val gladiatorId: String,
    val favoredWeaponType: EquipmentType,
    val hatesHeavyArmor: Boolean = false,
    val desiresHeavyProtection: Boolean = false,
    val attachedEquipmentId: String? = null,
    val attachedItemName: String? = null,
    val attachedSentimentalValue: Int = 15, // Morale boost when wielding
    val complaintText: String? = null
)

data class RomanMerchant(
    val id: String,
    val name: String,
    val title: String,
    val location: String,
    val specialty: String,
    val specialtyCategory: EquipmentCategory?,
    val skillLevel: String, // "Çırak", "Kalfa", "Usta Demirci", "İmparatorluk Tedarikçisi"
    val priceMultiplier: Float, // e.g. 0.85 for cheap, 1.40 for luxury
    val reputation: Int,        // 0 - 100
    var playerRelationship: Int,// -100 to +100
    val backstory: String,
    val inventory: MutableList<EquipmentItem> = mutableListOf(),
    val willBuyUsed: Boolean = true,
    val discountPercent: Int = 0
)

data class EquipmentAuction(
    val id: String,
    val item: EquipmentItem,
    val startingBid: Int,
    var currentBid: Int,
    var highBidder: String,
    var isPlayerHighBidder: Boolean,
    val daysRemaining: Int,
    val bidders: MutableList<String> = mutableListOf(),
    val historicalProvenance: String,
    val minBidIncrement: Int = 100
)

data class CustomCommission(
    val id: String,
    val gladiatorId: String?,
    val gladiatorName: String?,
    val weaponName: String,
    val category: EquipmentCategory,
    val type: EquipmentType,
    val material: EquipmentMaterial,
    val weightProfile: String, // "Light & Fast", "Balanced", "Heavy & Devastating"
    val bladeProfile: String,  // "Curved Slashing", "Pointed Thrusting", "Serrated Cleaver"
    val decoration: String,    // "Plain", "Roman Eagle Engraving", "Gold Trim", "Blood Inscription"
    val smithName: String,
    val totalCost: Int,
    var daysRemaining: Int,
    val totalDaysRequired: Int,
    val targetQuality: EquipmentQuality
) {
    val progressPercent: Float get() = if (totalDaysRequired > 0) ((totalDaysRequired - daysRemaining).toFloat() / totalDaysRequired).coerceIn(0f, 1f) else 1f
}

data class EquipmentMarketEvent(
    val id: String,
    val title: String,
    val description: String,
    val categoryAffected: EquipmentCategory?,
    val priceMultiplier: Float,
    val scarcityNote: String,
    var daysRemaining: Int,
    val isPositive: Boolean
)

data class PriceTrend(
    val category: EquipmentCategory,
    val trendName: String,
    val percentageChange: Int,
    val description: String,
    val isUp: Boolean
)

enum class MarketTab(val title: String) {
    MERCHANTS("Demirciler & Pazarlar"),
    USED_MARKET("İkinci El & Şecereli"),
    AUCTIONS("Canlı Müzayedeler"),
    COMMISSIONS("Özel Sipariş Ocağı"),
    ARMORY_REPAIRS("Cephanelik & Bakım")
}

data class EquipmentComparisonDiff(
    val marketItem: EquipmentItem,
    val currentItem: EquipmentItem?,
    val damageDelta: Int,
    val protectionDelta: Int,
    val mobilityDelta: Int,
    val staminaDrainDelta: Int,
    val weightDelta: Float,
    val durabilityDelta: Int,
    val priceDelta: Int
)

fun compareEquipment(marketItem: EquipmentItem, currentItem: EquipmentItem?): EquipmentComparisonDiff {
    val curDmg = currentItem?.effectiveDamage ?: 0
    val curProt = currentItem?.effectiveProtection ?: 0
    val curMob = currentItem?.mobilityPenalty ?: 0
    val curStam = currentItem?.staminaDrainPercent ?: 0
    val curWeight = currentItem?.weightKg ?: 0f
    val curDur = currentItem?.currentDurability ?: 0
    val curVal = currentItem?.currentMarketValue ?: 0

    return EquipmentComparisonDiff(
        marketItem = marketItem,
        currentItem = currentItem,
        damageDelta = marketItem.effectiveDamage - curDmg,
        protectionDelta = marketItem.effectiveProtection - curProt,
        mobilityDelta = marketItem.mobilityPenalty - curMob,
        staminaDrainDelta = marketItem.staminaDrainPercent - curStam,
        weightDelta = marketItem.weightKg - curWeight,
        durabilityDelta = marketItem.currentDurability - curDur,
        priceDelta = marketItem.currentMarketValue - curVal
    )
}

fun checkClassCompatibility(gladiatorClass: GladiatorClass, item: EquipmentItem): Pair<Boolean, String?> {
    return when (gladiatorClass) {
        GladiatorClass.RETIARIUS -> {
            if (item.type in listOf(EquipmentType.MURMILLO_HELMET, EquipmentType.SECUTOR_HELMET, EquipmentType.REINFORCED_HELMET)) {
                Pair(false, "Retiarius ağır miğfer takamaz; geniş görüş ve çevik soluk zorunludur.")
            } else if (item.category == EquipmentCategory.SHIELDS && item.type != EquipmentType.BUCKLER) {
                Pair(false, "Retiarius büyük kalkan taşıyamaz; ağı ve yabayı iki elle yönetmelidir.")
            } else if (item.type == EquipmentType.LORICA_SEGMENTATA) {
                Pair(false, "Ağır plaka zırh Retiarius'un çevik manevralarını engeller.")
            } else Pair(true, null)
        }
        GladiatorClass.DIMACHAERUS -> {
            if (item.category == EquipmentCategory.SHIELDS) {
                Pair(false, "Dimachaerus kalkan kullanmaz; çift el kılıç ustasıdır.")
            } else if (item.type == EquipmentType.LORICA_SEGMENTATA) {
                Pair(false, "Ağır plaka zırh çift kılıç akrobatik saldırılarını kısıtlar.")
            } else Pair(true, null)
        }
        GladiatorClass.MURMILLO -> {
            if (item.type == EquipmentType.TRIDENT || item.type == EquipmentType.TWIN_SWORDS) {
                Pair(false, "Murmillo geleneksel gladius ve scutum kalkanı taşımakla yükümlüdür.")
            } else Pair(true, null)
        }
        GladiatorClass.THRAEX -> {
            if (item.type == EquipmentType.SCUTUM) {
                Pair(false, "Thraex devasa lejyon scutum'u taşıyamaz; kompakt kare parma tercih eder.")
            } else if (item.type == EquipmentType.TRIDENT) {
                Pair(false, "Thraex stili kavisli Sica kılıcına odaklıdır.")
            } else Pair(true, null)
        }
        GladiatorClass.SECUTOR -> {
            if (item.type == EquipmentType.TRIDENT || item.type == EquipmentType.TWIN_SWORDS) {
                Pair(false, "Secutor pürüzsüz kaskı ve kılıcıyla Retiarius avcısıdır.")
            } else Pair(true, null)
        }
        GladiatorClass.HOPLOMACHUS -> {
            if (item.type == EquipmentType.TWIN_SWORDS) {
                Pair(false, "Hoplomachus mızrak ve hoplon kalkanı kuşanmalıdır.")
            } else Pair(true, null)
        }
        else -> Pair(true, null)
    }
}

