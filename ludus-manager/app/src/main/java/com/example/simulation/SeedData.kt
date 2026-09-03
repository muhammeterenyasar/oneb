package com.example.simulation

import com.example.model.*

object SeedData {

    fun createInitialGladiators(): List<Gladiator> {
        return listOf(
            Gladiator(
                id = "glad_1",
                name = "Titus",
                gladiatorClass = GladiatorClass.MURMILLO,
                origin = Origin.THRAX,
                status = GladiatorStatus.AUCTORATUS,
                age = 28,
                personality = Personality.DISCIPLINED,
                rank = 1,
                contractMonths = 18,
                monthlyWage = 120,
                physicalStats = PhysicalStats(
                    strength = 16,
                    speed = 12,
                    agility = 11,
                    endurance = 15,
                    reflex = 13,
                    painTolerance = 16,
                    heightCm = 185,
                    weightKg = 92,
                    reachCm = 89,
                    muscleDensity = 88,
                    bodyType = "Colossus"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Kılıç & Kalkan Çalışması",
                    dailyProgressPercent = 65f,
                    totalTrainingDays = 140,
                    experiencePoints = 740,
                    level = 3,
                    nextLevelThreshold = 1000,
                    dietRegimen = "High Protein (Gladiator Barley & Meat)",
                    fatigueAccrued = 14,
                    weaponMastery = 78,
                    shieldMastery = 85,
                    footworkMastery = 62,
                    tacticalDiscipline = 82,
                    assignedInstructor = "Marcus (Doctore)"
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 8,
                    victories = 6,
                    defeats = 2,
                    kills = 1,
                    sparedByCrowd = 1,
                    currentWinStreak = 2,
                    bestWinStreak = 4,
                    crowdApprovalRating = 72,
                    totalDenariiEarned = 3400,
                    criticalStrikesDelivered = 11,
                    timesDisarmed = 0,
                    arenasFoughtIn = mutableListOf("Capua Arena", "Pompeii Pit"),
                    titlesWon = mutableListOf("Capua Veteran Scutum")
                ),
                attributes = GladiatorAttributes(
                    strength = 16,
                    speed = 12,
                    agility = 11,
                    endurance = 15,
                    reflex = 13,
                    painTolerance = 16,
                    swordsmanship = 17,
                    shieldSkill = 18,
                    grappling = 14,
                    footwork = 12,
                    counterAttack = 15,
                    courage = 16,
                    discipline = 17,
                    composure = 14
                ),
                condition = GladiatorCondition(
                    health = 92,
                    stamina = 86,
                    pain = 5,
                    stress = 15,
                    morale = 75,
                    recovery = 85
                ),
                relationships = mutableListOf(
                    Relationship("Cassian", "Rivalry", "Cassian humiliated Titus in Capua local pit last festival."),
                    Relationship("Gaius", "Friendship", "Trained together in the sands for 2 years.")
                ),
                careerStats = CareerStats(fights = 8, wins = 6, losses = 2, kills = 1, crowdFavor = 72, denariiEarned = 3400),
                trainingFocus = "Kılıç & Kalkan Çalışması",
                diet = "High Protein (Gladiator Barley & Meat)"
            ),
            Gladiator(
                id = "glad_2",
                name = "Gaius",
                gladiatorClass = GladiatorClass.THRAEX,
                origin = Origin.HISPANIA,
                status = GladiatorStatus.SLAVE,
                age = 24,
                personality = Personality.AMBITIOUS,
                rank = 2,
                contractMonths = 36,
                monthlyWage = 40,
                physicalStats = PhysicalStats(
                    strength = 13,
                    speed = 17,
                    agility = 16,
                    endurance = 13,
                    reflex = 18,
                    painTolerance = 12,
                    heightCm = 178,
                    weightKg = 78,
                    reachCm = 85,
                    muscleDensity = 78,
                    bodyType = "Agile"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Çeviklik & Ayak Oyunları",
                    dailyProgressPercent = 50f,
                    totalTrainingDays = 85,
                    experiencePoints = 420,
                    level = 2,
                    nextLevelThreshold = 800,
                    dietRegimen = "Standard Legionary Rations",
                    fatigueAccrued = 10,
                    weaponMastery = 70,
                    shieldMastery = 55,
                    footworkMastery = 82,
                    tacticalDiscipline = 65,
                    assignedInstructor = "Marcus (Doctore)"
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 5,
                    victories = 4,
                    defeats = 1,
                    kills = 0,
                    sparedByCrowd = 0,
                    currentWinStreak = 1,
                    bestWinStreak = 3,
                    crowdApprovalRating = 64,
                    totalDenariiEarned = 1800,
                    criticalStrikesDelivered = 9,
                    timesDisarmed = 1,
                    arenasFoughtIn = mutableListOf("Capua Arena"),
                    titlesWon = mutableListOf()
                ),
                attributes = GladiatorAttributes(
                    strength = 13,
                    speed = 17,
                    agility = 16,
                    endurance = 13,
                    reflex = 18,
                    painTolerance = 12,
                    swordsmanship = 15,
                    shieldSkill = 12,
                    grappling = 11,
                    footwork = 17,
                    counterAttack = 16,
                    courage = 14,
                    discipline = 13,
                    composure = 12
                ),
                condition = GladiatorCondition(
                    health = 96,
                    stamina = 90,
                    pain = 0,
                    stress = 20,
                    morale = 68,
                    recovery = 88
                ),
                careerStats = CareerStats(fights = 5, wins = 4, losses = 1, kills = 0, crowdFavor = 64, denariiEarned = 1800),
                trainingFocus = "Çeviklik & Ayak Oyunları"
            ),
            Gladiator(
                id = "glad_3",
                name = "Dion",
                gladiatorClass = GladiatorClass.RETIARIUS,
                origin = Origin.NUMIDIA,
                status = GladiatorStatus.SLAVE,
                age = 23,
                personality = Personality.CALM,
                rank = 3,
                contractMonths = 40,
                monthlyWage = 35,
                physicalStats = PhysicalStats(
                    strength = 12,
                    speed = 16,
                    agility = 18,
                    endurance = 14,
                    reflex = 17,
                    painTolerance = 13,
                    heightCm = 188,
                    weightKg = 81,
                    reachCm = 92,
                    muscleDensity = 75,
                    bodyType = "Lean"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Ağ & Üçlü Mızrak Taktiği",
                    dailyProgressPercent = 40f,
                    totalTrainingDays = 60,
                    experiencePoints = 310,
                    level = 2,
                    nextLevelThreshold = 800,
                    dietRegimen = "High Protein (Gladiator Barley & Meat)",
                    fatigueAccrued = 8,
                    weaponMastery = 65,
                    shieldMastery = 40,
                    footworkMastery = 86,
                    tacticalDiscipline = 74,
                    assignedInstructor = "Marcus (Doctore)"
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 4,
                    victories = 3,
                    defeats = 1,
                    kills = 0,
                    sparedByCrowd = 1,
                    currentWinStreak = 2,
                    bestWinStreak = 2,
                    crowdApprovalRating = 60,
                    totalDenariiEarned = 1200,
                    criticalStrikesDelivered = 5,
                    timesDisarmed = 0,
                    arenasFoughtIn = mutableListOf("Capua Arena"),
                    titlesWon = mutableListOf()
                ),
                attributes = GladiatorAttributes(
                    strength = 12,
                    speed = 16,
                    agility = 18,
                    endurance = 14,
                    reflex = 17,
                    painTolerance = 13,
                    swordsmanship = 11,
                    shieldSkill = 8,
                    grappling = 13,
                    footwork = 18,
                    counterAttack = 14,
                    courage = 15,
                    discipline = 15,
                    composure = 17
                ),
                condition = GladiatorCondition(
                    health = 100,
                    stamina = 95,
                    pain = 0,
                    stress = 10,
                    morale = 80,
                    recovery = 90
                ),
                careerStats = CareerStats(fights = 4, wins = 3, losses = 1, kills = 0, crowdFavor = 60, denariiEarned = 1200),
                trainingFocus = "Ağ & Üçlü Mızrak Taktiği"
            ),
            Gladiator(
                id = "glad_4",
                name = "Hektor",
                gladiatorClass = GladiatorClass.HOPLOMACHUS,
                origin = Origin.GREECE,
                status = GladiatorStatus.AUCTORATUS,
                age = 30,
                personality = Personality.PROUD,
                rank = 4,
                contractMonths = 12,
                monthlyWage = 140,
                physicalStats = PhysicalStats(
                    strength = 15,
                    speed = 13,
                    agility = 13,
                    endurance = 16,
                    reflex = 14,
                    painTolerance = 15,
                    heightCm = 182,
                    weightKg = 85,
                    reachCm = 88,
                    muscleDensity = 84,
                    bodyType = "Muscular"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Mızrak & Savunma",
                    dailyProgressPercent = 75f,
                    totalTrainingDays = 210,
                    experiencePoints = 890,
                    level = 3,
                    nextLevelThreshold = 1000,
                    dietRegimen = "High Protein (Gladiator Barley & Meat)",
                    fatigueAccrued = 22,
                    weaponMastery = 80,
                    shieldMastery = 78,
                    footworkMastery = 66,
                    tacticalDiscipline = 84,
                    assignedInstructor = "Marcus (Doctore)"
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 11,
                    victories = 7,
                    defeats = 4,
                    kills = 2,
                    sparedByCrowd = 2,
                    currentWinStreak = 1,
                    bestWinStreak = 4,
                    crowdApprovalRating = 70,
                    totalDenariiEarned = 4100,
                    criticalStrikesDelivered = 14,
                    timesDisarmed = 1,
                    arenasFoughtIn = mutableListOf("Capua Arena", "Neapolis Arena"),
                    titlesWon = mutableListOf("Neapolis Champion Spear")
                ),
                attributes = GladiatorAttributes(
                    strength = 15,
                    speed = 13,
                    agility = 13,
                    endurance = 16,
                    reflex = 14,
                    painTolerance = 15,
                    swordsmanship = 14,
                    shieldSkill = 16,
                    grappling = 15,
                    footwork = 13,
                    counterAttack = 13,
                    courage = 16,
                    discipline = 16,
                    composure = 15
                ),
                condition = GladiatorCondition(
                    health = 88,
                    stamina = 78,
                    pain = 12,
                    stress = 25,
                    morale = 60,
                    recovery = 80
                ),
                injuries = mutableListOf(
                    Injury("inj_1", "Sağ Omuz Ezilmesi", "Hafif doku ezilmesi. Saldırı hızını %10 düşürür.", 3, "Light", "-1 Speed")
                ),
                careerStats = CareerStats(fights = 11, wins = 7, losses = 4, kills = 2, crowdFavor = 70, denariiEarned = 4100),
                trainingFocus = "Mızrak & Savunma"
            )
        )
    }

