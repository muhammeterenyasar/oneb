package com.example.data.engine

import com.example.model.CampaignMission
import com.example.model.EnemyGladiator
import com.example.model.EnemyTier
import com.example.model.GladiatorClass

object CampaignEngine {

    val allMissions: List<CampaignMission> = listOf(
        // CHAPTER 1: CAPUA
        CampaignMission(
            id = "camp_1_mud_pit",
            chapterOrder = 1,
            chapterTitle = "Bölüm 1: Capua Çamuru",
            missionTitle = "Çamur Çukuru Elemesi",
            description = "Capua amfitiyatrosuna çıkmadan önce yerel çaylaklar liginde adını duyur.",
            bossEnemy = EnemyGladiator(
                name = "Tiberius",
                title = "Çamur Baltacısı",
                ludusOrigin = "Capua Yerel Ludusu",
                gladiatorClass = GladiatorClass.MURMILLO,
                tier = EnemyTier.NOVICE,
                level = 2,
                traitName = "Ağır Darbe",
                str = 14, agi = 9, sta = 13, mor = 70,
                maxHp = 110, currentHp = 110,
                rewardGold = 120, rewardPrestige = 25
            ),
            rewardGold = 120,
            rewardPrestige = 25,
            trophyName = "Capua Çaylak Madalyonu"
        ),
        CampaignMission(
            id = "camp_2_capua_boss",
            chapterOrder = 1,
            chapterTitle = "Bölüm 1: Capua Çamuru",
            missionTitle = "Capua Bölge Şampiyonluğu Finali",
            description = "Capua halkının sevgilisi devasa Drusus'u devir ve şehre hükmet!",
            bossEnemy = EnemyGladiator(
                name = "Drusus 'The Bull'",
                title = "Capua Amfitiyatro Şampiyonu",
                ludusOrigin = "Batiatus Ekolü",
                gladiatorClass = GladiatorClass.SECUTOR,
                tier = EnemyTier.VETERAN,
                level = 4,
                traitName = "Çelik Zırh & Yıkılmaz",
                str = 18, agi = 11, sta = 18, mor = 85,
                maxHp = 160, currentHp = 160,
                rewardGold = 250, rewardPrestige = 60
            ),
            rewardGold = 250,
            rewardPrestige = 60,
            trophyName = "🏆 Capua Bronz Çelengi"
        ),

        // CHAPTER 2: VERONA & VENATIO
        CampaignMission(
            id = "camp_3_verona_venatio",
            chapterOrder = 2,
            chapterTitle = "Bölüm 2: Verona & Vahşi Av",
            missionTitle = "Venatio: Alp Dağları Kurt Sürüsü",
            description = "Kuzeyden getirilen vahşi kurt sürüsünü tek başına alt ederek seyircileri coştur.",
            bossEnemy = EnemyGladiator(
                name = "Lupus Rex",
                title = "Kuzeyin Vahşi Alfa Kurdu",
                ludusOrigin = "Alp Vahşi Doğası",
                gladiatorClass = GladiatorClass.DIMACHAERUS,
                tier = EnemyTier.VETERAN,
                level = 5,
                traitName = "Yırtıcı Refleks (+%25 Hız)",
                str = 16, agi = 20, sta = 14, mor = 90,
                maxHp = 145, currentHp = 145,
                rewardGold = 220, rewardPrestige = 50
            ),
            rewardGold = 220,
            rewardPrestige = 50,
            trophyName = "Kurt Postu Nişanı"
        ),
        CampaignMission(
            id = "camp_4_verona_boss",
            chapterOrder = 2,
            chapterTitle = "Bölüm 2: Verona & Vahşi Av",
            missionTitle = "Verona Büyük Munera Şampiyonası",
            description = "Verona'nın yenilmez altın ağ ustası Crixus'a karşı taktiksel meydan okuma.",
            bossEnemy = EnemyGladiator(
                name = "Crixus 'The Net King'",
                title = "Verona Arena Efsanesi",
                ludusOrigin = "Verona Seçkin Okulu",
                gladiatorClass = GladiatorClass.RETIARIUS,
                tier = EnemyTier.ELITE,
                level = 7,
                traitName = "Ölümcül Ağ & Kaçınma",
                str = 19, agi = 24, sta = 19, mor = 95,
                maxHp = 210, currentHp = 210,
                rewardGold = 450, rewardPrestige = 100
            ),
            rewardGold = 450,
            rewardPrestige = 100,
            trophyName = "🗡️ Verona Gümüş Sica'sı"
        ),

        // CHAPTER 3: SENATE SHADOWS
        CampaignMission(
            id = "camp_5_senate_duel",
            chapterOrder = 3,
            chapterTitle = "Bölüm 3: Senato Komploları",
            missionTitle = "Senatör Marcus'un Özel Bahis Düellosu",
            description = "Gizli villalarda zengin soyluların altın döktüğü ölümcül özel düello.",
            bossEnemy = EnemyGladiator(
                name = "Spiculus",
                title = "Senato Suikastçısı",
                ludusOrigin = "Roma Gizli Loncası",
                gladiatorClass = GladiatorClass.THRAEX,
                tier = EnemyTier.ELITE,
                level = 8,
                traitName = "Zehirli Bıçak & Kritik Vuruş",
                str = 21, agi = 25, sta = 20, mor = 90,
                maxHp = 230, currentHp = 230,
                rewardGold = 500, rewardPrestige = 80
            ),
            rewardGold = 500,
            rewardPrestige = 80,
            trophyName = "Senato Altın Mührü"
        ),
        CampaignMission(
            id = "camp_6_senate_boss",
            chapterOrder = 3,
            chapterTitle = "Bölüm 3: Senato Komploları",
            missionTitle = "Subura Kanlı Yeraltı Tahtı",
            description = "Roma'nın karanlık yeraltı sokaklarının acımasız efendisi Priscus'u tahttan indir!",
            bossEnemy = EnemyGladiator(
                name = "Priscus 'The Executioner'",
                title = "Yeraltı Çukuru Celladı",
                ludusOrigin = "Subura Karanlık Arenası",
                gladiatorClass = GladiatorClass.MURMILLO,
                tier = EnemyTier.CHAMPION_BOSS,
                level = 9,
                traitName = "Ezici Balyoz & Boyun Vurucu",
                str = 26, agi = 18, sta = 25, mor = 100,
                maxHp = 300, currentHp = 300,
                rewardGold = 700, rewardPrestige = 150
            ),
            rewardGold = 700,
            rewardPrestige = 150,
            trophyName = "👑 Subura Yeraltı Tacı"
        ),

        // CHAPTER 4: ROME COLOSSEUM IMPERIAL FINALE
        CampaignMission(
            id = "camp_7_praetorian",
            chapterOrder = 4,
            chapterTitle = "Bölüm 4: Roma Colosseum Finali",
            missionTitle = "İmparatorluk Muhafızları Ön Elemeleri",
            description = "İmparatorun huzuruna çıkmadan önce seçkin Praetorian şampiyonunu mağlup et.",
            bossEnemy = EnemyGladiator(
                name = "Spartok 'The Praetorian'",
                title = "İmparatorluk Muhafız Başkomutanı",
                ludusOrigin = "Palatine Saray Muhafızları",
                gladiatorClass = GladiatorClass.SECUTOR,
                tier = EnemyTier.CHAMPION_BOSS,
                level = 10,
                traitName = "Kusursuz Lejyon Disiplini",
                str = 27, agi = 24, sta = 27, mor = 100,
                maxHp = 340, currentHp = 340,
                rewardGold = 850, rewardPrestige = 200
            ),
            rewardGold = 850,
            rewardPrestige = 200,
            trophyName = "Praetorian Çelik Kartalı"
        ),
        CampaignMission(
            id = "camp_8_colosseum_grand_boss",
            chapterOrder = 4,
            chapterTitle = "Bölüm 4: Roma Colosseum Finali",
            missionTitle = "COLOSSEUM BÜYÜK İMPARATORLUK FİNALİ",
            description = "50 bin Romalının ve İmparatorun gözü önünde tarihin en büyük şampiyonu Flamma ile dövüş!",
            bossEnemy = EnemyGladiator(
                name = "FLAMMA (The Living Legend)",
                title = "Colosseum Yenilgisiz Efsanesi",
                ludusOrigin = "İmparatorluk Büyük Okulu (Ludus Magnus)",
                gladiatorClass = GladiatorClass.MURMILLO,
                tier = EnemyTier.CHAMPION_BOSS,
                level = 12,
                traitName = "Tanrısal Güç & Ebedi Şan",
                str = 30, agi = 28, sta = 30, mor = 100,
                maxHp = 420, currentHp = 420,
                rewardGold = 2000, rewardPrestige = 500
            ),
            rewardGold = 2000,
            rewardPrestige = 500,
            trophyName = "🏛️ İMPARATORLUK ALTIN ÇELENGİ & ROMA ÖZGÜRLÜĞÜ"
        )
    )

    fun getMissionsForChapter(chapterOrder: Int): List<CampaignMission> {
        return allMissions.filter { it.chapterOrder == chapterOrder }
    }

    fun isMissionUnlocked(mission: CampaignMission, completedMissionIds: List<String>): Boolean {
        val missionIndex = allMissions.indexOfFirst { it.id == mission.id }
        if (missionIndex <= 0) return true
        val previousMission = allMissions[missionIndex - 1]
        return completedMissionIds.contains(previousMission.id)
    }
}
