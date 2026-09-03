package com.example.simulation

import com.example.model.*
import kotlin.math.roundToInt
import kotlin.random.Random

object EquipmentEngine {

    fun createInitialMerchants(): List<RomanMerchant> {
        return listOf(
            RomanMerchant(
                id = "merch_servius",
                name = "Servius Ferrarius",
                title = "Capua Baş Silah Ustası",
                location = "Capua Zanaatkarlar Caddesi",
                specialty = "Dövme Kılıçlar & Mızraklar",
                specialtyCategory = EquipmentCategory.WEAPONS,
                skillLevel = "Usta Demirci (Magister)",
                priceMultiplier = 1.0f,
                reputation = 88,
                playerRelationship = 15,
                backstory = "Üç kuşaktır Capua amfitiyatrosunun en meşhur şampiyonlarına gladius döven Campania'lı kıdemli demirci.",
                inventory = generateServiusInventory().toMutableList(),
                willBuyUsed = true
            ),
            RomanMerchant(
                id = "merch_marcus",
                name = "Marcus Loricanus",
                title = "Zırh & Kalkan İmalatçısı",
                location = "Capua Dövüş Vadisi",
                specialty = "Lorica, Ocreae & Ağır Scutum",
                specialtyCategory = EquipmentCategory.ARMOR,
                skillLevel = "Zırh Ustası (Armamentarius)",
                priceMultiplier = 1.05f,
                reputation = 82,
                playerRelationship = 10,
                backstory = "Eski bir lejyoner zırhçısı. Segmentata plakalarını ve Murmillo kalkanlarını kusursuz büker.",
                inventory = generateMarcusInventory().toMutableList(),
                willBuyUsed = true
            ),
            RomanMerchant(
                id = "merch_hanno",
                name = "Hanno Punicus",
                title = "İkinci El & Savaş Hurdası Taciri",
                location = "Subura Çarşısı / Puteoli İskelesi",
                specialty = "Kullanılmış, Bıçkın & Şecereli Silahlar",
                specialtyCategory = null,
                skillLevel = "Kurnaz Çerçi",
                priceMultiplier = 0.70f,
                reputation = 60,
                playerRelationship = 5,
                backstory = "Arenada ölen veya emekli edilen dövüşçülerin teçhizatını kelepire toplar. Arada paha biçilmez antika şaheserler çıkar.",
                inventory = generateHannoUsedInventory().toMutableList(),
                willBuyUsed = true
            ),
            RomanMerchant(
                id = "merch_aurelius",
                name = "Aurelius Barbarus",
                title = "Doğu & İber İthalatçısı",
                location = "Puteoli Liman Depoları",
                specialty = "Trak Sica, Şam Çeliği & Falcata",
                specialtyCategory = EquipmentCategory.WEAPONS,
                skillLevel = "Büyük Tüccar (Negotiator)",
                priceMultiplier = 1.25f,
                reputation = 75,
                playerRelationship = 0,
                backstory = "İskenderiye ve Hispania'dan gelen gemilerden egzotik alaşımlar ve yabancı gladyatör silahları getirir.",
                inventory = generateAureliusInventory().toMutableList(),
                willBuyUsed = false
            ),
            RomanMerchant(
                id = "merch_decimus",
                name = "Decimus Tribunus",
                title = "Lejyon İkmal Müteahhidi",
                location = "Via Appia Askeri Kampı",
                specialty = "Ordu Malı Fazlası Zırh & Miğferler",
                specialtyCategory = EquipmentCategory.HELMETS,
                skillLevel = "Askeri Müteahhit",
                priceMultiplier = 0.90f,
                reputation = 90,
                playerRelationship = 20,
                backstory = "Roma lejyonlarının standart zırh, miğfer ve sandaletlerini resmi izinlerle lanistalara tahsis eder.",
                inventory = generateDecimusInventory().toMutableList(),
                willBuyUsed = true
            ),
            RomanMerchant(
                id = "merch_gaius",
                name = "Gaius Valerius Nobilis",
                title = "İmparatorluk Lüks Kuyumcusu",
                location = "Roma Palatine / Capua Villası",
                specialty = "Altın Yaldızlı Miğferler & Şaheserler",
                specialtyCategory = EquipmentCategory.ACCESSORIES,
                skillLevel = "Saray Kuyumcusu",
                priceMultiplier = 1.80f,
                reputation = 96,
                playerRelationship = -5,
                backstory = "Sadece senatörlerin ve zengin lanistaların kapısını çaldığı, törensel altın işlemeli şampiyon zırhları satar.",
                inventory = generateGaiusLuxuryInventory().toMutableList(),
                willBuyUsed = false
            )
        )
    }

    // -------------------------------------------------------------
    // SEED INVENTORIES
    // -------------------------------------------------------------
    private fun generateServiusInventory(): List<EquipmentItem> {
        return listOf(
            EquipmentItem(
                id = "eq_serv_glad_1",
                name = "Dövme Çelik Gladius",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.GLADIUS,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.HIGH_QUALITY_IRON,
                maxDurability = 110,
                currentDurability = 110,
                damage = 16,
                reachCm = 68,
                attackSpeed = 68,
                armorPenetrationPercent = 25,
                weightKg = 1.3f,
                balance = 82,
                reliability = 92,
                staminaCost = 5.5f,
                specialProperty = "İtme Saplaması: Kritik vuruş şansını %15 artırır",
                basePriceDenarii = 380,
                prestigeBonus = 15,
                commissionedSmith = "Servius Ferrarius"
            ),
            EquipmentItem(
                id = "eq_serv_spatha_1",
                name = "Campania Spatha Kılıcı",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.SPATHA,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.COMMON,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 95,
                currentDurability = 95,
                damage = 18,
                reachCm = 85,
                attackSpeed = 54,
                armorPenetrationPercent = 20,
                weightKg = 1.8f,
                balance = 70,
                reliability = 88,
                staminaCost = 7.0f,
                specialProperty = "Geniş Biçme: Ağır uzuv kesme hasarı",
                basePriceDenarii = 320,
                prestigeBonus = 10,
                commissionedSmith = "Servius Ferrarius"
            ),
            EquipmentItem(
                id = "eq_serv_spear_1",
                name = "Venabulum Av Mızrağı",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.SPEAR,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.HIGH_QUALITY_IRON,
                maxDurability = 105,
                currentDurability = 105,
                damage = 19,
                reachCm = 195,
                attackSpeed = 50,
                armorPenetrationPercent = 35,
                weightKg = 2.4f,
                balance = 78,
                reliability = 90,
                staminaCost = 7.8f,
                specialProperty = "Mesafe Hakimiyeti: Yaklaşan rakipleri durdurur",
                basePriceDenarii = 420,
                prestigeBonus = 18,
                commissionedSmith = "Servius Ferrarius"
            ),
            EquipmentItem(
                id = "eq_serv_rudis_1",
                name = "Ağır Meşe Rudis (Talim Kılıcı)",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.TRAINING_WEAPON,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.COMMON,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 150,
                currentDurability = 150,
                damage = 4,
                reachCm = 65,
                attackSpeed = 60,
                armorPenetrationPercent = 0,
                weightKg = 2.2f,
                balance = 60,
                reliability = 99,
                staminaCost = 9.0f,
                specialProperty = "İdman Ağırlığı: Günlük antrenman XP kazancını %25 artırır",
                basePriceDenarii = 90,
                prestigeBonus = 2,
                commissionedSmith = "Servius Ferrarius"
            )
        )
    }