    fun createMarketGladiators(): List<Gladiator> {
        return listOf(
            Gladiator(
                id = "mkt_1",
                name = "Brutus",
                gladiatorClass = GladiatorClass.MURMILLO,
                origin = Origin.GAUL,
                status = GladiatorStatus.SLAVE,
                age = 27,
                personality = Personality.BRUTAL,
                rank = 1,
                contractMonths = 48,
                monthlyWage = 50,
                physicalStats = PhysicalStats(
                    strength = 18,
                    speed = 10,
                    agility = 9,
                    endurance = 17,
                    reflex = 11,
                    painTolerance = 19,
                    heightCm = 191,
                    weightKg = 98,
                    reachCm = 93,
                    muscleDensity = 94,
                    bodyType = "Colossus"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Ağır Güç Antrenmanı",
                    level = 2,
                    experiencePoints = 500,
                    weaponMastery = 68,
                    shieldMastery = 70
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 3,
                    victories = 3,
                    defeats = 0,
                    kills = 2,
                    crowdApprovalRating = 76,
                    totalDenariiEarned = 1400
                ),
                attributes = GladiatorAttributes(
                    strength = 18,
                    speed = 10,
                    agility = 9,
                    endurance = 17,
                    reflex = 11,
                    painTolerance = 19,
                    swordsmanship = 14,
                    shieldSkill = 15,
                    grappling = 17,
                    footwork = 10,
                    counterAttack = 12,
                    courage = 17,
                    discipline = 12,
                    composure = 10
                ),
                condition = GladiatorCondition(health = 100, stamina = 100, morale = 70)
            ),
            Gladiator(
                id = "mkt_2",
                name = "Hermes",
                gladiatorClass = GladiatorClass.DIMACHAERUS,
                origin = Origin.GREECE,
                status = GladiatorStatus.AUCTORATUS,
                age = 22,
                personality = Personality.SHOWMAN,
                rank = 2,
                contractMonths = 24,
                monthlyWage = 90,
                physicalStats = PhysicalStats(
                    strength = 12,
                    speed = 18,
                    agility = 19,
                    endurance = 13,
                    reflex = 17,
                    painTolerance = 11,
                    heightCm = 175,
                    weightKg = 73,
                    reachCm = 83,
                    muscleDensity = 72,
                    bodyType = "Agile"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Çift Kılıç Dansı",
                    level = 1,
                    experiencePoints = 280,
                    weaponMastery = 72,
                    shieldMastery = 30
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 2,
                    victories = 2,
                    defeats = 0,
                    kills = 0,
                    crowdApprovalRating = 85,
                    totalDenariiEarned = 1100
                ),
                attributes = GladiatorAttributes(
                    strength = 12,
                    speed = 18,
                    agility = 19,
                    endurance = 13,
                    reflex = 17,
                    painTolerance = 11,
                    swordsmanship = 16,
                    shieldSkill = 6,
                    grappling = 10,
                    footwork = 18,
                    counterAttack = 17,
                    courage = 14,
                    discipline = 11,
                    composure = 13
                ),
                condition = GladiatorCondition(health = 100, stamina = 100, morale = 85)
            ),
            Gladiator(
                id = "mkt_3",
                name = "Caratacus",
                gladiatorClass = GladiatorClass.BESTIARIUS,
                origin = Origin.GERMANIA,
                status = GladiatorStatus.SLAVE,
                age = 26,
                personality = Personality.RECKLESS,
                rank = 3,
                contractMonths = 36,
                monthlyWage = 45,
                physicalStats = PhysicalStats(
                    strength = 16,
                    speed = 14,
                    agility = 14,
                    endurance = 16,
                    reflex = 15,
                    painTolerance = 17,
                    heightCm = 184,
                    weightKg = 87,
                    reachCm = 88,
                    muscleDensity = 86,
                    bodyType = "Muscular"
                ),
                trainingProgress = TrainingProgress(
                    currentFocus = "Vahşi Hayvan Avı",
                    level = 2,
                    experiencePoints = 480,
                    weaponMastery = 66,
                    shieldMastery = 52
                ),
                historicalPerformance = HistoricalPerformance(
                    totalMatches = 4,
                    victories = 3,
                    defeats = 1,
                    kills = 1,
                    crowdApprovalRating = 68,
                    totalDenariiEarned = 1350
                ),
                attributes = GladiatorAttributes(
                    strength = 16,
                    speed = 14,
                    agility = 14,
                    endurance = 16,
                    reflex = 15,
                    painTolerance = 17,
                    swordsmanship = 13,
                    shieldSkill = 11,
                    grappling = 16,
                    footwork = 13,
                    counterAttack = 14,
                    courage = 18,
                    discipline = 10,
                    composure = 11
                ),
                condition = GladiatorCondition(health = 100, stamina = 100, morale = 65)
            )
        )
    }

