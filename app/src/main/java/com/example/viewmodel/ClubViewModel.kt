package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.network.GeminiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClubViewModel : ViewModel() {

    // --- News State ---
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()

    // --- Player State ---
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()
    
    private val _selectedPlayer = MutableStateFlow<Player?>(null)
    val selectedPlayer: StateFlow<Player?> = _selectedPlayer.asStateFlow()

    // --- Matchday & Fixtures ---
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _activeLiveMatch = MutableStateFlow<Match?>(null)
    val activeLiveMatch: StateFlow<Match?> = _activeLiveMatch.asStateFlow()

    // --- Club TV State ---
    private val _tvVideos = MutableStateFlow<List<Video>>(emptyList())
    val tvVideos: StateFlow<List<Video>> = _tvVideos.asStateFlow()

    // --- Shop State ---
    private val _shopItems = MutableStateFlow<List<ShopItem>>(emptyList())
    val shopItems: StateFlow<List<ShopItem>> = _shopItems.asStateFlow()

    private val _cart = MutableStateFlow<Map<ShopItem, Int>>(emptyMap())
    val cart: StateFlow<Map<ShopItem, Int>> = _cart.asStateFlow()

    // --- Ticketing State ---
    private val _purchasedTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val purchasedTickets: StateFlow<List<Ticket>> = _purchasedTickets.asStateFlow()
    
    // --- Membership State ---
    private val _loyaltyPoints = MutableStateFlow(4250)
    val loyaltyPoints: StateFlow<Int> = _loyaltyPoints.asStateFlow()
    
    private val _memberCardNumber = MutableStateFlow("AR-9954-2026")
    val memberCardNumber: StateFlow<String> = _memberCardNumber.asStateFlow()

    // --- Community state ---
    private val _polls = MutableStateFlow<List<CommunityPoll>>(emptyList())
    val polls: StateFlow<List<CommunityPoll>> = _polls.asStateFlow()

    // --- AI Chat assistant state ---
    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(
        listOf("model" to "Greetings from Apex Reds HQ! 🔴 I am your local AI Club Assistant. Ask me anything about tickets, kits, players, or club history!")
    )
    val chatMessages: StateFlow<List<Pair<String, String>>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- Admin state ---
    private val _adminNotificationLogs = MutableStateFlow<List<String>>(emptyList())
    val adminNotificationLogs: StateFlow<List<String>> = _adminNotificationLogs.asStateFlow()

    private val _totalRevenue = MutableStateFlow(128450.0)
    val totalRevenue: StateFlow<Double> = _totalRevenue.asStateFlow()

    private val _appInstalls = MutableStateFlow(1420500)
    val appInstalls: StateFlow<Int> = _appInstalls.asStateFlow()

    // Trophies for History
    val trophies = listOf(
        Trophy("League Champions", 18, "2024, 2022, 2018, 2015, 2011, 2008, 1999...", "The premier top-flight football league championship of the land. We have dominated decades of sports history."),
        Trophy("Champions Cup", 3, "2023, 2019, 2005", "Europe's most prestigious club competition, secured after magical stadium nights under the floodlights."),
        Trophy("FA Trophy Cups", 9, "2024, 2021, 2017, 2012, 2006, 2002...", "The oldest and most storied domestic elimination cup tournament in club history."),
        Trophy("Club World Cup", 10, "2024, 2023, 2019...", "A showcase of world dominance, solidifying our reign on the global stage.")
    )

    init {
        loadInitialMockData()
        startMatchdaySimulator()
    }

    private fun loadInitialMockData() {
        // Mock News
        _newsList.value = listOf(
            News(
                id = "1",
                title = "MARCUS 'APEX' STERLING SIGNS 5-YEAR CONTRACT EXTENSION!",
                category = "EXCLUSIVE",
                summary = "Our legendary number 10 commits his future to the club, pledging to retire as an Apex Reds legend.",
                body = "Our global icon and marquee forward Marcus 'Apex' Sterling has put pen to paper on a new five-year contract that extends his stay at the Apex Arena until June 2031. \n\n\"This is my home,\" Sterling said. \"From the academy to the first team, this club, the coaching staff, and the supporters have given me everything. We have more trophies to win, and I want to be here for every single minute of it.\"\n\nManager Silva added: \"Marcus is the heart of Apex Reds. He represents our speed, our tactical precision, and our relentless winning spirit.\"",
                date = "TODAY",
                readTime = "3 MIN READ",
                isPremium = false
            ),
            News(
                id = "2",
                title = "CHAMPIONS LEAGUE PREVIEW: REVISITING EUROPEAN GLORY UNDER THE LIGHTS",
                category = "MATCH REPORT",
                summary = "The stage is set at the Apex Arena as we look to defend our unbeaten continental home run.",
                body = "Apex Reds prepare to host Madrid in an epic rematch. Under the floodlights, with 85,000 passionate supporters chanting, we expect nothing less than a historic match. Our technical analysts indicate dynamic changes in standard midfield setups.",
                date = "YESTERDAY",
                readTime = "5 MIN READ",
                isPremium = true
            ),
            News(
                id = "3",
                title = "TRANSFER UPDATES: MIDFIELD MAESTRO SPOTTED AT APEX HEADQUARTERS",
                category = "TRANSFER",
                summary = "Speculation intensifies over a record-breaking summer signing to reinforce our defense.",
                body = "Rumors fly as club scouts close in on securing a major playmaker from Milan. The board has indicated our commitment to sustaining top-tier competitive depth.",
                date = "3 DAYS AGO",
                readTime = "2 MIN READ",
                isPremium = false
            ),
            News(
                id = "4",
                title = "WOMEN'S TEAM CLINCH DERBY CUP IN EXTRA TIME THRILLER!",
                category = "NEWS",
                summary = "A dramatic overhead strike by Chelsea Lane in the 118th minute seals the historic silverware.",
                body = "Our Women's squad put on an absolute clinic of determination, bringing back the trophy to loud fan cheers. This marks our fourth major derby title in succession.",
                date = "4 DAYS AGO",
                readTime = "4 MIN READ",
                isPremium = false
            )
        )

        // Mock Players
        val initialPlayers = listOf(
            Player("1", "Marcus Sterling", 10, "Winger / Forward", "Academy graduate, multi-golden boot winner, and international superstar.", "Oct 24, 2001", "1.82 m", "77 kg", listOf("Apex Academy", "Apex Reds First Team"), listOf("Champions Cup 2023", "League MVP 2024"), 34, 24, 12, 0, 8.8),
            Player("2", "Kai van de Beek", 8, "Playmaker / Midfielder", "A technical magician capable of splitting defenses with precision geometric passes.", "Jan 12, 1999", "1.79 m", "72 kg", listOf("Ajax", "Apex Reds"), listOf("League Champion 2022", "Pass Master 2024"), 32, 6, 18, 0, 8.1),
            Player("3", "Alisson Silva", 4, "Captain / Center-Back", "The solid rock of our defense. Respected leader and clean sheet anchor.", "Jun 4, 1995", "1.91 m", "88 kg", listOf("Flamengo", "Milan", "Apex Reds"), listOf("Champions Cup 2023", "Defensive Player of the Year"), 36, 3, 2, 22, 8.5),
            Player("4", "Chelsea Lane", 9, "Forward (Women's Team)", "Dynamic, rapid goal-scorer who spearheaded our Derby Cup championship run.", "Aug 15, 2002", "1.72 m", "63 kg", listOf("Apex Academy", "Apex Reds Women"), listOf("Derby Cup 2026", "Golden Boot 2025"), 28, 22, 8, 0, 8.6),
            Player("5", "Lukas Romero", 1, "Goalkeeping Hero", "Aerially dominant goalkeeper renowned for making spectacular penalty saves.", "Mar 20, 1997", "1.95 m", "92 kg", listOf("Atletico Madrid", "Apex Reds"), listOf("Europa Champ 2021", "Best Goalkeeper 2024"), 35, 0, 0, 17, 8.3)
        )
        _players.value = initialPlayers
        _selectedPlayer.value = initialPlayers.first()

        // Mock Videos (Netflix Style)
        _tvVideos.value = listOf(
            Video("v1", "Apex Reds vs Manchester Blue (3-1) - Full Highlights", "12:15", "HIGHLIGHTS", "8.4M views", "MATCHDAY SHOW"),
            Video("v2", "EXCLUSIVE: Inside Marcus Sterling's Signing Day", "24:30", "FEATURES", "1.2M views", "BEHIND THE SCENES"),
            Video("v3", "Documentary: The Golden Double Of 2024", "1:15:00", "FEATURES", "5M views", "CLUB CINEMA"),
            Video("v4", "Silva's Tactical Clinic: Dissecting the Counter-Press", "15:45", "FEATURES", "450K views", "TRAINING REELS"),
            Video("v5", "Academy Rising Star - Leo Santos Season Recap", "08:12", "REPLAYS", "200K views", "ACADEMY MATCH")
        )

        // Mock Shop Items
        _shopItems.value = listOf(
            ShopItem("s1", "Official 2026/27 Home Armor Kit", 89.99, "KITS", "Our heritage scarlet base weaves together dryline-mesh engineering with gold-gilt badges. Relive the golden era.", rating = 4.9),
            ShopItem("s2", "Gold-Accent Away Kit (Midnight Obsidian)", 89.99, "KITS", "An elegant dark kit accented with bright neon-gold threads.", rating = 4.8),
            ShopItem("s3", "Heritage Retro Jersey (1999 Treble Edition)", 64.99, "RETRO", "A true remake of the iconic deep collar armor worn during our historic final triumph.", rating = 5.0),
            ShopItem("s4", "Squad Training Anthem Jacket", 74.99, "TRAINING", "Heavyweight thermal wear with structured, windproof zippers.", rating = 4.7),
            ShopItem("s5", "Marcus Sterling Signed Heritage Football", 199.99, "MEMORABILIA", "Genuine premium leather football, hand-signed by our iconic No. 10.", rating = 5.0)
        )

        // Mock Fixtures & Results
        _matches.value = listOf(
            Match("m1", "London Gunners", "JUN 12, 2026", "19:00", true, "LDN", "Premier League", null, null, "UPCOMING"),
            Match("m2", "Munich Eagles", "JUN 17, 2026", "20:45", false, "MUN", "Champions League", null, null, "UPCOMING"),
            Match("m3", "Madrid Galacticos", "MAY 29, 2026", "COMPLETED", true, "MAD", "Champions League", 3, 2, "COMPLETED"),
            Match("m4", "Milan Devils", "MAY 22, 2026", "COMPLETED", false, "MIL", "Champions League", 1, 2, "COMPLETED")
        )

        // Mock Tickets
        _purchasedTickets.value = listOf(
            Ticket("t1", "London Gunners", "JUN 12, 2026 - 19:00", "Premier League", "SOCIOS DECK - SECTOR A", "ROW 12", "SEAT 104", 75.0, "APX-TKT-GUNNERS-2026", isVIP = true),
            Ticket("t2", "Munich Eagles", "JUN 17, 2026 - 20:45", "Champions League", "EAST STAND - BLOCK C", "ROW 8", "SEAT 42", 55.0, "APX-TKT-MUNICH-1194", isVIP = false)
        )

        // Mock Community Polls
        _polls.value = listOf(
            CommunityPoll("p1", "Who was your Man of the Match from our last Champions League clash?", listOf("Marcus Sterling", "Kai van de Beek", "Lukas Romero", "Alisson Silva"), listOf(425, 230, 89, 142)),
            CommunityPoll("p2", "Which dynamic color do you prefer for next season's alternative training kit?", listOf("Sunset Gold", "Royal Crimson", "Volt White", "Steel Grey"), listOf(120, 310, 80, 54))
        )
    }

    // --- Matchday Simulation ---
    private fun startMatchdaySimulator() {
        // Initialize active LIVE match on launch
        val initialMatch = Match(
            id = "live_90",
            opponent = "Manchester Blue",
            dateText = "LIVE NOW",
            timeText = "MATCHDAY ACTIVE",
            isHome = true,
            logoText = "MNC",
            competition = "Premier League Derby",
            scoreHome = 2,
            scoreAway = 1,
            status = "LIVE",
            liveMinute = 62,
            stats = MatchStats(54, 46, 14, 9, 6, 4, 1, 3, 0, 0),
            commentary = listOf(
                LiveCommentary(61, "GOAL APEX REDS! Marcus Sterling converts a wonder volley after a stellar overhead lob! Dynamic stadium eruption! ⚽", true),
                LiveCommentary(58, "Yellow Card issued to Manchester Center-Back for a cynical challenge on Kai.", false),
                LiveCommentary(45, "Second-half begins under deafening chants from the East Stand supporters.", false),
                LiveCommentary(32, "Manchester Blue equalizes through a fast lateral transition goal.", false),
                LiveCommentary(12, "GOAL APEX REDS! Alisson Silva scores a towering header off a gold-spun corner! ⚽", true),
                LiveCommentary(1, "Kickoff at a packed Apex Arena with 85,000 supporters singing our Anthem!", false)
            ),
            lineupsHome = listOf("Romero (GK)", "Silva (C)", "Gomez", "Zanetti", "Show", "Kai", "Muller", "Sterling (10)", "Nunes", "Santos", "Santos Jr"),
            lineupsAway = listOf("Aderson (GK)", "Dias", "Walker", "Stones", "Kovcic", "De Gray", "Foden", "Silva B", "Haad", "Docu", "Grelis")
        )
        _activeLiveMatch.value = initialMatch
    }

    fun selectPlayer(player: Player) {
        _selectedPlayer.value = player
    }

    // --- Admin Control Functions to trigger beautiful simulation events ---
    fun adminSimulateMatchGoal() {
        _activeLiveMatch.update { current ->
            current?.let { m ->
                val newScoreHome = (m.scoreHome ?: 0) + 1
                val newMin = if (m.liveMinute < 90) m.liveMinute + 4 else 90
                val newCommentary = listOf(
                    LiveCommentary(newMin, "⚡ GOAL APEX REDS! Beautiful tiki-taka sequence leading to a golden strike in the top corner! Dynamic stadium euphoria! 🔴⚽", true)
                ) + m.commentary
                
                // Increase fan analytics and logs
                _loyaltyPoints.value += 150
                _totalRevenue.value += 4500.0
                addAdminLog("🔴 SIMULATOR: Goal posted! Score is now $newScoreHome - ${m.scoreAway}")

                m.copy(
                    scoreHome = newScoreHome,
                    liveMinute = newMin,
                    commentary = newCommentary,
                    stats = m.stats.copy(
                        possessionHome = 58,
                        shotsHome = m.stats.shotsHome + 2,
                        cornersHome = m.stats.cornersHome + 1
                    )
                )
            }
        }
    }

    fun adminSimulateOpponentGoal() {
        _activeLiveMatch.update { current ->
            current?.let { m ->
                val newScoreAway = (m.scoreAway ?: 0) + 1
                val newMin = if (m.liveMinute < 90) m.liveMinute + 3 else 90
                val newCommentary = listOf(
                    LiveCommentary(newMin, "⚠️ Opponent scores via a deflected shot in extra space. Defenses organizing immediate corrections.", false)
                ) + m.commentary
                addAdminLog("⚠️ SIMULATOR: Opponent Goal posted! Score is now ${m.scoreHome} - $newScoreAway")

                m.copy(
                    scoreAway = newScoreAway,
                    liveMinute = newMin,
                    commentary = newCommentary,
                    stats = m.stats.copy(
                        possessionHome = 49,
                        shotsAway = m.stats.shotsAway + 1
                    )
                )
            }
        }
    }

    fun adminSimulateYellowCard() {
        _activeLiveMatch.update { current ->
            current?.let { m ->
                val newMin = if (m.liveMinute < 90) m.liveMinute + 2 else 90
                val newCommentary = listOf(
                    LiveCommentary(newMin, "🟨 Tactical foul stops a dangerous counter. Yellow Card shown to Apex Reds.", false)
                ) + m.commentary
                addAdminLog("🟨 SIMULATOR: Yellow Card simulation registered.")

                m.copy(
                    liveMinute = newMin,
                    commentary = newCommentary,
                    stats = m.stats.copy(
                        yellowCardsHome = m.stats.yellowCardsHome + 1
                    )
                )
            }
        }
    }

    fun adminPostCustomNews(title: String, body: String, category: String) {
        val newArticle = News(
            id = (newsList.value.size + 1).toString(),
            title = title.uppercase(),
            category = category,
            summary = if (body.length > 80) body.substring(0, 80) + "..." else body,
            body = body,
            date = "JUST NOW",
            readTime = "2 MIN READ",
            isPremium = false
        )
        _newsList.value = listOf(newArticle) + _newsList.value
        _loyaltyPoints.value += 50
        addAdminLog("📢 ADMIN: Published article '$category: ${title.take(30)}'")
    }

    fun adminDispatchPushAlert(message: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logMsg = "[$timeStamp] AirAlert: \"$message\""
        _adminNotificationLogs.value = listOf(logMsg) + _adminNotificationLogs.value
        addAdminLog("📲 PUSH SENT: $message")
    }

    private fun addAdminLog(message: String) {
        // Log is displayed in Admin screen terminal
        Log.d("ClubAdmin", message)
    }

    // --- Fan Interaction & Ecommerce Methods ---
    fun voteInPoll(pollId: String, optionIndex: Int) {
        _polls.update { currentPolls ->
            currentPolls.map { poll ->
                if (poll.id == pollId && poll.userVote == null) {
                    val updatedVotes = poll.votes.toMutableList()
                    updatedVotes[optionIndex] = updatedVotes[optionIndex] + 1
                    _loyaltyPoints.value += 100 // Rewards supporters
                    poll.copy(votes = updatedVotes, userVote = optionIndex)
                } else {
                    poll
                }
            }
        }
    }

    fun addToCart(item: ShopItem) {
        _cart.update { current ->
            val updated = current.toMutableMap()
            updated[item] = (updated[item] ?: 0) + 1
            updated
        }
    }

    fun removeFromCart(item: ShopItem) {
        _cart.update { current ->
            val updated = current.toMutableMap()
            val existing = updated[item] ?: 0
            if (existing <= 1) {
                updated.remove(item)
            } else {
                updated[item] = existing - 1
            }
            updated
        }
    }

    fun checkoutCart() {
        val total = _cart.value.entries.sumOf { it.key.price * it.value }
        if (total > 0.0) {
            _totalRevenue.value += total
            _loyaltyPoints.value += (total * 10).toInt() // 10 points per dollar
            _cart.value = emptyMap()
        }
    }

    fun buyVIPExperience(ticketOptionName: String, price: Double) {
        val randomRow = (1..15).random()
        val randomSeat = (1..200).random()
        val newTkt = Ticket(
            id = "tkt-${System.currentTimeMillis()}",
            matchOpponent = "London Gunners",
            matchDate = "JUN 12, 2026 - 19:00",
            competition = "Premier League Derby",
            section = "VIP SUITE - $ticketOptionName",
            row = "R$randomRow",
            seat = "S$randomSeat",
            price = price,
            qrCodeId = "APX-VIP-${System.currentTimeMillis().toString().takeLast(6)}",
            isVIP = true
        )
        _purchasedTickets.value = listOf(newTkt) + _purchasedTickets.value
        _totalRevenue.value += price
        _loyaltyPoints.value += (price * 15).toInt() // massive loyalty bonus
    }

    // --- Chat AI Interactions ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        
        // Add User Message
        _chatMessages.update { current -> current + ("user" to text) }
        _isChatLoading.value = true

        viewModelScope.launch {
            // Get conversation history format (Pair user/model list)
            val history = _chatMessages.value.dropLast(1) // exclude current turn which was just added
            
            // Artificial typing suspense delay to build tension e.g. "writing..."
            delay(1000)
            
            val aiResponse = GeminiService.chatWithAssistant(text, history)
            
            _isChatLoading.value = false
            _chatMessages.update { current -> current + ("model" to aiResponse) }
            
            // Fans get loyalty points for using the companion assistant!
            _loyaltyPoints.value += 15
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            "model" to "Chat history reset! How else can Apex Reds AI support you, champion?"
        )
    }
}