    private fun generateMarcusInventory(): List<EquipmentItem> {
        return listOf(
            EquipmentItem(
                id = "eq_marc_scut_1",
                name = "Büyük Murmillo Scutum",
                category = EquipmentCategory.SHIELDS,
                type = EquipmentType.SCUTUM,
                slot = EquipmentSlot.SHIELD,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.HIGH_QUALITY_IRON,
                maxDurability = 130,
                currentDurability = 130,
                weightKg = 7.2f,
                protection = 28,
                coveragePercent = 75,
                mobilityPenalty = 6,
                staminaDrainPercent = 10,
                heatResistancePenalty = 8,
                blockEffectiveness = 85,
                bashEffectiveness = 22,
                basePriceDenarii = 440,
                prestigeBonus = 20,
                specialProperty = "Kalkan Duvarı: Gelen ok ve mızrakları tamamen sönümler"
            ),
            EquipmentItem(
                id = "eq_marc_lorica_1",
                name = "Lorica Hamata (Halka Zincir)",
                category = EquipmentCategory.ARMOR,
                type = EquipmentType.LORICA_HAMATA,
                slot = EquipmentSlot.BODY_ARMOR,
                quality = EquipmentQuality.COMMON,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 100,
                currentDurability = 100,
                weightKg = 9.5f,
                protection = 22,
                coveragePercent = 65,
                mobilityPenalty = 4,
                staminaDrainPercent = 8,
                heatResistancePenalty = 6,
                painResistanceBonus = 8,
                basePriceDenarii = 520,
                prestigeBonus = 15
            ),
            EquipmentItem(
                id = "eq_marc_manica_1",
                name = "Segmentli Demir Manica (Sağ Kol)",
                category = EquipmentCategory.ARMOR,
                type = EquipmentType.ARM_MANICA,
                slot = EquipmentSlot.RIGHT_ARM,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.HIGH_QUALITY_IRON,
                maxDurability = 110,
                currentDurability = 110,
                weightKg = 2.1f,
                protection = 14,
                coveragePercent = 35,
                mobilityPenalty = 1,
                staminaDrainPercent = 3,
                heatResistancePenalty = 2,
                painResistanceBonus = 6,
                basePriceDenarii = 260,
                prestigeBonus = 12
            ),
            EquipmentItem(
                id = "eq_marc_ocrea_1",
                name = "Çift Bronz Ocreae (Dizçekler)",
                category = EquipmentCategory.ARMOR,
                type = EquipmentType.GREAVES_OCREAE,
                slot = EquipmentSlot.LEGS,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.ORNATE_BRONZE,
                maxDurability = 120,
                currentDurability = 120,
                weightKg = 3.2f,
                protection = 16,
                coveragePercent = 40,
                mobilityPenalty = 2,
                staminaDrainPercent = 4,
                heatResistancePenalty = 3,
                painResistanceBonus = 8,
                basePriceDenarii = 310,
                prestigeBonus = 18
            )
        )
    }

    private fun generateHannoUsedInventory(): List<EquipmentItem> {
        return listOf(
            EquipmentItem(
                id = "eq_used_dama_glad",
                name = "Dama'nın Aşınmış Gladius'u",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.GLADIUS,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.FAMOUS,
                material = EquipmentMaterial.IBERIAN_STEEL,
                maxDurability = 120,
                currentDurability = 68,
                damage = 22,
                reachCm = 66,
                attackSpeed = 74,
                armorPenetrationPercent = 32,
                weightKg = 1.2f,
                balance = 90,
                reliability = 84,
                staminaCost = 5.2f,
                specialProperty = "Ölümcül Hatıra: Kalabalık tezahüratını %30 artırır",
                basePriceDenarii = 480,
                prestigeBonus = 65,
                isUsed = true,
                isNamedArtifact = true,
                provenance = EquipmentProvenance(
                    previousOwner = "Dama the Undefeated",
                    previousLudus = "Ludus Capuensis",
                    victoriesRecorded = 17,
                    fatalKills = 9,
                    battleLore = "Capua amfitiyatrosunda 17 kez kuma basıp yenilgi yüzü görmeyen Dama'nın kanla sulanmış kılıcı.",
                    bonusPrestige = 45
                )
            ),
            EquipmentItem(
                id = "eq_used_scut_cracked",
                name = "Savaş İzi Taşıyan Scutum",
                category = EquipmentCategory.SHIELDS,
                type = EquipmentType.SCUTUM,
                slot = EquipmentSlot.SHIELD,
                quality = EquipmentQuality.COMMON,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 100,
                currentDurability = 42,
                weightKg = 7.0f,
                protection = 20,
                coveragePercent = 70,
                mobilityPenalty = 6,
                staminaDrainPercent = 9,
                heatResistancePenalty = 7,
                blockEffectiveness = 65,
                bashEffectiveness = 16,
                basePriceDenarii = 150,
                prestigeBonus = 5,
                isUsed = true,
                provenance = EquipmentProvenance(
                    previousOwner = "Bilinmeyen Samnit Dövüşçüsü",
                    victoriesRecorded = 3,
                    fatalKills = 1,
                    battleLore = "Bir mızrak darbesiyle çatlamış, ancak demir perçinleri hala sağlam."
                )
            ),
            EquipmentItem(
                id = "eq_used_secutor_helm",
                name = "Ezilmiş Secutor Miğferi",
                category = EquipmentCategory.HELMETS,
                type = EquipmentType.SECUTOR_HELMET,
                slot = EquipmentSlot.HELMET,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.HIGH_QUALITY_IRON,
                maxDurability = 110,
                currentDurability = 55,
                protection = 24,
                coveragePercent = 85,
                mobilityPenalty = 3,
                staminaDrainPercent = 6,
                heatResistancePenalty = 9,
                visibilityPercent = 65,
                hearingPercent = 55,
                weightKg = 3.6f,
                basePriceDenarii = 220,
                prestigeBonus = 15,
                isUsed = true,
                specialProperty = "Pürüzsüz Kubbe: Retiarius ağının takılmasını tamamen engeller"
            ),
            EquipmentItem(
                id = "eq_used_sica_rusty",
                name = "Paslı Trak Sica Eğri Kılıcı",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.SICA,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.CRUDE,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 70,
                currentDurability = 31,
                damage = 13,
                reachCm = 58,
                attackSpeed = 70,
                armorPenetrationPercent = 20,
                weightKg = 1.1f,
                balance = 68,
                reliability = 65,
                staminaCost = 4.8f,
                specialProperty = "Kanca Ucu: Kalkan arkasından vurma şansı %20",
                basePriceDenarii = 85,
                prestigeBonus = 0,
                isUsed = true
            )
        )
    }