    fun createRivalFighter(): Gladiator {
        return ArenaDatabase.createInitialPersistentFighters().find { it.id == "fighter_cassian" }
            ?: Gladiator(
                id = "fighter_cassian",
                name = "Cassian",
                nickname = "Blood Drinker",
                gladiatorClass = GladiatorClass.THRAEX,
                origin = Origin.THRAX,
                status = GladiatorStatus.AUCTORATUS,
                age = 26,
                personality = Personality.BRUTAL,
                tier = OpponentTier.ELITE,
                ludusAffiliation = "Domus Auctor",
                ownerName = "Lanista Decimus",
                rank = 1
            )
    }

    fun createInitialStaff(): List<StaffMember> {
        return listOf(
            StaffMember("staff_1", "Head Trainer", "Marcus", 3, "Güç ve Kılıç Disiplini", 300, "Gladyatörlerin antrenman verimini +%15 artırır"),
            StaffMember("staff_2", "Medicus", "Lucius", 2, "Cerrahi ve Bitkisel Şifa", 250, "Sakatlık iyileşme süresini 2 gün kısaltır, ölüm riskini düşürür"),
            StaffMember("staff_3", "Engineer", "Tiberius", 2, "Tesis İyileştirme", 200, "Tesis geliştirme maliyetlerini %10 düşürür"),
            StaffMember("staff_4", "Security Chief", "Gannicus", 3, "Köle & Güvenlik Nöbeti", 350, "İsyan ve sabotaj riskini %80 engeller")
        )
    }

