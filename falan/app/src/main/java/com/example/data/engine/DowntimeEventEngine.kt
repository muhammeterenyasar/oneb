package com.example.data.engine

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object DowntimeEventEngine {

    fun generateBetweenCycleEvent(
        day: Int,
        cityTier: CityTier,
        ludusState: LudusState,
        forceRandom: Boolean = false
    ): BetweenCycleEvent {
        val events = listOf(
            BetweenCycleEvent(
                id = "local_merchant_$day",
                title = "İber Yarımadasından Gezgin Silah Tüccarı",
                subtitle = "Subura Dükkanları & Çarşı Girişi",
                type = BetweenCycleEventType.LOCAL_MERCHANT,
                dayOccurred = day,
                locationName = "${cityTier.cityName} Çarşı Kapısı",
                narrativeText = "İberya ve Galya madenlerinden yeni gelen bir kervan tüccarı, ludus kapınızın önüne sergi açtı. Nadir çelik silahlar, kaliteli Yunan şarapları ve kışla için dayanıklı zırh parçaları satıyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "merchant_toledo_gladius",
                        title = "Toledo Çeliği Gladius Satın Al (-40 🪙)",
                        description = "Seçilen dövüşçüye kusursuz dövülmüş İber kılıcı ver (+3 STR, Silah Seviyesi & Bilenmiş Silah buff'ı).",
                        icon = "🗡️",
                        goldCost = 40,
                        targetGladiatorStatBonus = "+3 Kuvvet (STR)",
                        targetStatType = "STR",
                        statAmount = 3,
                        activatesSharpenedWeapons = true,
                        narrativeOutcome = "Toledo çeliği kılıç gladyatörünüzün eline mükemmel oturdu! (+3 STR ve Bilenmiş Silahlar aktif)."
                    ),
                    EventDecisionChoice(
                        id = "merchant_greek_wine",
                        title = "Yunan İthal Şarap Fıçıları (-25 🪙)",
                        description = "Kışlaya kaliteli şarap ve kuru incir ikram et. Tüm yorgunluğu sıfırlar, +35 Moral verir.",
                        icon = "🍷",
                        goldCost = 25,
                        moraleChange = 35,
                        fatigueChange = -100,
                        narrativeOutcome = "Kışlada ziyafet verildi, gladyatörlerin yorgunluğu silindi ve morali tavan yaptı! (+35 Moral)."
                    ),
                    EventDecisionChoice(
                        id = "merchant_sell_gear",
                        title = "Eski Kışla Teçhizatını Tüccara Sat (+65 🪙)",
                        description = "Kullanılmayan yedek kalkan ve tahta kılıçları elden çıkar (+65 Altın, -5 Prestij).",
                        icon = "💰",
                        goldReward = 65,
                        prestigeReward = -5,
                        narrativeOutcome = "Eski malzemeleri satarak kasaya +65 Altın kazandırdınız."
                    ),
                    EventDecisionChoice(
                        id = "merchant_pass",
                        title = "Tüccarı Kibarca Geri Çevir (0 🪙)",
                        description = "Harcama yapmadan kapıyı kapat.",
                        icon = "🚪",
                        narrativeOutcome = "Tüccar mallarını toplayıp şehir meydanına doğru ilerledi."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "market_festival_$day",
                title = "Mercatus & Canavar Panayırı",
                subtitle = "Subura Forum & Pazar Meydanı",
                type = BetweenCycleEventType.MARKET_FESTIVAL,
                dayOccurred = day,
                locationName = "${cityTier.cityName} Pazar Alanı",
                narrativeText = "Şehir meydanında Akdeniz ve Kuzey Afrika'dan gelen tüccarların büyük panayırı kuruldu. Egzotik yırtıcılar, Şam çeliği silahlar ve gladyatör ziyafeti için taze mahsuller sergileniyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "market_beast_training",
                        title = "Numidya Aslanı & Kurt İdmanı (-55 🪙)",
                        description = "Dövüşçüleri kafesteki yırtıcılarla korkusuzluk talimine sok. Tüm kadroya +40 XP kazandırır.",
                        icon = "🦁",
                        goldCost = 55,
                        teamXpReward = 40,
                        moraleChange = 15,
                        narrativeOutcome = "Gladyatörler vahşi yırtıcıların kükremeleri karşısında soğukkanlı kalmayı öğrendi! Tüm dövüşçüler +40 XP kazandı."
                    ),
                    EventDecisionChoice(
                        id = "market_damascus_steel",
                        title = "Şam Çeliği Silah Satın Al (-45 🪙)",
                        description = "Seçilen gladyatöre efsanevi dövülmüş kılıç ver (+3 STR ve Bilenmiş Silah buff'ı).",
                        icon = "🗡️",
                        goldCost = 45,
                        targetGladiatorStatBonus = "+3 Kuvvet (STR)",
                        targetStatType = "STR",
                        statAmount = 3,
                        activatesSharpenedWeapons = true,
                        narrativeOutcome = "Efsanevi dövülmüş Şam çeliği kılıç gladyatörünüzün elinde ölümcül bir silaha dönüştü! (+3 STR & Bilenmiş Silah)."
                    ),
                    EventDecisionChoice(
                        id = "market_saturnalia_feast",
                        title = "Saturnalia Bal & Şarap Şöleni (-35 🪙)",
                        description = "Tüm kadronun yorgunluğunu sıfırlar, +35 Moral verir ve sakatlık sürelerini 1 gün kısaltır.",
                        icon = "🍷",
                        goldCost = 35,
                        moraleChange = 35,
                        fatigueChange = -100,
                        healsInjuriesDays = 1,
                        narrativeOutcome = "Mulsum şarabı ve kızarmış et ziyafeti kışlayı neşelendirdi! Tüm yorgunluk silindi, +35 Moral ve sakatlıklar hızla toparlandı."
                    ),
                    EventDecisionChoice(
                        id = "market_spectate",
                        title = "Pazarı Teftiş Et & Halkı Selamla (0 🪙)",
                        description = "Lanista olarak boy göster ve halkın saygısını kazan (+15 Prestij).",
                        icon = "🏛️",
                        prestigeReward = 15,
                        narrativeOutcome = "Pazarda asil duruşunuzla takdir topladınız. Şehir halkı ludus'unuza duyduğu saygıyı artırdı (+15 Prestij)."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "secret_society_$day",
                title = "Subura Yeraltı Locası & Mithras Kültü",
                subtitle = "Karanlık Katakomplar & Gölge Loncası",
                type = BetweenCycleEventType.SECRET_SOCIETY,
                dayOccurred = day,
                locationName = "Subura Yeraltı Mahzeni",
                narrativeText = "Karanlık bir sokakta pelerinli bir gölge elçi size gizli Mithras Locası'nın mührünü uzattı. Rakip okullara karşı gizli sabotajlar, kaçak gladyatörler ve borç defteri yakma teklifleri masada.",
                requiresTargetGladiator = false,
                choices = listOf(
                    EventDecisionChoice(
                        id = "secret_poison_rival",
                        title = "Rakip Şampiyonun Şarabına Belladonna Kat (-50 🪙)",
                        description = "Gölge suikastçısı sıradaki arena karşılaşmasında rakibin canını %25 zayıflatır!",
                        icon = "☠️",
                        goldCost = 50,
                        activatesPoison = true,
                        narrativeOutcome = "Suikastçı rakip kışlaya sızdı! Sıradaki maçta rakip şampiyon %25 eksik canla ve zehir zayıflığıyla arenaya çıkacak."
                    ),
                    EventDecisionChoice(
                        id = "secret_recruit_outlaw",
                        title = "Kaçak Kelt Berserker'ı Kadroya Kat (-70 🪙)",
                        description = "Yeraltında saklanan vahşi bir şampiyon gladyatörü gizlice kadroya dahil et.",
                        icon = "⛓️",
                        goldCost = 70,
                        recruitsShadowGladiator = true,
                        narrativeOutcome = "Karanlık locadan çıkan vahşi Kelt savaşçısı 'Brennus' kışlanıza katıldı! Efsanevi kuvvetiyle arenayı sarsmaya hazır."
                    ),
                    EventDecisionChoice(
                        id = "secret_burn_debt",
                        title = "Tefeci Borç Defterini Yaktır (-45 🪙)",
                        description = "Hırsızlar tefecinin mahzenine girer (Aktif borçtan 160 🪙 silinir. %20 risk: yakalanırsa 30 🪙 rüşvet cezası).",
                        icon = "🔥",
                        goldCost = 45,
                        reducesDebtAmount = 160,
                        riskChancePercent = 20,
                        riskFailureNarrative = "Hırsız mahzenden çıkarken yakalandı! Muhafızlara 30 Altın rüşvet vererek olayı kapattınız.",
                        narrativeOutcome = "Tefecinin borç senetleri kül oldu! Aktif borcunuzdan 160 Altın başarıyla silindi."
                    ),
                    EventDecisionChoice(
                        id = "secret_report_praetor",
                        title = "Locayı Şehir Praetor'una İhbar Et (0 🪙)",
                        description = "Roma kanunlarına sadık kal. +25 Senato Prestiji ve 35 🪙 ödül kazan.",
                        icon = "⚖️",
                        prestigeReward = 25,
                        goldReward = 35,
                        narrativeOutcome = "Praetor muhafızları katakompları bastı! Sadakatiniz için +25 Prestij ve 35 Altın ödül verildi."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "mysterious_traveler_$day",
                title = "İskenderiyeli Filozof & Savaş Düşünürü",
                subtitle = "Ludus Kütüphanesi & Revakları",
                type = BetweenCycleEventType.MYSTERIOUS_TRAVELER,
                dayOccurred = day,
                locationName = "Ludus Avlusu",
                narrativeText = "Atina ve İskenderiye akademilerinde eğitim görmüş bilge bir filozof kapınızı çaldı. Stoacı zihin kontrolü, antik savaş stratejileri ve gladyatörlerin psikolojik direnci hakkında rehberlik teklif ediyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "philosopher_reflex_coaching",
                        title = "Gladyatöre Zihin & Refleks Dersi (-35 🪙)",
                        description = "Seçilen dövüşçüye odaklanma ve zihinsel çeviklik kazandır (+4 AGI & +40 XP).",
                        icon = "🧠",
                        goldCost = 35,
                        targetGladiatorStatBonus = "+4 Çeviklik (AGI)",
                        targetStatType = "AGI",
                        statAmount = 4,
                        narrativeOutcome = "Filozofun nefes ve refleks dersleri gladyatörün çevikliğini zirveye taşıdı! (+4 AGI & +40 XP)."
                    ),
                    EventDecisionChoice(
                        id = "philosopher_stoic_speech",
                        title = "Kadroya Stoacı Savaş Felsefesi Semineri (-25 🪙)",
                        description = "Ölüm korkusunu silen felsefi hitabet (+35 Moral & +25 Kadro XP).",
                        icon = "📜",
                        goldCost = 25,
                        moraleChange = 35,
                        teamXpReward = 25,
                        narrativeOutcome = "Filozofun onur ve kader konuşması kışladaki tüm savaşçıları derinden etkiledi! (+35 Moral & +25 XP)."
                    ),
                    EventDecisionChoice(
                        id = "philosopher_spy_advisor",
                        title = "Taktik Danışman Olarak Görevlendir (-20 🪙)",
                        description = "Rakip okulların zayıflıklarını haritalandır (+15 Prestij & Casusluk Buff'ı).",
                        icon = "🎯",
                        goldCost = 20,
                        prestigeReward = 15,
                        activatesScoutBonus = true,
                        narrativeOutcome = "Filozof rakip dövüşçülerin zaaflarını analiz etti! (+15 Prestij ve Casusluk Buff'ı aktif)."
                    ),
                    EventDecisionChoice(
                        id = "philosopher_pass",
                        title = "Teşekkür Edip Uğurla (0 🪙)",
                        description = "Kibar bir vedayla filozofun yoluna devam etmesini sağla (+5 Prestij).",
                        icon = "🕊️",
                        prestigeReward = 5,
                        narrativeOutcome = "Filozof size hayır duaları ederek Roma yoluna koyuldu (+5 Prestij)."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "black_market_beasts_$day",
                title = "Numidya Vahşi Yırtıcı Simsarı",
                subtitle = "Şehir Dışı Hayvan Kafesleri",
                type = BetweenCycleEventType.BLACK_MARKET_BEASTS,
                dayOccurred = day,
                locationName = "Kuzey Kapısı Karavan Alanı",
                narrativeText = "Atlas Dağları'ndan getirdiği yırtıcı kurtlar, Numidya aslanları ve Mısır engerekleriyle bir hayvan simsarı geldi. Dövüşçüleri vahşi doğaya karşı eğitme ve zehir sağma imkanı sunuyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "beasts_predator_drills",
                        title = "Kafesli Aslan & Kurt Tatbikatı (-50 🪙)",
                        description = "Dövüşçüleri yırtıcıların pençelerine karşı talime sok (+45 Kadro XP & +20 Moral).",
                        icon = "🦁",
                        goldCost = 50,
                        teamXpReward = 45,
                        moraleChange = 20,
                        narrativeOutcome = "Gladyatörler kükreyen aslanlar karşısında kalkan duvarı kurmayı başardı! (+45 XP & +20 Moral)."
                    ),
                    EventDecisionChoice(
                        id = "beasts_viper_venom",
                        title = "Engerek Zehri Sağımı & Bıçak Kaplaması (-35 🪙)",
                        description = "Sıradaki maçta rakip şampiyonu zehirle zayıflatır (%25 Can İndirimi).",
                        icon = "🐍",
                        goldCost = 35,
                        activatesPoison = true,
                        narrativeOutcome = "Ölümcül engerek zehri kılıç kınına sürüldü! Sıradaki maçta rakip %25 zayıflamış olacak."
                    ),
                    EventDecisionChoice(
                        id = "beasts_carnivore_diet",
                        title = "Hedef Gladyatöre Özel Yırtıcı Eti Kürü (-40 🪙)",
                        description = "Seçilen dövüşçüye yüksek proteinli vahşi et diyeti (+3 STA & +25 Max HP).",
                        icon = "🥩",
                        goldCost = 40,
                        targetGladiatorStatBonus = "+3 Dayanıklılık (STA) & +25 HP",
                        targetStatType = "STA",
                        statAmount = 3,
                        narrativeOutcome = "Yırtıcı eti kürü seçilen gladyatörün kas kütlesini güçlendirdi! (+3 STA & +25 Max HP)."
                    ),
                    EventDecisionChoice(
                        id = "beasts_pass",
                        title = "Simsarı Reddet (0 🪙)",
                        description = "Vahşi hayvanlarla ilgilenmeden kışlaya dön.",
                        icon = "🛑",
                        narrativeOutcome = "Simsar kafesleriyle birlikte başka bir ludus'a doğru yola çıktı."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "senate_banquet_$day",
                title = "Senatör Flaccus'un Villa Gösterisi",
                subtitle = "Capua Tepeleri Patrician Villası",
                type = BetweenCycleEventType.SENATORIAL_PATRONAGE,
                dayOccurred = day,
                locationName = "Patrician Villa Bahçeleri",
                narrativeText = "Nüfuzlu Senatör Flaccus, villasındaki Roma asilleri ziyafeti için en gözde gladyatörünüzü özel bir şov düellosuna davet ediyor. Karşılığında büyük altın ve senato himayesi vadediyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "senate_private_duel",
                        title = "Seçilen Gladyatörü Şov Düellosuna Sür",
                        description = "Kazanç: +120 Altın, +30 Prestij ve 3 Günlük Senatör Sponsorluğu (Gladyatörde +30 Yorgunluk).",
                        icon = "👑",
                        goldReward = 120,
                        prestigeReward = 30,
                        fatigueChange = 30,
                        targetGladiatorStatBonus = "+25 XP Asil Tecrübesi",
                        narrativeOutcome = "Gladyatörünüzün asiller önündeki zarif kılıç şovu ayakta alkışlandı! +120 Altın, +30 Prestij ve Senato Sponsorluğu kazandınız."
                    ),
                    EventDecisionChoice(
                        id = "senate_bodyguard_crew",
                        title = "Tüm Kadroyla Ziyafet Koruyucusu Ol (-15 🪙 Masraf)",
                        description = "Asillerin ziyafetini koruyun. +70 Altın ve tüm kadroya +25 Moral.",
                        icon = "🛡️",
                        goldCost = 15,
                        goldReward = 70,
                        moraleChange = 25,
                        narrativeOutcome = "Dövüşçüleriniz ziyafetin güvenliğini sağlarken zengin artıklarla beslendi. Kasaya +55 net Altın ve +25 Moral girdi."
                    ),
                    EventDecisionChoice(
                        id = "senate_decline_rest",
                        title = "Daveti Reddet & Arenaya Odaklan (0 🪙)",
                        description = "Dövüşçüleri dinlendir. Tüm kadroda -20 Yorgunluk ve odaklanma artışı.",
                        icon = "🛌",
                        fatigueChange = -20,
                        moraleChange = 10,
                        narrativeOutcome = "Politik oyunları geri çevirip dinlenmeyi seçtiniz. Gladyatörlerinizin yorgunluğu düştü, turnuvaya hazırlandılar."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "temple_mars_$day",
                title = "Savaş Tanrısı Mars Tapınağında Kan Kurbanı",
                subtitle = "Mars Gradivus Altarı",
                type = BetweenCycleEventType.TEMPLE_SACRIFICE,
                dayOccurred = day,
                locationName = "Mars Gradivus Tapınağı",
                narrativeText = "Yaklaşan turnuva öncesi Flamen rahipleri Mars adına kutsama töreni düzenliyor. Altar üzerindeki kurbanların sıradaki karşılaşmada ölümcül sakatlıklardan koruduğuna ve kritik darbeleri artırdığına inanılıyor.",
                requiresTargetGladiator = false,
                choices = listOf(
                    EventDecisionChoice(
                        id = "mars_blessing_sacrifice",
                        title = "Kutsal Boğa Kurbanı & Zırh Takdisi (-45 🪙)",
                        description = "Mars'ın İlahi Kutsaması: Sıradaki dövüşte sakatlanma riski sıfırlanır ve kritik hasar şansı +%20 artar!",
                        icon = "🔥",
                        goldCost = 45,
                        activatesMarsBlessing = true,
                        moraleChange = 25,
                        narrativeOutcome = "Mars'ın alevi sunakta parıldadı! Dövüşçünüz sıradaki karşılaşmada Mars'ın zırhıyla korunacak (+%20 Kritik & Sakatlık Koruması)."
                    ),
                    EventDecisionChoice(
                        id = "mars_blood_oath",
                        title = "Gladyatörlerin Kan Yemini Töreni (-20 🪙)",
                        description = "Dövüşçüler Mars sunağında ant içer. Tüm kadroya +35 Moral ve +25 XP.",
                        icon = "⚔️",
                        goldCost = 20,
                        moraleChange = 35,
                        teamXpReward = 25,
                        narrativeOutcome = "Kılıçlarını sunak alevine sokan gladyatörler zafer yemini etti! +35 Moral ve +25 XP kazanıldı."
                    ),
                    EventDecisionChoice(
                        id = "mars_dedicate_triumph",
                        title = "Son Zaferi Mars'a Adayarak Adak Sun (0 🪙)",
                        description = "Zafer ganimetlerini tapınak rahipleriyle paylaş (+20 Prestij, +15 Moral).",
                        icon = "🏆",
                        prestigeReward = 20,
                        moraleChange = 15,
                        narrativeOutcome = "Halk tapınaktaki adaklarınızı görerek ludus'un dindarlığını övdü (+20 Prestij)."
                    ),
                    EventDecisionChoice(
                        id = "mars_silent_prayer",
                        title = "Sessiz Saygı Duruşu (0 🪙)",
                        description = "Basit bir dua ile kışlaya dön (+10 Moral).",
                        icon = "🙏",
                        moraleChange = 10,
                        narrativeOutcome = "Tapınakta sessizce dua edildi. Kadro huzurla kışlaya döndü."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "blacksmith_guild_$day",
                title = "Vulcan Demirci Loncası Ustası Ziyareti",
                subtitle = "Ludus Demir Dökümhanesi",
                type = BetweenCycleEventType.BLACKSMITH_GUILD,
                dayOccurred = day,
                locationName = "Demirciler Mahallesi",
                narrativeText = "Roma Lejyonlarına zırh döven usta bir demirci atölyenizi ziyaret etti. Gladyatörlerinizin kalkanlarını güçlendirme, kılıçlara jilet gibi keskinlik verme ve zırh revizyonu yapma teklifinde bulunuyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "smith_bronze_armor",
                        title = "Güçlendirilmiş Bronz Göğüs Zırhı (-50 🪙)",
                        description = "Seçilen gladyatörün zırhını ve savunmasını geliştir (+3 STA & Zırh Seviyesi Artışı).",
                        icon = "🛡️",
                        goldCost = 50,
                        targetGladiatorStatBonus = "+3 Dayanıklılık (STA)",
                        targetStatType = "STA",
                        statAmount = 3,
                        narrativeOutcome = "Demirci usta gladyatörün göğüs zırhını çelik perçinlerle takviye etti! (+3 STA & Zırh Seviyesi)."
                    ),
                    EventDecisionChoice(
                        id = "smith_sharpen_all",
                        title = "Tüm Ludus Arsenali Bilet (-30 🪙)",
                        description = "Kışladaki tüm gladius, mızrak ve baltaları jilet gibi bile (+15 Kritik Buff & +20 Kadro XP).",
                        icon = "🗡️",
                        goldCost = 30,
                        activatesSharpenedWeapons = true,
                        teamXpReward = 20,
                        narrativeOutcome = "Tüm silahlar bileğitaşından geçirildi! Sıradaki dövüşte +%15 Kritik Buff aktif oldu."
                    ),
                    EventDecisionChoice(
                        id = "smith_ceremonial_helm",
                        title = "Altın İşlemeli Gösteri Miğferi Yaptır (-40 🪙)",
                        description = "Arenada göz kamaştıracak gösterişli bir miğfer sipariş et (+35 Prestij).",
                        icon = "👑",
                        goldCost = 40,
                        prestigeReward = 35,
                        narrativeOutcome = "Göz kamaştırıcı altın yaldızlı miğfer arenada büyük yankı uyandıracak (+35 Prestij)."
                    ),
                    EventDecisionChoice(
                        id = "smith_pass",
                        title = "Teşekkür Et ve Gönder (0 🪙)",
                        description = "Masraf yapmadan atölyeyi kapat.",
                        icon = "🚪",
                        narrativeOutcome = "Demirci usta sipariş alamadan çarşıya geri döndü."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "eastern_caravan_$day",
                title = "İskenderiye & Doğu Simyacı Kervanı",
                subtitle = "Ostia Yolu Kervansarayı",
                type = BetweenCycleEventType.FOREIGN_CARAVAN,
                dayOccurred = day,
                locationName = "Doğu Ticaret Yolu",
                narrativeText = "Mısır ve Mezopotamya'dan gelen simyacılar; antik şifa yağları, Sparta askeri parşömenleri ve dinlendirici bitki özleri satıyor.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "caravan_healing_elixirs",
                        title = "İskenderiye İksir Sandığı (-45 🪙)",
                        description = "Tüm yaralı gladyatörlerin iyileşme günlerini 2 gün kısaltır ve canlarını fuller.",
                        icon = "🌿",
                        goldCost = 45,
                        healsInjuriesDays = 2,
                        narrativeOutcome = "Mısır şifa merhemleri yaraları anında kapattı! İyileşme süreleri 2 gün kısaldı ve canlar tazelendi."
                    ),
                    EventDecisionChoice(
                        id = "caravan_spartan_scrolls",
                        title = "Sparta Muharebe Papirüsleri (-40 🪙)",
                        description = "Seçilen dövüşçüye antik teknikleri öğret (+3 AGI & +35 XP).",
                        icon = "📜",
                        goldCost = 40,
                        targetGladiatorStatBonus = "+3 Çeviklik (AGI)",
                        targetStatType = "AGI",
                        statAmount = 3,
                        narrativeOutcome = "Antik Sparta papirüslerindeki savuşturma taktikleri seçilen gladyatörün çevikliğini artırdı! (+3 AGI & +35 XP)."
                    ),
                    EventDecisionChoice(
                        id = "caravan_relaxing_oils",
                        title = "Doğu Kokuları ve Kas Masaj Yağları (-25 🪙)",
                        description = "Tüm gladyatörlerin yorgunluğunu siler, +30 Moral verir.",
                        icon = "🌸",
                        goldCost = 25,
                        moraleChange = 30,
                        fatigueChange = -100,
                        narrativeOutcome = "Lavanta ve mür ağacı yağları kasları gevşetti. Tüm yorgunluk sıfırlandı, +30 Moral."
                    ),
                    EventDecisionChoice(
                        id = "caravan_pass",
                        title = "Kervanı Uğurla (0 🪙)",
                        description = "Harcama yapmadan geç.",
                        icon = "🐫",
                        narrativeOutcome = "Kervan doğuya doğru yoluna devam etti."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "barracks_unrest_$day",
                title = "Gladyatör Kışlasında İsyan Fısıltısı",
                subtitle = "Ludus Hücreleri ve Avlu",
                type = BetweenCycleEventType.REBEL_CONSPIRACY,
                dayOccurred = day,
                locationName = "Ludus Kışla Koğuşu",
                narrativeText = "Gece yarısı kışladan fısıltılar yükseliyor. Bazı savaşçılar zorlu antrenmanlardan ve ağır şartlardan ötürü huzursuzluk içinde.",
                requiresTargetGladiator = false,
                choices = listOf(
                    EventDecisionChoice(
                        id = "unrest_feast_wine",
                        title = "Erzak Payını Artır & Birlikte Kadeh Kaldır (-25 🪙)",
                        description = "Kardeşlik ve güven tazelenir: +40 Moral ve tam sadakat.",
                        icon = "🍖",
                        goldCost = 25,
                        moraleChange = 40,
                        narrativeOutcome = "Dövüşçülerle bir arada yemek yediniz, adaletli tavrınız isyanı sevgiye dönüştürdü (+40 Moral)."
                    ),
                    EventDecisionChoice(
                        id = "unrest_night_drills",
                        title = "Doctore ile Gece Tatbikatı Yaptır (0 🪙)",
                        description = "Disiplin eğitimi ver (+30 XP, -10 Moral).",
                        icon = "🥋",
                        teamXpReward = 30,
                        moraleChange = -10,
                        narrativeOutcome = "Doctore tüm kışlayı gece boyunca talime soktu. Disiplin korundu ve +30 XP kazanıldı."
                    ),
                    EventDecisionChoice(
                        id = "unrest_freedom_promise",
                        title = "Özgürlük Sözü Ver (Promise of Freedom)",
                        description = "Arenada şampiyonluk kazananlara azad sözü ver (+45 Moral).",
                        icon = "🕊️",
                        moraleChange = 45,
                        narrativeOutcome = "Verdiğiniz özgürlük vaadi gözlerdeki kıvılcımı yeniden yaktı! Dövüşçülerin morali zirveye çıktı (+45 Moral)."
                    )
                )
            ),
            BetweenCycleEvent(
                id = "underground_pit_$day",
                title = "Subura Yeraltı Gizli Bahis Çukuru",
                subtitle = "Taverna Arkasındaki Yasa Dışı Arena",
                type = BetweenCycleEventType.UNDERGROUND_FIGHT,
                dayOccurred = day,
                locationName = "Subura Katakompları",
                narrativeText = "Subura'nın yerel bahis baronları, Roma muhafızlarından gizli yeraltı çukurunda yüksek bahisli bir dövüş düzenliyor. Seçilen bir gladyatörü sokarak büyük altın kazanabilir veya tellallara rüşvet vererek halkın desteğini arkamıza alabiliriz.",
                requiresTargetGladiator = true,
                choices = listOf(
                    EventDecisionChoice(
                        id = "pit_gladiator_duel",
                        title = "Seçilen Gladyatörü Çukura Sür (-20 🪙 Kayıt)",
                        description = "Gizli düelloda dövüş: +135 Altın ve +35 XP (%20 muhafız baskını riski: 30 🪙 ceza).",
                        icon = "💀",
                        goldCost = 20,
                        goldReward = 135,
                        teamXpReward = 35,
                        fatigueChange = 25,
                        targetGladiatorStatBonus = "+35 XP Yeraltı Tecrübesi",
                        riskChancePercent = 20,
                        riskFailureNarrative = "Muhafızlar çukuru bastı! Muhafız yüzbaşısına 30 Altın rüşvet verip dövüşçünüzü kaçırdınız.",
                        narrativeOutcome = "Gladyatörünüz yeraltı çukurundaki vahşi rakibini devirdi! Kasaya net +115 Altın girdi ve tecrübe kazanıldı."
                    ),
                    EventDecisionChoice(
                        id = "pit_town_criers_hype",
                        title = "Tellallara Bahis Rüşveti Verip Hype'ı Coştur (-25 🪙)",
                        description = "Sıradaki resmi maçta arena tribünlerini hazırlat (%80 Başlangıç Hype Desteği).",
                        icon = "📢",
                        goldCost = 25,
                        activatesCrowdHype = true,
                        narrativeOutcome = "Subura tellalları gladyatörünüzün namını tüm Roma'ya yaydı! Sıradaki maçta %80 başlangıç hype aktif oldu."
                    ),
                    EventDecisionChoice(
                        id = "pit_walk_away",
                        title = "Yasa Dışı Çukurdan Uzak Dur (0 🪙)",
                        description = "Kanunsuz işlere bulaşmadan kışlaya dön (+10 Prestij).",
                        icon = "🏛️",
                        prestigeReward = 10,
                        narrativeOutcome = "Onurlu bir lanista olarak yeraltı çukurlarından uzak durdunuz (+10 Prestij)."
                    )
                )
            )
        )

        return if (forceRandom) {
            events.random()
        } else {
            events[(day + cityTier.tierNumber * 2) % events.size]
        }
    }

    data class EventExecutionResult(
        val updatedGladiators: List<Gladiator>,
        val newGladiatorToInsert: Gladiator? = null,
        val updatedState: LudusState,
        val resolutionResult: EventResolutionResult
    )

    fun resolveBetweenCycleEvent(
        event: BetweenCycleEvent,
        choice: EventDecisionChoice,
        targetGladiatorId: Long?,
        currentState: LudusState,
        currentGladiators: List<Gladiator>
    ): EventExecutionResult {
        if (choice.goldCost > 0 && currentState.gold < choice.goldCost) {
            return EventExecutionResult(
                updatedGladiators = currentGladiators,
                updatedState = currentState,
                resolutionResult = EventResolutionResult(
                    title = "Yetersiz Altın",
                    story = "Bu karar için ${choice.goldCost} Altın gereklidir. Kasada yeterli altın yok."
                )
            )
        }

        var newGold = currentState.gold - choice.goldCost + choice.goldReward
        var newPrestige = currentState.prestige + choice.prestigeReward
        var newDebt = currentState.activeDebt
        val poisonActive = currentState.rivalWeakenedByPoison || choice.activatesPoison
        val marsActive = currentState.marsDivineBlessing || choice.activatesMarsBlessing
        val sharpenActive = currentState.sharpenedWeapons || choice.activatesSharpenedWeapons || (choice.targetStatType == "STR")
        val scoutActive = currentState.scoutedEnemyWeakness || choice.activatesScoutBonus
        val hypeActive = currentState.crowdHypeBonus || choice.activatesCrowdHype
        val changesList = mutableListOf<String>()

        if (choice.goldCost > 0) changesList.add("-${choice.goldCost} 🪙 Altın")
        if (choice.goldReward > 0) changesList.add("+${choice.goldReward} 🪙 Altın")
        if (choice.prestigeReward > 0) changesList.add("+${choice.prestigeReward} 🏛️ Prestij")
        if (choice.prestigeReward < 0) changesList.add("${choice.prestigeReward} 🏛️ Prestij")

        // Risk check
        var riskTriggered = false
        var outcomeStory = choice.narrativeOutcome

        if (choice.riskChancePercent > 0 && Random.nextInt(100) < choice.riskChancePercent) {
            riskTriggered = true
            choice.riskFailureNarrative?.let {
                outcomeStory = it
            }
            newGold = max(0, newGold - 30)
            changesList.add("⚠️ Risk patladı! -30 🪙 Ceza")
        }

        // Debt Reduction
        if (choice.reducesDebtAmount > 0 && newDebt > 0) {
            val amountCleared = min(newDebt, choice.reducesDebtAmount)
            newDebt -= amountCleared
            changesList.add("🔥 -$amountCleared 🪙 Borç Silindi")
        }

        // Process existing gladiators
        var shadowFighter: Gladiator? = null

        val updatedGladiators = currentGladiators.map { g ->
            var updated = g

            // Team-wide changes
            if (choice.moraleChange != 0) {
                updated = updated.copy(mor = max(10, min(100, updated.mor + choice.moraleChange)))
            }
            if (choice.fatigueChange != 0) {
                updated = updated.copy(fatigue = max(0, min(100, updated.fatigue + choice.fatigueChange)))
            }
            if (choice.teamXpReward > 0) {
                updated = updated.copy(experience = updated.experience + choice.teamXpReward)
            }
            if (choice.healsInjuriesDays > 0) {
                val newDays = max(0, updated.recoveryDaysLeft - choice.healsInjuriesDays)
                val healed = newDays == 0
                updated = updated.copy(
                    recoveryDaysLeft = newDays,
                    isInjured = !healed,
                    currentHp = if (healed) updated.maxHp else min(updated.maxHp, updated.currentHp + 30)
                )
            }

            // Target Gladiator Specific Buffs
            if (targetGladiatorId != null && updated.id == targetGladiatorId) {
                when (choice.targetStatType) {
                    "STR" -> {
                        updated = updated.copy(
                            str = updated.str + choice.statAmount,
                            weaponLevel = updated.weaponLevel + 1,
                            experience = updated.experience + 25
                        )
                        changesList.add("${updated.name}: +${choice.statAmount} STR & Silah Seviyesi")
                    }
                    "AGI" -> {
                        updated = updated.copy(
                            agi = updated.agi + choice.statAmount,
                            experience = updated.experience + 35
                        )
                        changesList.add("${updated.name}: +${choice.statAmount} AGI & +35 XP")
                    }
                    "STA" -> {
                        updated = updated.copy(
                            sta = updated.sta + choice.statAmount,
                            maxHp = updated.maxHp + (choice.statAmount * 5)
                        )
                        changesList.add("${updated.name}: +${choice.statAmount} STA & +${choice.statAmount * 5} Max HP")
                    }
                    "MAX_HP" -> {
                        updated = updated.copy(
                            maxHp = updated.maxHp + choice.statAmount,
                            currentHp = min(updated.maxHp + choice.statAmount, updated.currentHp + choice.statAmount)
                        )
                        changesList.add("${updated.name}: +${choice.statAmount} Max HP")
                    }
                }
                if (choice.id == "senate_private_duel" || choice.id == "pit_gladiator_duel") {
                    updated = updated.copy(experience = updated.experience + 30, fatigue = min(100, updated.fatigue + 25))
                }
            }
            updated
        }

        if (choice.moraleChange > 0) changesList.add("+${choice.moraleChange} Kadro Morali")
        if (choice.fatigueChange < 0) changesList.add("Yorgunluk Silindi")
        if (choice.teamXpReward > 0) changesList.add("+${choice.teamXpReward} Kadro XP")
        if (choice.activatesPoison || poisonActive) changesList.add("☠️ Rakibe Belladonna Zehri Aktif")
        if (choice.activatesMarsBlessing || marsActive) changesList.add("🔥 Mars İlahi Kutsaması Aktif")
        if (choice.activatesSharpenedWeapons || sharpenActive) changesList.add("🗡️ Bilenmiş Silahlar Aktif")
        if (choice.activatesScoutBonus || scoutActive) changesList.add("🎯 Casusluk Zayıflık Tespiti Aktif")
        if (choice.activatesCrowdHype || hypeActive) changesList.add("📢 Tellal Arena Hype Desteği Aktif")

        // Recruit Shadow Gladiator if chosen
        if (choice.recruitsShadowGladiator && currentGladiators.size < currentState.maxGladiatorSlots) {
            shadowFighter = Gladiator(
                id = 0,
                name = "Brennus",
                nickname = "Gölge Berserker",
                origin = "Kelt Dağları",
                gladiatorClass = GladiatorClass.DIMACHAERUS,
                contractType = GladiatorContractType.SLAVE,
                dailySalary = 0,
                priceValue = 280,
                str = 18,
                agi = 16,
                sta = 17,
                mor = 90,
                age = 24,
                fatigue = 0,
                experience = 50,
                currentHp = 160,
                maxHp = 160,
                weaponLevel = 2,
                armorLevel = 1,
                personalityTrait = GladiatorTrait.BLOODTHIRSTY
            )
            changesList.add("⚔️ Yeni Savaşçı: Brennus (Kelt Berserker) Kadroya Katıldı!")
        }

        val updatedState = currentState.copy(
            gold = newGold,
            prestige = newPrestige,
            activeDebt = newDebt,
            rivalWeakenedByPoison = poisonActive,
            marsDivineBlessing = marsActive,
            sharpenedWeapons = sharpenActive,
            scoutedEnemyWeakness = scoutActive,
            crowdHypeBonus = hypeActive
        )

        return EventExecutionResult(
            updatedGladiators = updatedGladiators,
            newGladiatorToInsert = shadowFighter,
            updatedState = updatedState,
            resolutionResult = EventResolutionResult(
                title = event.title,
                story = outcomeStory,
                wasRiskTriggered = riskTriggered,
                summaryChanges = changesList
            )
        )
    }
}