    private fun generateAureliusInventory(): List<EquipmentItem> {
        return listOf(
            EquipmentItem(
                id = "eq_aur_sica_master",
                name = "Trak Usta İşi Altın Sica",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.SICA,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.MASTERWORK,
                material = EquipmentMaterial.IBERIAN_STEEL,
                maxDurability = 140,
                currentDurability = 140,
                damage = 24,
                reachCm = 62,
                attackSpeed = 82,
                armorPenetrationPercent = 40,
                weightKg = 1.1f,
                balance = 94,
                reliability = 96,
                staminaCost = 5.0f,
                specialProperty = "Boyun Koparan: Kalkan bloğunu %35 yoksayar, ağır kanama başlatır",
                basePriceDenarii = 850,
                prestigeBonus = 55
            ),
            EquipmentItem(
                id = "eq_aur_falcata_1",
                name = "Hispania Falcata Palası",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.FALCATA,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.IBERIAN_STEEL,
                maxDurability = 125,
                currentDurability = 125,
                damage = 22,
                reachCm = 64,
                attackSpeed = 64,
                armorPenetrationPercent = 30,
                weightKg = 1.7f,
                balance = 76,
                reliability = 92,
                staminaCost = 6.8f,
                specialProperty = "Kafatası Parçalayan: Kalkan ve miğfer dayanıklılığını iki kat hızlı eritir",
                basePriceDenarii = 540,
                prestigeBonus = 25
            ),
            EquipmentItem(
                id = "eq_aur_trident_damascus",
                name = "Şam Çeliği Tridens (Üçlü Yaba)",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.TRIDENT,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.MASTERWORK,
                material = EquipmentMaterial.RARE_DAMASCUS_STEEL,
                maxDurability = 160,
                currentDurability = 160,
                damage = 26,
                reachCm = 175,
                attackSpeed = 66,
                armorPenetrationPercent = 45,
                weightKg = 2.0f,
                balance = 92,
                reliability = 98,
                staminaCost = 6.5f,
                specialProperty = "Kılıç Kilidi: Rakibi silahsız bırakma ve devirme ihtimali %25",
                basePriceDenarii = 1200,
                prestigeBonus = 80
            ),
            EquipmentItem(
                id = "eq_aur_galerus_bronze",
                name = "İşlemeli Korint Galerus (Sol Omuzluk)",
                category = EquipmentCategory.ARMOR,
                type = EquipmentType.SHOULDER_GALERUS,
                slot = EquipmentSlot.LEFT_ARM,
                quality = EquipmentQuality.MASTERWORK,
                material = EquipmentMaterial.ORNATE_BRONZE,
                maxDurability = 135,
                currentDurability = 135,
                weightKg = 2.8f,
                protection = 20,
                coveragePercent = 30,
                mobilityPenalty = 1,
                staminaDrainPercent = 2,
                heatResistancePenalty = 2,
                painResistanceBonus = 10,
                basePriceDenarii = 620,
                prestigeBonus = 45,
                specialProperty = "Boyun Siperi: Boyna gelen öldürücü saplamaları %50 bloke eder"
            )
        )
    }

    private fun generateDecimusInventory(): List<EquipmentItem> {
        return listOf(
            EquipmentItem(
                id = "eq_dec_lorica_seg",
                name = "Lejyon Segmentata Plaka Zırhı",
                category = EquipmentCategory.ARMOR,
                type = EquipmentType.LORICA_SEGMENTATA,
                slot = EquipmentSlot.BODY_ARMOR,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.HIGH_QUALITY_IRON,
                maxDurability = 140,
                currentDurability = 140,
                weightKg = 11.5f,
                protection = 32,
                coveragePercent = 80,
                mobilityPenalty = 7,
                staminaDrainPercent = 12,
                heatResistancePenalty = 10,
                painResistanceBonus = 14,
                basePriceDenarii = 680,
                prestigeBonus = 30,
                specialProperty = "Demir Zırh: Ezici ve kesici gövde hasarını yarıya indirir"
            ),
            EquipmentItem(
                id = "eq_dec_caligae",
                name = "Ağır Çivili Lejyon Caligae'si",
                category = EquipmentCategory.ACCESSORIES,
                type = EquipmentType.CALIGAE_SANDALS,
                slot = EquipmentSlot.ACCESSORY,
                quality = EquipmentQuality.FINE,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 120,
                currentDurability = 120,
                weightKg = 1.4f,
                protection = 6,
                coveragePercent = 20,
                mobilityPenalty = 0,
                staminaDrainPercent = 1,
                heatResistancePenalty = 0,
                painResistanceBonus = 4,
                basePriceDenarii = 140,
                prestigeBonus = 10,
                specialProperty = "Kum Tutuşu: Kaygan kanlı zeminde tökezlemeyi ve düşmeyi önler"
            ),
            EquipmentItem(
                id = "eq_dec_helm_legion",
                name = "Açık Yüz Askeri Miğfer (Galea)",
                category = EquipmentCategory.HELMETS,
                type = EquipmentType.OPEN_FACE_HELMET,
                slot = EquipmentSlot.HELMET,
                quality = EquipmentQuality.COMMON,
                material = EquipmentMaterial.STANDARD_IRON,
                maxDurability = 90,
                currentDurability = 90,
                weightKg = 2.0f,
                protection = 16,
                coveragePercent = 60,
                mobilityPenalty = 1,
                staminaDrainPercent = 2,
                heatResistancePenalty = 3,
                visibilityPercent = 95,
                hearingPercent = 95,
                basePriceDenarii = 220,
                prestigeBonus = 10,
                specialProperty = "Geniş Görüş: Lanista taktik emirlerine itaat oranı %95"
            ),
            EquipmentItem(
                id = "eq_dec_balteus",
                name = "Bronz Perçinli Balteus Kemeri",
                category = EquipmentCategory.ACCESSORIES,
                type = EquipmentType.BALTEUS_BELT,
                slot = EquipmentSlot.ACCESSORY,
                quality = EquipmentQuality.COMMON,
                material = EquipmentMaterial.ORNATE_BRONZE,
                maxDurability = 100,
                currentDurability = 100,
                weightKg = 1.2f,
                protection = 5,
                coveragePercent = 15,
                mobilityPenalty = 0,
                staminaDrainPercent = 1,
                heatResistancePenalty = 1,
                painResistanceBonus = 5,
                basePriceDenarii = 110,
                prestigeBonus = 8
            )
        )
    }

    private fun generateGaiusLuxuryInventory(): List<EquipmentItem> {
        return listOf(
            EquipmentItem(
                id = "eq_gaius_gold_murmillo",
                name = "Capua Şampiyonu Altın Miğferi",
                category = EquipmentCategory.HELMETS,
                type = EquipmentType.MURMILLO_HELMET,
                slot = EquipmentSlot.HELMET,
                quality = EquipmentQuality.NAMED,
                material = EquipmentMaterial.GILDED_STEEL,
                maxDurability = 170,
                currentDurability = 170,
                weightKg = 4.2f,
                protection = 34,
                coveragePercent = 90,
                mobilityPenalty = 4,
                staminaDrainPercent = 7,
                heatResistancePenalty = 8,
                visibilityPercent = 75,
                hearingPercent = 65,
                basePriceDenarii = 2100,
                prestigeBonus = 150,
                isNamedArtifact = true,
                specialProperty = "İmparatorluk Görkemi: Seyirci coşkusunu ve bahis getirisini %40 artırır",
                provenance = EquipmentProvenance(
                    previousOwner = "Marcus Valerius Maximus",
                    previousLudus = "Domus Augusta",
                    victoriesRecorded = 28,
                    fatalKills = 14,
                    battleLore = "İmparatorun şahsen Capua zaferi sonrası hediye ettiği altın yaldızlı şampiyon tacı."
                )
            ),
            EquipmentItem(
                id = "eq_gaius_twin_swords",
                name = "Damascus Dimachaerus Çift Bıçakları",
                category = EquipmentCategory.WEAPONS,
                type = EquipmentType.TWIN_SWORDS,
                slot = EquipmentSlot.MAIN_HAND,
                quality = EquipmentQuality.MASTERWORK,
                material = EquipmentMaterial.RARE_DAMASCUS_STEEL,
                maxDurability = 150,
                currentDurability = 150,
                damage = 27,
                reachCm = 60,
                attackSpeed = 95,
                armorPenetrationPercent = 35,
                weightKg = 1.9f,
                balance = 98,
                reliability = 95,
                staminaCost = 8.5f,
                specialProperty = "Kasırga Darbesi: Savunmasız rakiplere çift darbe indirir",
                basePriceDenarii = 1650,
                prestigeBonus = 90
            ),
            EquipmentItem(
                id = "eq_gaius_thraex_crest",
                name = "Grifon Kabartmalı Trak Miğferi",
                category = EquipmentCategory.HELMETS,
                type = EquipmentType.THRAEX_HELMET,
                slot = EquipmentSlot.HELMET,
                quality = EquipmentQuality.FAMOUS,
                material = EquipmentMaterial.ORNATE_BRONZE,
                maxDurability = 145,
                currentDurability = 145,
                weightKg = 3.8f,
                protection = 28,
                coveragePercent = 88,
                mobilityPenalty = 3,
                staminaDrainPercent = 6,
                heatResistancePenalty = 7,
                visibilityPercent = 70,
                hearingPercent = 65,
                basePriceDenarii = 1350,
                prestigeBonus = 75,
                specialProperty = "Grifon Heybeti: Karşı dövüşçünün cesaretini kırarak panikletir"
            )
        )
    }