    fun createInitialFacilities(): List<Facility> {
        return listOf(
            Facility("fac_1", "Antrenman Sahası", 2, 5, 2500, "Kum zemin, ahşap palisler ve ağır mankenler.", "Antrenman Verimi: +%15", "Sonraki Seviye: +%25 Verim"),
            Facility("fac_2", "Gladyatör Koğuşları", 2, 5, 1800, "Gladyatörlerin uyuduğu ve dinlendiği yatakhane.", "Dinlenme & Moral Toparlama: +%10", "Sonraki Seviye: +10 Kapasite, +%20 Dinlenme"),
            Facility("fac_3", "Silah Atölyesi", 1, 5, 1200, "Demir dövme ve zırh bileme atölyesi.", "Zırh ve Silah Dayanıklılığı: Seviye 1", "Sonraki Seviye: Seviye 2 Zırhlar ve Hasar Bonusu"),
            Facility("fac_4", "Hastane & Revir", 1, 5, 1500, "Yaralı gladyatörlerin tedavi gördüğü revir.", "Hafif Ameliyat ve Sargı İmkanı", "Sonraki Seviye: Yoğun Bakım ve Hızlı İyileşme")
        )
    }

    fun createInitialCities(): List<CityProgression> {
        return listOf(
            CityProgression(1, "Capua", "Capua Taşra Arenası", 0, 1.0f, true, "Gladyatörlüğün beşiği. Sert kum, coşkulu taşra halkı."),
            CityProgression(2, "Pompeii", "Pompeii Amfitiyatrosu", 2500, 1.6f, false, "Vezüv'ün gölgesinde zengin tüccarların cömert ödülleri."),
            CityProgression(3, "Neapolis", "Neapolis Deniz Arenası", 5000, 2.4f, false, "Yunan etkisinde sofistike dövüşler ve büyük bahisler."),
            CityProgression(4, "Roma", "Flavianus Amfitiyatrosu (Colosseum)", 10000, 5.0f, false, "İmparatorluğun kalbi. 50.000 seyirci ve ölümsüz şan.")
        )
    }

