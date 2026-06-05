package com.example.model

data class Player(
    val id: String,
    val name: String,
    val number: Int,
    val position: String,
    val bio: String,
    val birthDate: String,
    val height: String,
    val weight: String,
    val careerHistory: List<String>,
    val achievements: List<String>,
    // Season stats:
    val matches: Int,
    val goals: Int,
    val assists: Int,
    val cleanSheets: Int,
    val rating: Double,
    val shotAccuracy: String = "84%",
    val passesCompleted: String = "91%"
)

data class News(
    val id: String,
    val title: String,
    val category: String, // "MATCH REPORT", "TRANSFER", "EXCLUSIVE", "NEWS"
    val summary: String,
    val body: String,
    val date: String,
    val readTime: String,
    val isPremium: Boolean = false
)

data class LiveCommentary(
    val minute: Int,
    val message: String,
    val isKeyMoment: Boolean = false
)

data class MatchStats(
    val possessionHome: Int,
    val possessionAway: Int,
    val shotsHome: Int,
    val shotsAway: Int,
    val cornersHome: Int,
    val cornersAway: Int,
    val yellowCardsHome: Int,
    val yellowCardsAway: Int,
    val redCardsHome: Int,
    val redCardsAway: Int,
    val xGHome: Double = 1.84,
    val xGAway: Double = 1.12
)

data class Match(
    val id: String,
    val opponent: String,
    val dateText: String,
    val timeText: String,
    val isHome: Boolean,
    val logoText: String, // Crest abbreviation
    val competition: String, // "Premier League", "Champions League"
    val scoreHome: Int?,
    val scoreAway: Int?,
    val status: String, // "UPCOMING", "LIVE", "COMPLETED"
    val liveMinute: Int = 0,
    val stats: MatchStats = MatchStats(54, 46, 12, 8, 6, 3, 1, 2, 0, 0),
    val commentary: List<LiveCommentary> = emptyList(),
    val lineupsHome: List<String> = emptyList(),
    val lineupsAway: List<String> = emptyList(),
    val formHome: List<String> = listOf("W", "W", "D", "W", "L")
)

data class Video(
    val id: String,
    val title: String,
    val duration: String,
    val category: String, // "HIGHLIGHTS", "INTERVIEW", "FEATURES", "LIVE"
    val views: String,
    val thumbnailLabel: String
)

data class ShopItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String, // "KITS", "TRAINING", "RETRO", "MEMORABILIA"
    val description: String,
    val sizes: List<String> = listOf("S", "M", "L", "XL", "XXL"),
    val rating: Double = 4.8
)

data class Trophy(
    val title: String,
    val count: Int,
    val years: String,
    val description: String
)

data class CommunityPoll(
    val id: String,
    val question: String,
    val options: List<String>,
    val votes: List<Int>,
    val userVote: Int? = null,
    val isCompleted: Boolean = false
)

data class Ticket(
    val id: String,
    val matchOpponent: String,
    val matchDate: String,
    val competition: String,
    val section: String,
    val row: String,
    val seat: String,
    val price: Double,
    val qrCodeId: String,
    val isVIP: Boolean = false
)