    // -------------------------------------------------------------
    // AUCTIONS & UNIQUE NAMED ARTIFACTS
    // -------------------------------------------------------------
    fun createInitialAuctions(): List<EquipmentAuction> {
        val bullGladius = EquipmentItem(
            id = "eq_auc_bull_gladius",
            name = "Boğa'nın Efsanevi Gladius'u (Gladius Tauri)",
            category = EquipmentCategory.WEAPONS,
            type = EquipmentType.GLADIUS,
            slot = EquipmentSlot.MAIN_HAND,
            quality = EquipmentQuality.NAMED,
            material = EquipmentMaterial.IBERIAN_STEEL,
            maxDurability = 160,
            currentDurability = 135,
            damage = 28,
            reachCm = 70,
            attackSpeed = 78,
            armorPenetrationPercent = 45,
            weightKg = 1.35f,
            balance = 95,
            reliability = 98,
            staminaCost = 5.4f,
            specialProperty = "Boğa Boynuzu: Zırhı delip geçer, darbe anında rakibi %30 sendeletir",
            basePriceDenarii = 950,
            prestigeBonus = 120,
            isUsed = true,
            isNamedArtifact = true,
            provenance = EquipmentProvenance(
                previousOwner = "Taurus 'The Minotaur' of Pompeii",
                previousLudus = "Ludus Vatia",
                victoriesRecorded = 23,
                fatalKills = 18,
                battleLore = "Pompeii arenasında kurbanlarının göğüs kafesini parçalayan, İber çeliğinden dökülme efsanevi silah.",
                bonusPrestige = 100
            )
        )

        val seaKingTrident = EquipmentItem(
            id = "eq_auc_sea_king",
            name = "Deniz Kralı Glaucus'un Yabası (Tridens Glauci)",
            category = EquipmentCategory.WEAPONS,
            type = EquipmentType.TRIDENT,
            slot = EquipmentSlot.MAIN_HAND,
            quality = EquipmentQuality.NAMED,
            material = EquipmentMaterial.RARE_DAMASCUS_STEEL,
            maxDurability = 175,
            currentDurability = 140,
            damage = 30,
            reachCm = 185,
            attackSpeed = 72,
            armorPenetrationPercent = 50,
            weightKg = 2.1f,
            balance = 96,
            reliability = 99,
            staminaCost = 6.2f,
            specialProperty = "Neptün'ün Gazabı: Rakibin silahını kilitleyip devirir, %40 kanama",
            basePriceDenarii = 1400,
            prestigeBonus = 140,
            isUsed = true,
            isNamedArtifact = true,
            provenance = EquipmentProvenance(
                previousOwner = "Glaucus the Manticore",
                previousLudus = "Ludus Puteolanus",
                victoriesRecorded = 31,
                fatalKills = 22,
                battleLore = "Puteoli sularında ve amfitiyatrosunda 31 kurban alan yenilmez Retiarius Glaucus'un yadigar yabası.",
                bonusPrestige = 120
            )
        )

        return listOf(
            EquipmentAuction(
                id = "auc_1",
                item = bullGladius,
                startingBid = 1200,
                currentBid = 1500,
                highBidder = "Domus Auctor (Rival Lanista)",
                isPlayerHighBidder = false,
                daysRemaining = 3,
                bidders = mutableListOf("Domus Auctor", "Senatör Lucius Cassius", "Ludus Batiatus"),
                historicalProvenance = "Taurus 'The Minotaur' Pompeii arenasında 23 galibiyet ve 18 infaz elde etti. Kılıç İberya madenlerinden özel siparişle getirtilmiştir.",
                minBidIncrement = 150
            ),
            EquipmentAuction(
                id = "auc_2",
                item = seaKingTrident,
                startingBid = 1800,
                currentBid = 2100,
                highBidder = "Patrician Quintus Flavius",
                isPlayerHighBidder = false,
                daysRemaining = 5,
                bidders = mutableListOf("Patrician Quintus Flavius", "Ludus Vatia", "Tüccar Aurelius"),
                historicalProvenance = "31 dövüş kazanan Glaucus'un efsanevi Şam çeliği üçlü yabası. Müzayede gelirinin bir kısmı Capua Jüpiter Tapınağına bağışlanacaktır.",
                minBidIncrement = 200
            )
        )
    }

    // -------------------------------------------------------------
    // MARKET DYNAMIC EVENTS & PRICE TRENDS
    // -------------------------------------------------------------
    fun createInitialMarketEvents(): List<EquipmentMarketEvent> {
        return listOf(
            EquipmentMarketEvent(
                id = "mkt_ev_iron_shortage",
                title = "İtalya Demir Cevheri Kıtlığı",
                description = "Kuzey Etruria demir madenlerindeki çöküntü ve grev sebebiyle ham demir fiyatları fırladı. Standart kılıç ve zırh maliyetleri %30 arttı.",
                categoryAffected = EquipmentCategory.WEAPONS,
                priceMultiplier = 1.30f,
                scarcityNote = "Demir Silah Fiyatları +%30",
                daysRemaining = 6,
                isPositive = false
            ),
            EquipmentMarketEvent(
                id = "mkt_ev_iberian_shipment",
                title = "Puteoli'ye İber Çeliği Yüklü Kadırga Yanaştı",
                description = "Hispania'dan gelen tüccar filosu yüksek kaliteli Toledo kılıçlarını ve çelik plakaları Capua pazarına indirdi.",
                categoryAffected = EquipmentCategory.ARMOR,
                priceMultiplier = 0.85f,
                scarcityNote = "İber Çeliği Zırhlarda %15 İndirim & Bol Stok",
                daysRemaining = 4,
                isPositive = true
            )
        )
    }

    fun createPriceTrends(): List<PriceTrend> {
        return listOf(
            PriceTrend(EquipmentCategory.WEAPONS, "Silah Piyasası", 18, "Festival öncesi gladyatör kılıçlarına yoğun talep.", true),
            PriceTrend(EquipmentCategory.ARMOR, "Zırh Piyasası", -8, "Hispania sevkiyatı sebebiyle plaka zırhlar ucuzladı.", false),
            PriceTrend(EquipmentCategory.SHIELDS, "Kalkanlar", 5, "Scutum ahşabı ve deri kaplama tedariki stabil.", true),
            PriceTrend(EquipmentCategory.HELMETS, "Miğferler", 12, "Lejyon alımları sebebiyle dökme miğferler kıymete bindi.", true),
            PriceTrend(EquipmentCategory.ACCESSORIES, "Aksesuarlar", 0, "Caligae ve Balteus kemer fiyatları dengeli.", false)
        )
    }