    fun createInitialPatrons(): List<Patron> {
        return listOf(
            Patron("pat_1", "Senator Cassius", "Capua Senato Temsilcisi", 65, 500, "Capua Oyunlarında zaferler kazan", "Senato nüfuzu ve elit dövüş izinleri"),
            Patron("pat_2", "Fortuna Bahis Evi", "Tüccar Quintus", 45, 400, "Dövüşlerin 3 raunttan uzun sürmesini sağla", "Bahis gelirlerinden +%15 komisyon"),
            Patron("pat_3", "Capua Halk Loncası", "Plebeian Temsilcisi", 70, 250, "Cesur ve şovmen gladyatörler çıkart", "Halk desteği ve popülarite artışı")
        )
    }

    fun createInitialRivals(): List<RivalLudus> {
        return listOf(
            RivalLudus("riv_1", "Domus Auctor", "Lanista Decimus", "Capua", 1450, "Rufus", GladiatorClass.THRAEX, "Capua'daki en büyük yerel rakibiniz. Sık sık meydan okur."),
            RivalLudus("riv_2", "Domus Aemilius", "Lanista Aemilius", "Pompeii", 2800, "Spiculus", GladiatorClass.MURMILLO, "Zengin senatör fonuyla kurulan elit bir ludus."),
            RivalLudus("riv_3", "Ludus Brutus", "Lanista Brutus", "Roma", 6500, "Flamma", GladiatorClass.SECUTOR, "Roma'nın en korkulan ve acımasız şampiyonlarına sahip.")
        )
    }

    fun createInitialChronicles(): List<ChronicleEntry> {
        return listOf(
            ChronicleEntry("69 A.U.C.", "Ludus Kuruldu", "Capua banliyösünde eski bir zeytinyağı tüccarı olarak gladyatör ocağımızı kurduk.", true),
            ChronicleEntry("69 A.U.C.", "Titus'un İlk Zaferi", "Titus, yerel Capua kum havuzunda köle dövüşçü Drusus'u dize getirerek ilk 400 denariiyi kazandırdı.", true)
        )
    }
}