    // -------------------------------------------------------------
    // INITIAL GLADIATOR LOADOUTS FACTORY
    // -------------------------------------------------------------
    fun createDefaultLoadoutForGladiator(gladiator: Gladiator): GladiatorLoadout {
        return when (gladiator.gladiatorClass) {
            GladiatorClass.MURMILLO -> GladiatorLoadout(
                gladiatorId = gladiator.id,
                helmet = EquipmentItem(
                    id = "glad_${gladiator.id}_helm",
                    name = "Standart Murmillo Galea",
                    category = EquipmentCategory.HELMETS,
                    type = EquipmentType.MURMILLO_HELMET,
                    slot = EquipmentSlot.HELMET,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 100,
                    currentDurability = 92,
                    protection = 25,
                    coveragePercent = 85,
                    mobilityPenalty = 4,
                    staminaDrainPercent = 6,
                    heatResistancePenalty = 7,
                    visibilityPercent = 75,
                    hearingPercent = 65,
                    weightKg = 3.8f,
                    basePriceDenarii = 280,
                    specialProperty = "Balık Tepeliği: Baş darbelerini kaydırır"
                ),
                bodyArmor = EquipmentItem(
                    id = "glad_${gladiator.id}_body",
                    name = "Dolgulu Göğüs Kuşağı & Subarmalis",
                    category = EquipmentCategory.ARMOR,
                    type = EquipmentType.PADDED_SUBARMALIS,
                    slot = EquipmentSlot.BODY_ARMOR,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 90,
                    currentDurability = 85,
                    protection = 12,
                    coveragePercent = 45,
                    mobilityPenalty = 1,
                    staminaDrainPercent = 3,
                    heatResistancePenalty = 4,
                    painResistanceBonus = 6,
                    weightKg = 3.5f,
                    basePriceDenarii = 180
                ),
                rightArm = EquipmentItem(
                    id = "glad_${gladiator.id}_rarm",
                    name = "Mafsallı Deri Manica",
                    category = EquipmentCategory.ARMOR,
                    type = EquipmentType.ARM_MANICA,
                    slot = EquipmentSlot.RIGHT_ARM,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 85,
                    currentDurability = 80,
                    protection = 10,
                    coveragePercent = 30,
                    mobilityPenalty = 1,
                    staminaDrainPercent = 2,
                    heatResistancePenalty = 2,
                    weightKg = 1.6f,
                    basePriceDenarii = 140
                ),
                mainHand = EquipmentItem(
                    id = "glad_${gladiator.id}_wpn",
                    name = "Campania Lejyon Gladius'u",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.GLADIUS,
                    slot = EquipmentSlot.MAIN_HAND,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 100,
                    currentDurability = 88,
                    damage = 14,
                    reachCm = 65,
                    attackSpeed = 62,
                    armorPenetrationPercent = 20,
                    weightKg = 1.3f,
                    balance = 78,
                    reliability = 90,
                    staminaCost = 5.8f,
                    basePriceDenarii = 260,
                    specialProperty = "İtme Saplaması: Yakın mesafede ölümcül"
                ),
                shield = EquipmentItem(
                    id = "glad_${gladiator.id}_shld",
                    name = "Murmillo Scutum Kalkanı",
                    category = EquipmentCategory.SHIELDS,
                    type = EquipmentType.SCUTUM,
                    slot = EquipmentSlot.SHIELD,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 110,
                    currentDurability = 94,
                    weightKg = 6.8f,
                    protection = 26,
                    coveragePercent = 75,
                    mobilityPenalty = 5,
                    staminaDrainPercent = 9,
                    heatResistancePenalty = 7,
                    blockEffectiveness = 80,
                    bashEffectiveness = 20,
                    basePriceDenarii = 320,
                    specialProperty = "Kalkan Duvarı: Gelen darbeleri büyük oranda emer"
                ),
                legs = EquipmentItem(
                    id = "glad_${gladiator.id}_legs",
                    name = "Sol Bacak Ocrea Dizçeği",
                    category = EquipmentCategory.ARMOR,
                    type = EquipmentType.GREAVES_OCREAE,
                    slot = EquipmentSlot.LEGS,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.ORNATE_BRONZE,
                    maxDurability = 95,
                    currentDurability = 88,
                    weightKg = 2.4f,
                    protection = 12,
                    coveragePercent = 35,
                    mobilityPenalty = 1,
                    staminaDrainPercent = 2,
                    heatResistancePenalty = 2,
                    basePriceDenarii = 180
                ),
                accessory = EquipmentItem(
                    id = "glad_${gladiator.id}_acc",
                    name = "Çivili Dövüş Caligae'si",
                    category = EquipmentCategory.ACCESSORIES,
                    type = EquipmentType.CALIGAE_SANDALS,
                    slot = EquipmentSlot.ACCESSORY,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 100,
                    currentDurability = 90,
                    weightKg = 1.2f,
                    protection = 4,
                    coveragePercent = 15,
                    mobilityPenalty = 0,
                    staminaDrainPercent = 1,
                    heatResistancePenalty = 0,
                    basePriceDenarii = 95,
                    specialProperty = "Kum Tutuşu: Kaymayı önler"
                )
            )
            GladiatorClass.THRAEX -> GladiatorLoadout(
                gladiatorId = gladiator.id,
                helmet = EquipmentItem(
                    id = "glad_${gladiator.id}_helm",
                    name = "Grifon Tepelikli Thraex Miğferi",
                    category = EquipmentCategory.HELMETS,
                    type = EquipmentType.THRAEX_HELMET,
                    slot = EquipmentSlot.HELMET,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.ORNATE_BRONZE,
                    maxDurability = 120,
                    currentDurability = 110,
                    protection = 24,
                    coveragePercent = 85,
                    mobilityPenalty = 3,
                    staminaDrainPercent = 5,
                    heatResistancePenalty = 6,
                    visibilityPercent = 70,
                    hearingPercent = 65,
                    weightKg = 3.5f,
                    basePriceDenarii = 380,
                    specialProperty = "Korkutucu Grifon: Rakip moralini sarsar"
                ),
                mainHand = EquipmentItem(
                    id = "glad_${gladiator.id}_wpn",
                    name = "Keskin Trak Sica'sı",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.SICA,
                    slot = EquipmentSlot.MAIN_HAND,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.HIGH_QUALITY_IRON,
                    maxDurability = 110,
                    currentDurability = 102,
                    damage = 17,
                    reachCm = 60,
                    attackSpeed = 76,
                    armorPenetrationPercent = 30,
                    weightKg = 1.15f,
                    balance = 86,
                    reliability = 92,
                    staminaCost = 5.0f,
                    basePriceDenarii = 340,
                    specialProperty = "Kavisli Kesik: Kalkan arkasındaki boyun ve sırtı hedefler"
                ),
                shield = EquipmentItem(
                    id = "glad_${gladiator.id}_shld",
                    name = "Thraex Parma Kalkanı",
                    category = EquipmentCategory.SHIELDS,
                    type = EquipmentType.THRAEX_SHIELD,
                    slot = EquipmentSlot.SHIELD,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 95,
                    currentDurability = 89,
                    weightKg = 3.8f,
                    protection = 18,
                    coveragePercent = 50,
                    mobilityPenalty = 2,
                    staminaDrainPercent = 4,
                    heatResistancePenalty = 4,
                    blockEffectiveness = 68,
                    bashEffectiveness = 14,
                    basePriceDenarii = 220,
                    specialProperty = "Çevik Siper: Hızlı karşı ataklara izin verir"
                ),
                legs = EquipmentItem(
                    id = "glad_${gladiator.id}_legs",
                    name = "Yüksek Çift Thraex Ocreae",
                    category = EquipmentCategory.ARMOR,
                    type = EquipmentType.GREAVES_OCREAE,
                    slot = EquipmentSlot.LEGS,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.ORNATE_BRONZE,
                    maxDurability = 120,
                    currentDurability = 115,
                    weightKg = 4.0f,
                    protection = 20,
                    coveragePercent = 55,
                    mobilityPenalty = 3,
                    staminaDrainPercent = 5,
                    heatResistancePenalty = 4,
                    basePriceDenarii = 310,
                    specialProperty = "Yüksek Baldırlık: Uyluk ve dizleri korur"
                )
            )
            GladiatorClass.RETIARIUS -> GladiatorLoadout(
                gladiatorId = gladiator.id,
                leftArm = EquipmentItem(
                    id = "glad_${gladiator.id}_larm",
                    name = "Tunç Galerus Omuzluğu",
                    category = EquipmentCategory.ARMOR,
                    type = EquipmentType.SHOULDER_GALERUS,
                    slot = EquipmentSlot.LEFT_ARM,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.ORNATE_BRONZE,
                    maxDurability = 115,
                    currentDurability = 110,
                    weightKg = 2.5f,
                    protection = 18,
                    coveragePercent = 28,
                    mobilityPenalty = 1,
                    staminaDrainPercent = 2,
                    heatResistancePenalty = 2,
                    basePriceDenarii = 320,
                    specialProperty = "Boyun Kalkanı: Sol tarafı siper gibi korur"
                ),
                mainHand = EquipmentItem(
                    id = "glad_${gladiator.id}_wpn",
                    name = "Retiarius Tridens (Yaba)",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.TRIDENT,
                    slot = EquipmentSlot.MAIN_HAND,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.HIGH_QUALITY_IRON,
                    maxDurability = 120,
                    currentDurability = 112,
                    damage = 21,
                    reachCm = 180,
                    attackSpeed = 62,
                    armorPenetrationPercent = 35,
                    weightKg = 2.2f,
                    balance = 84,
                    reliability = 94,
                    staminaCost = 6.8f,
                    basePriceDenarii = 410,
                    specialProperty = "Deşici Çatallar: Rakibin kılıcını kilitleyebilir"
                ),
                offHand = EquipmentItem(
                    id = "glad_${gladiator.id}_off",
                    name = "Çelik Pugio Hançeri",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.DAGGER,
                    slot = EquipmentSlot.OFF_HAND,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.HIGH_QUALITY_IRON,
                    maxDurability = 90,
                    currentDurability = 85,
                    damage = 11,
                    reachCm = 32,
                    attackSpeed = 88,
                    armorPenetrationPercent = 25,
                    weightKg = 0.6f,
                    balance = 92,
                    reliability = 96,
                    staminaCost = 3.2f,
                    basePriceDenarii = 160,
                    specialProperty = "Yakın Saplama: Sarılma anında ölümcül"
                ),
                accessory = EquipmentItem(
                    id = "glad_${gladiator.id}_acc",
                    name = "Hafif Fasciae Bacak Sarıkları",
                    category = EquipmentCategory.ACCESSORIES,
                    type = EquipmentType.FASCIAE_STRAPS,
                    slot = EquipmentSlot.ACCESSORY,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 80,
                    currentDurability = 75,
                    weightKg = 0.8f,
                    protection = 3,
                    coveragePercent = 15,
                    mobilityPenalty = -2, // Bonus mobility!
                    staminaDrainPercent = -2,
                    heatResistancePenalty = 0,
                    basePriceDenarii = 70,
                    specialProperty = "Çevik Adımlar: Hareket hızını artırır"
                )
            )
            GladiatorClass.DIMACHAERUS -> GladiatorLoadout(
                gladiatorId = gladiator.id,
                mainHand = EquipmentItem(
                    id = "glad_${gladiator.id}_wpn1",
                    name = "Sağ El Gladius'u",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.GLADIUS,
                    slot = EquipmentSlot.MAIN_HAND,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.HIGH_QUALITY_IRON,
                    maxDurability = 105,
                    currentDurability = 98,
                    damage = 15,
                    reachCm = 64,
                    attackSpeed = 75,
                    armorPenetrationPercent = 22,
                    weightKg = 1.2f,
                    balance = 85,
                    reliability = 92,
                    staminaCost = 5.2f,
                    basePriceDenarii = 280
                ),
                offHand = EquipmentItem(
                    id = "glad_${gladiator.id}_wpn2",
                    name = "Sol El Savunma Sica'sı",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.SICA,
                    slot = EquipmentSlot.OFF_HAND,
                    quality = EquipmentQuality.FINE,
                    material = EquipmentMaterial.HIGH_QUALITY_IRON,
                    maxDurability = 100,
                    currentDurability = 95,
                    damage = 14,
                    reachCm = 58,
                    attackSpeed = 80,
                    armorPenetrationPercent = 25,
                    weightKg = 1.1f,
                    balance = 88,
                    reliability = 90,
                    staminaCost = 4.8f,
                    basePriceDenarii = 290,
                    specialProperty = "Çift Bıçak Parresi: Kalkan olmadan darbeleri saptırır"
                ),
                bodyArmor = EquipmentItem(
                    id = "glad_${gladiator.id}_body",
                    name = "Hafif Sert Deri Cuirass",
                    category = EquipmentCategory.ARMOR,
                    type = EquipmentType.LEATHER_ARMOR,
                    slot = EquipmentSlot.BODY_ARMOR,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 90,
                    currentDurability = 85,
                    weightKg = 4.2f,
                    protection = 14,
                    coveragePercent = 50,
                    mobilityPenalty = 1,
                    staminaDrainPercent = 2,
                    heatResistancePenalty = 3,
                    basePriceDenarii = 210
                )
            )
            else -> GladiatorLoadout(
                gladiatorId = gladiator.id,
                mainHand = EquipmentItem(
                    id = "glad_${gladiator.id}_wpn",
                    name = "Standart Gladyatör Kılıcı",
                    category = EquipmentCategory.WEAPONS,
                    type = EquipmentType.GLADIUS,
                    slot = EquipmentSlot.MAIN_HAND,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 90,
                    currentDurability = 85,
                    damage = 13,
                    reachCm = 65,
                    attackSpeed = 60,
                    armorPenetrationPercent = 18,
                    weightKg = 1.3f,
                    balance = 75,
                    reliability = 88,
                    staminaCost = 5.5f,
                    basePriceDenarii = 200
                ),
                shield = EquipmentItem(
                    id = "glad_${gladiator.id}_shld",
                    name = "Parma Yuvarlak Kalkan",
                    category = EquipmentCategory.SHIELDS,
                    type = EquipmentType.PARMA,
                    slot = EquipmentSlot.SHIELD,
                    quality = EquipmentQuality.COMMON,
                    material = EquipmentMaterial.STANDARD_IRON,
                    maxDurability = 90,
                    currentDurability = 82,
                    weightKg = 4.2f,
                    protection = 18,
                    coveragePercent = 55,
                    mobilityPenalty = 2,
                    staminaDrainPercent = 4,
                    heatResistancePenalty = 4,
                    blockEffectiveness = 70,
                    bashEffectiveness = 14,
                    basePriceDenarii = 210
                )
            )
        }
    }

    // -------------------------------------------------------------
    // GLADIATOR PREFERENCES FACTORY
    // -------------------------------------------------------------
    fun createPreferenceForGladiator(gladiator: Gladiator): GladiatorEquipmentPreference {
        return when (gladiator.gladiatorClass) {
            GladiatorClass.MURMILLO -> GladiatorEquipmentPreference(
                gladiatorId = gladiator.id,
                favoredWeaponType = EquipmentType.GLADIUS,
                hatesHeavyArmor = false,
                desiresHeavyProtection = true,
                attachedItemName = "Campania Lejyon Gladius'u",
                complaintText = if (gladiator.physicalStats.endurance < 12) "Ağır zırh beni boğuyor lanista!" else null
            )
            GladiatorClass.THRAEX -> GladiatorEquipmentPreference(
                gladiatorId = gladiator.id,
                favoredWeaponType = EquipmentType.SICA,
                hatesHeavyArmor = true,
                desiresHeavyProtection = false,
                attachedItemName = "Keskin Trak Sica'sı",
                complaintText = "Düz kılıçla dövüşemem, kavisli Sica'mı verin!"
            )
            GladiatorClass.RETIARIUS -> GladiatorEquipmentPreference(
                gladiatorId = gladiator.id,
                favoredWeaponType = EquipmentType.TRIDENT,
                hatesHeavyArmor = true,
                desiresHeavyProtection = false,
                attachedItemName = "Retiarius Tridens",
                complaintText = "Miğfer takarsam nefes alamam, görüşümü kapatmayın!"
            )
            GladiatorClass.DIMACHAERUS -> GladiatorEquipmentPreference(
                gladiatorId = gladiator.id,
                favoredWeaponType = EquipmentType.TWIN_SWORDS,
                hatesHeavyArmor = true,
                desiresHeavyProtection = false,
                complaintText = "Kalkan taşıyan korkaktır, bana iki keskin çelik verin!"
            )
            GladiatorClass.HOPLOMACHUS, GladiatorClass.BESTIARIUS -> GladiatorEquipmentPreference(
                gladiatorId = gladiator.id,
                favoredWeaponType = EquipmentType.SPEAR,
                hatesHeavyArmor = false,
                desiresHeavyProtection = false
            )
            else -> GladiatorEquipmentPreference(
                gladiatorId = gladiator.id,
                favoredWeaponType = EquipmentType.GLADIUS,
                hatesHeavyArmor = false,
                desiresHeavyProtection = false
            )
        }
    }

    // -------------------------------------------------------------
    // DYNAMIC PRICING ENGINE
    // -------------------------------------------------------------
    fun calculateDynamicPrice(
        item: EquipmentItem,
        merchant: RomanMerchant,
        activeEvents: List<EquipmentMarketEvent>,
        politicalFactions: List<PoliticalFaction>
    ): Int {
        var price = item.currentMarketValue.toFloat() * merchant.priceMultiplier

        // Merchant relationship discount (-20% to +25%)
        val relMod = (merchant.playerRelationship * 0.002f).coerceIn(-0.20f, 0.25f)
        price *= (1f - relMod)

        // Category-specific active market events
        activeEvents.forEach { ev ->
            if (ev.categoryAffected == null || ev.categoryAffected == item.category) {
                price *= ev.priceMultiplier
            }
        }

        // Political faction standings
        val merchantGuild = politicalFactions.find { it.id == PoliticalFactionId.MERCHANT_GUILD }
        if (merchantGuild != null && merchantGuild.opinionOfPlayer >= 50) {
            price *= 0.90f // 10% Guild wholesale discount
        }

        val militaryFaction = politicalFactions.find { it.id == PoliticalFactionId.MILITARY }
        if (militaryFaction != null && militaryFaction.opinionOfPlayer >= 40 && merchant.id == "merch_decimus") {
            price *= 0.85f // 15% Legion procurement discount
        }

        return price.roundToInt().coerceAtLeast(12)
    }

    fun calculateDynamicSalePrice(
        item: EquipmentItem,
        merchant: RomanMerchant,
        activeEvents: List<EquipmentMarketEvent>,
        politicalFactions: List<PoliticalFaction>
    ): Int {
        val baseRatio = if (merchant.specialtyCategory == item.category || merchant.specialtyCategory == null) 0.55f else 0.40f
        val relBonus = (merchant.playerRelationship * 0.0015f).coerceIn(-0.08f, 0.15f)
        var salePrice = item.currentMarketValue * (baseRatio + relBonus)

        activeEvents.forEach { ev ->
            if (ev.categoryAffected == null || ev.categoryAffected == item.category) {
                salePrice *= (ev.priceMultiplier * 0.85f)
            }
        }

        return salePrice.roundToInt().coerceAtLeast(5)
    }

    // -------------------------------------------------------------
    // RESTOCKING & DYNAMIC ROTATION
    // -------------------------------------------------------------
    fun restockMerchantInventories(
        currentMerchants: List<RomanMerchant>,
        dayNumber: Int,
        dominusPrestige: Int
    ): List<RomanMerchant> {
        return currentMerchants.map { merchant ->
            val playerSoldItems = merchant.inventory.filter { it.isUsed && it.sellerMerchantId == "player" }
            val refreshedItems = when (merchant.id) {
                "merch_servius" -> generateServiusInventory().shuffled().take(4 + (dayNumber % 3))
                "merch_marcus" -> generateMarcusInventory().shuffled().take(4 + (dayNumber % 3))
                "merch_hanno" -> generateHannoUsedInventory().shuffled().take(5 + (dayNumber % 3))
                "merch_aurelius" -> generateAureliusInventory().shuffled().take(3 + (dayNumber % 3))
                "merch_decimus" -> generateDecimusInventory().shuffled().take(4 + (dayNumber % 2))
                "merch_gaius" -> generateGaiusLuxuryInventory().shuffled().take(2 + (dayNumber % 2))
                else -> merchant.inventory
            }

            // Combine fresh items with any items player sold to the merchant
            val combined = (refreshedItems + playerSoldItems).distinctBy { it.id }.toMutableList()
            merchant.copy(inventory = combined)
        }
    }

    // -------------------------------------------------------------
    // OPPONENT LOADOUT FACTORY (Scouting & Matchmaking)
    // -------------------------------------------------------------
    fun createLoadoutForOpponent(
        opponent: Gladiator,
        tier: OpponentTier = OpponentTier.COMMON,
        venue: ArenaVenueId = ArenaVenueId.CAPUA
    ): GladiatorLoadout {
        val (quality, material) = when (tier) {
            OpponentTier.COMMON -> Pair(EquipmentQuality.COMMON, EquipmentMaterial.STANDARD_IRON)
            OpponentTier.UNCOMMON -> Pair(EquipmentQuality.FINE, EquipmentMaterial.STANDARD_IRON)
            OpponentTier.ELITE -> Pair(EquipmentQuality.FINE, EquipmentMaterial.HIGH_QUALITY_IRON)
            OpponentTier.CHAMPION -> Pair(EquipmentQuality.MASTERWORK, EquipmentMaterial.IBERIAN_STEEL)
            OpponentTier.LEGEND -> Pair(EquipmentQuality.FAMOUS, EquipmentMaterial.RARE_DAMASCUS_STEEL)
        }

        val baseLoadout = createDefaultLoadoutForGladiator(opponent)
        return GladiatorLoadout(
            gladiatorId = opponent.id,
            helmet = baseLoadout.helmet?.copy(quality = quality, material = material),
            bodyArmor = baseLoadout.bodyArmor?.copy(quality = quality, material = material),
            leftArm = baseLoadout.leftArm?.copy(quality = quality, material = material),
            rightArm = baseLoadout.rightArm?.copy(quality = quality, material = material),
            mainHand = baseLoadout.mainHand?.copy(quality = quality, material = material),
            offHand = baseLoadout.offHand?.copy(quality = quality, material = material),
            shield = baseLoadout.shield?.copy(quality = quality, material = material),
            legs = baseLoadout.legs?.copy(quality = quality, material = material),
            accessory = baseLoadout.accessory?.copy(quality = quality, material = material)
        )
    }

    // -------------------------------------------------------------
    // AUCTION SIMULATION & BIDDING PROGRESSION
    // -------------------------------------------------------------
    fun advanceAuctions(
        auctions: List<EquipmentAuction>,
        dayNumber: Int,
        playerDenarii: Int
    ): Pair<List<EquipmentAuction>, List<EquipmentAuctionResolution>> {
        val activeAuctions = mutableListOf<EquipmentAuction>()
        val resolutions = mutableListOf<EquipmentAuctionResolution>()

        val rivalLanistas = listOf("Domus Auctor", "Ludus Vatia", "Patrician Quintus Flavius", "Senatör Lucius Cassius", "Ludus Batiatus")

        for (auction in auctions) {
            val remaining = auction.daysRemaining - 1
            if (remaining <= 0) {
                // Auction ended
                val playerWon = auction.isPlayerHighBidder
                resolutions.add(
                    EquipmentAuctionResolution(
                        auction = auction,
                        isPlayerWinner = playerWon,
                        winnerName = auction.highBidder,
                        finalPrice = auction.currentBid,
                        message = if (playerWon) {
                            "TEBRİKLER! ${auction.item.name} müzayedesini ${auction.currentBid} Denarii teklifle kazandınız! Teçhizat cephaneliğinize teslim edildi."
                        } else {
                            "MÜZAYEDE SONA ERDİ: ${auction.item.name}, ${auction.highBidder} tarafından ${auction.currentBid} Denarii bedelle satın alındı."
                        }
                    )
                )
            } else {
                // In progress - simulate rival counter-bids
                var newBid = auction.currentBid
                var newHighBidder = auction.highBidder
                var isPlayerStillHigh = auction.isPlayerHighBidder

                // If player is currently high bidder, 45% chance an AI rival raises the bid
                if (auction.isPlayerHighBidder && Random.nextFloat() < 0.45f) {
                    val rival = rivalLanistas.random()
                    val increment = auction.minBidIncrement + (Random.nextInt(1, 4) * 50)
                    newBid += increment
                    newHighBidder = rival
                    isPlayerStillHigh = false
                    if (!auction.bidders.contains(rival)) {
                        auction.bidders.add(rival)
                    }
                } else if (!auction.isPlayerHighBidder && Random.nextFloat() < 0.30f) {
                    val rival = rivalLanistas.random()
                    if (rival != auction.highBidder) {
                        newBid += auction.minBidIncrement
                        newHighBidder = rival
                    }
                }

                activeAuctions.add(
                    auction.copy(
                        currentBid = newBid,
                        highBidder = newHighBidder,
                        isPlayerHighBidder = isPlayerStillHigh,
                        daysRemaining = remaining
                    )
                )
            }
        }

        return Pair(activeAuctions, resolutions)
    }

    // -------------------------------------------------------------
    // CUSTOM COMMISSIONS PROGRESSION
    // -------------------------------------------------------------
    fun advanceCommissions(
        commissions: List<CustomCommission>
    ): Pair<List<CustomCommission>, List<EquipmentItem>> {
        val remainingCommissions = mutableListOf<CustomCommission>()
        val finishedItems = mutableListOf<EquipmentItem>()

        for (comm in commissions) {
            val remaining = comm.daysRemaining - 1
            if (remaining <= 0) {
                // Commission ready!
                val roll = Random.nextFloat()
                val finalQuality = when {
                    roll < 0.15f && comm.targetQuality != EquipmentQuality.NAMED -> EquipmentQuality.values()[minOf(EquipmentQuality.values().size - 1, comm.targetQuality.ordinal + 1)]
                    roll > 0.88f && comm.targetQuality != EquipmentQuality.CRUDE -> EquipmentQuality.values()[maxOf(0, comm.targetQuality.ordinal - 1)]
                    else -> comm.targetQuality
                }

                val craftedItem = EquipmentItem(
                    id = "comm_crafted_${comm.id}",
                    name = comm.weaponName,
                    category = comm.category,
                    type = comm.type,
                    slot = comm.type.defaultSlot,
                    quality = finalQuality,
                    material = comm.material,
                    maxDurability = (120 * finalQuality.durabilityMultiplier * comm.material.durabilityMod).toInt(),
                    currentDurability = (120 * finalQuality.durabilityMultiplier * comm.material.durabilityMod).toInt(),
                    damage = (16 * comm.material.damageMod).roundToInt(),
                    reachCm = if (comm.type == EquipmentType.SPEAR || comm.type == EquipmentType.TRIDENT) 185 else 68,
                    attackSpeed = if (comm.weightProfile == "Light & Fast") 75 else 60,
                    armorPenetrationPercent = if (comm.bladeProfile == "Pointed Thrusting") 35 else 20,
                    weightKg = if (comm.weightProfile == "Light & Fast") 1.1f else 1.6f,
                    balance = 88,
                    reliability = 94,
                    staminaCost = if (comm.weightProfile == "Light & Fast") 4.8f else 6.2f,
                    specialProperty = "Özel Dövüm: ${comm.bladeProfile} - ${comm.decoration}",
                    basePriceDenarii = (comm.totalCost * 0.85f).roundToInt(),
                    prestigeBonus = (25 * finalQuality.statMultiplier + comm.material.prestige).toInt(),
                    commissionedSmith = comm.smithName
                )
                finishedItems.add(craftedItem)
            } else {
                remainingCommissions.add(comm.copy(daysRemaining = remaining))
            }
        }

        return Pair(remainingCommissions, finishedItems)
    }

    // -------------------------------------------------------------
    // DYNAMIC EVENT GENERATOR
    // -------------------------------------------------------------
    fun generateRandomMarketEvent(dayNumber: Int, currentEvents: List<EquipmentMarketEvent>): EquipmentMarketEvent? {
        if (currentEvents.size >= 2) return null
        val eventPool = listOf(
            EquipmentMarketEvent(
                id = "mkt_ev_iron_shortage_$dayNumber",
                title = "İtalya Demir Cevheri Kıtlığı",
                description = "Kuzey Etruria madenlerindeki grev ham demir tedarikini vurdu. Silah ve zırh fiyatları %30 arttı.",
                categoryAffected = EquipmentCategory.WEAPONS,
                priceMultiplier = 1.30f,
                scarcityNote = "Demir Silahlar +%30 Pahalı",
                daysRemaining = 5,
                isPositive = false
            ),
            EquipmentMarketEvent(
                id = "mkt_ev_iberian_shipment_$dayNumber",
                title = "Puteoli'ye İber Çeliği Kadırgası Yanaştı",
                description = "Hispania'dan gelen tüccarlar üstün Toledo çeliği getirdi. Fiyatlarda %15 indirim var.",
                categoryAffected = EquipmentCategory.ARMOR,
                priceMultiplier = 0.85f,
                scarcityNote = "Zırhlarda %15 İndirim & Bol Stok",
                daysRemaining = 4,
                isPositive = true
            ),
            EquipmentMarketEvent(
                id = "mkt_ev_mars_festival_$dayNumber",
                title = "Mars Bayramı Büyük Oyunları İlan Edildi",
                description = "Roma ve Capua amfitiyatrolarındaki bayram öncesi tüm lanistalar silah depoluyor. Talep zirvede!",
                categoryAffected = null,
                priceMultiplier = 1.25f,
                scarcityNote = "Piyasa Genelinde +%25 Fiyat Artışı",
                daysRemaining = 6,
                isPositive = false
            ),
            EquipmentMarketEvent(
                id = "mkt_ev_military_contract_$dayNumber",
                title = "Legio VI Ferrata Lejyon İkmal Emri",
                description = "Askeri müteahhitler Capua zırh atölyelerini kapattı. Miğfer ve kalkan fiyatları yükseldi.",
                categoryAffected = EquipmentCategory.HELMETS,
                priceMultiplier = 1.35f,
                scarcityNote = "Miğfer ve Başlıklar +%35",
                daysRemaining = 5,
                isPositive = false
            ),
            EquipmentMarketEvent(
                id = "mkt_ev_smith_workshop_$dayNumber",
                title = "Ünlü Yunan Usta Capua'da Körük Yaktı",
                description = "Korint tuncunu usta döven zanaatkâr sipariş almaya başladı. Özel siparişlerde hız artışı.",
                categoryAffected = EquipmentCategory.SHIELDS,
                priceMultiplier = 0.90f,
                scarcityNote = "Kalkanlar %10 İndirimli",
                daysRemaining = 7,
                isPositive = true
            )
        )

        return eventPool.filter { ev -> currentEvents.none { it.title == ev.title } }.randomOrNull()
    }
}

data class EquipmentAuctionResolution(
    val auction: EquipmentAuction,
    val isPlayerWinner: Boolean,
    val winnerName: String,
    val finalPrice: Int,
    val message: String
)

