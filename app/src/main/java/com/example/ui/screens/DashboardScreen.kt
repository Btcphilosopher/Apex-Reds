package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.ClubViewModel
import kotlinx.coroutines.delay

// High quality tab indicators
enum class ClubTab(val label: String) {
    HOME("Home"),
    MATCHDAY("Live"),
    TV("Club TV"),
    PLAYERS("Squad"),
    TICKETS("Arena"),
    SHOP("Shop"),
    COMMUNITY("Fans"),
    HISTORY("Museum"),
    ASSISTANT("AI Fan"),
    ADMIN("Admin")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ClubViewModel,
    onLogoutClick: (() -> Unit)? = null
) {
    var currentTab by remember { mutableStateOf(ClubTab.HOME) }
    var showActiveSplash by remember { mutableStateOf(true) }
    var selectedNewsItem by remember { mutableStateOf<News?>(null) }
    var showNotificationsPanel by remember { mutableStateOf(false) }

    val newsList by viewModel.newsList.collectAsState()
    val players by viewModel.players.collectAsState()
    val activeMatch by viewModel.activeLiveMatch.collectAsState()
    val tvVideos by viewModel.tvVideos.collectAsState()
    val shopItems by viewModel.shopItems.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val purchasedTickets by viewModel.purchasedTickets.collectAsState()
    val loyaltyPoints by viewModel.loyaltyPoints.collectAsState()
    val polls by viewModel.polls.collectAsState()
    
    // Quick Splash Playback Simulation
    if (showActiveSplash) {
        SplashLauncher {
            showActiveSplash = false
        }
    } else {
        // Main Immersive View Scaffolding
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Gold Pulsing Crest Badge
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(ClubScarlet)
                                    .border(1.5.dp, GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "A",
                                    color = GoldAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "APEX REDS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { currentTab = ClubTab.ADMIN },
                            modifier = Modifier.testTag("nav_admin_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Matchday Sim Console",
                                tint = GoldAccent
                            )
                        }
                    },
                    actions = {
                        // Loyalty Points indicator click takes to Fans tab
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { currentTab = ClubTab.COMMUNITY }
                                .background(GoldGlow)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Points",
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "$loyaltyPoints PTS",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        IconButton(
                            onClick = { showNotificationsPanel = !showNotificationsPanel },
                            modifier = Modifier.testTag("notifications_toolbar_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Club Notifications",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = DarkObsidian
                    )
                )
            },
            bottomBar = {
                // Customized Grid bottom bar to handle 10 navigation flows cleanly
                Column(modifier = Modifier.background(DarkObsidian)) {
                    Divider(color = ClubScarlet.copy(alpha = 0.3f), thickness = 1.dp)
                    
                    // Row 1 Bottom Nav
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TabNavItem(ClubTab.HOME, currentTab, Icons.Default.Home) { currentTab = it }
                        TabNavItem(ClubTab.MATCHDAY, currentTab, Icons.Default.PlayArrow) { currentTab = it }
                        TabNavItem(ClubTab.TV, currentTab, Icons.Default.PlayArrow) { currentTab = it }
                        TabNavItem(ClubTab.PLAYERS, currentTab, Icons.Default.Person) { currentTab = it }
                        TabNavItem(ClubTab.TICKETS, currentTab, Icons.Default.LocationOn) { currentTab = it }
                    }
                    
                    // Row 2 Bottom Nav
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp, top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TabNavItem(ClubTab.SHOP, currentTab, Icons.Default.ShoppingCart) { currentTab = it }
                        TabNavItem(ClubTab.COMMUNITY, currentTab, Icons.Default.Star) { currentTab = it }
                        TabNavItem(ClubTab.HISTORY, currentTab, Icons.Default.Info) { currentTab = it }
                        TabNavItem(ClubTab.ASSISTANT, currentTab, Icons.Default.Send) { currentTab = it }
                        TabNavItem(ClubTab.ADMIN, currentTab, Icons.Default.Settings) { currentTab = it }
                    }
                }
            },
            containerColor = DarkObsidian
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DarkObsidian, ClubVelvetDark.copy(alpha = 0.4f), DarkObsidian)
                        )
                    )
            ) {
                // Crossfade Navigation Content
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                    },
                    label = "TabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        ClubTab.HOME -> HomeScreen(
                            newsList = newsList,
                            activeMatch = activeMatch,
                            onNewsClick = { selectedNewsItem = it },
                            onTicketsPrompt = { currentTab = ClubTab.TICKETS },
                            onShopPrompt = { currentTab = ClubTab.SHOP }
                        )
                        ClubTab.MATCHDAY -> MatchdayScreen(
                            match = activeMatch ?: Match("none", "Opponent", "TBD", "TBD", true, "OPP", "League", 0, 0, "UPCOMING"),
                            viewModel = viewModel
                        )
                        ClubTab.TV -> ClubTVScreen(
                            videos = tvVideos
                        )
                        ClubTab.PLAYERS -> PlayersScreen(
                            players = players,
                            viewModel = viewModel
                        )
                        ClubTab.TICKETS -> TicketingScreen(
                            purchasedTickets = purchasedTickets,
                            viewModel = viewModel
                        )
                        ClubTab.SHOP -> ShopScreen(
                            items = shopItems,
                            cart = cart,
                            viewModel = viewModel
                        )
                        ClubTab.COMMUNITY -> CommunityScreen(
                            polls = polls,
                            activeMatch = activeMatch,
                            viewModel = viewModel
                        )
                        ClubTab.HISTORY -> HistoryMuseumScreen()
                        ClubTab.ASSISTANT -> ClubAssistantScreen(
                            viewModel = viewModel
                        )
                        ClubTab.ADMIN -> AdminSuiteScreen(
                            viewModel = viewModel
                        )
                    }
                }

                // Modal Overlays
                if (selectedNewsItem != null) {
                    NewsDetailDialog(news = selectedNewsItem!!, onDismiss = { selectedNewsItem = null })
                }

                if (showNotificationsPanel) {
                    NotificationsPanel(
                        viewModel = viewModel,
                        onDismiss = { showNotificationsPanel = false }
                    )
                }
            }
        }
    }
}

// --- Dynamic Interactive Splash Screen ---
@Composable
fun SplashLauncher(onFinished: () -> Unit) {
    var startPulse by remember { mutableStateOf(false) }
    val pulseScale by animateFloatAsState(
        targetValue = if (startPulse) 1.2f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    LaunchedEffect(Unit) {
        startPulse = true
        delay(2200) // Beautiful atmospheric launch sequencing
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .testTag("splash_container"),
        contentAlignment = Alignment.Center
    ) {
        // Red velvet stadium ambient aura behind crest
        Box(
            modifier = Modifier
                .size(300.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ClubScarlet.copy(alpha = 0.35f), Color.Transparent),
                            center = center,
                            radius = size.width / 2
                        )
                    )
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-fidelity Club Emblem Drawn in code
            Box(
                modifier = Modifier
                    .size(120.dp * pulseScale)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(3.dp, GoldAccent, CircleShape)
                    .border(6.dp, ClubScarlet.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Gold crown symbol inside crest
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("♛", color = GoldAccent, fontSize = 28.sp)
                    Text("APEX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                "APEX REDS",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                letterSpacing = 4.sp
            )
            
            Text(
                "OFFICIAL DIGITAL PLATFORM",
                color = GoldAccent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))
            LinearProgressIndicator(
                color = ClubScarlet,
                trackColor = CharcoalSurface,
                modifier = Modifier
                    .width(140.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                "Loading Stadium Ambience...",
                color = LightGreyText,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

// --- Home Tab Navigation Bar Item ---
@Composable
fun TabNavItem(
    tab: ClubTab,
    selectedTab: ClubTab,
    fallbackIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (ClubTab) -> Unit
) {
    val isSelected = tab == selectedTab
    val backColor = if (isSelected) ClubScarlet.copy(alpha = 0.2f) else Color.Transparent
    val tintColor = if (isSelected) GoldAccent else LightGreyText

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backColor)
            .clickable { onClick(tab) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("tab_${tab.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = when(tab) {
                    ClubTab.HOME -> Icons.Default.Home
                    ClubTab.MATCHDAY -> Icons.Default.PlayArrow
                    ClubTab.TV -> Icons.Default.PlayArrow
                    ClubTab.PLAYERS -> Icons.Default.Person
                    ClubTab.TICKETS -> Icons.Default.LocationOn
                    ClubTab.SHOP -> Icons.Default.ShoppingCart
                    ClubTab.COMMUNITY -> Icons.Default.Star
                    ClubTab.HISTORY -> Icons.Default.Info
                    ClubTab.ASSISTANT -> Icons.Default.Send
                    ClubTab.ADMIN -> Icons.Default.Settings
                },
                contentDescription = tab.label,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tab.label,
                color = tintColor,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// ==========================================
// SCREEN 1: DYNAMIC HOME SCREEN
// ==========================================
@Composable
fun HomeScreen(
    newsList: List<News>,
    activeMatch: Match?,
    onNewsClick: (News) -> Unit,
    onTicketsPrompt: () -> Unit,
    onShopPrompt: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Personalization Header
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    "Welcome Back, Supporter",
                    color = LightGreyText,
                    fontSize = 14.sp
                )
                Text(
                    "YOU ARE PART OF THE CLUB",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Live/Next Match Hero Banner
        item {
            activeMatch?.let { m ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (m.status == "LIVE") ClubScarlet else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("match_hero_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (m.status == "LIVE") ClubScarlet else LightGreyText)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (m.status == "LIVE") "LIVE MATCHDAY" else m.competition,
                                    color = if (m.status == "LIVE") ClubScarlet else LightGreyText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            if (m.status == "LIVE") {
                                Text(
                                    "MIN ${m.liveMinute}'",
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Score / Versus Layout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Club Logo Block
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(ClubScarlet),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("APX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text("Apex Reds", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Score text
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = m.scoreHome?.toString() ?: "-",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    " : ",
                                    color = GoldAccent,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = m.scoreAway?.toString() ?: "-",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Opponent Logo Block
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(m.logoText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text(m.opponent, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }

                        // Stadium location & date
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Apex Arena (85,000 Seats)",
                                color = LightGreyText,
                                fontSize = 11.sp
                            )
                            Text(
                                "Tickets: VIP Sold Out",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onTicketsPrompt() }
                            )
                        }
                    }
                }
            }
        }

        // Promotional Multi-grid Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onShopPrompt() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CLUB SHOP", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("2026/27 Armor Kits", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Order with 15% discount", color = LightGreyText, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTicketsPrompt() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("RESERVE SEATS", color = ClubScarlet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("3D Stadium Map", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Grab London Tickets", color = LightGreyText, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Latest Club News Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LATEST CLUB CORRESPONDENCE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    "VIEW ALL",
                    color = LightGreyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // News List
        items(newsList) { article ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNewsClick(article) }
                    .testTag("news_item_${article.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Color Band
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(70.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (article.isPremium) GoldAccent else ClubScarlet)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = article.category,
                                color = if (article.isPremium) GoldAccent else ClubScarlet,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = article.readTime,
                                color = LightGreyText,
                                fontSize = 10.sp
                            )
                        }
                        
                        Text(
                            text = article.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Text(
                            text = article.summary,
                            color = LightGreyText,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// ==========================================
// SCREEN 2: MATCHDAY MODE
// ==========================================
@Composable
fun MatchdayScreen(
    match: Match,
    viewModel: ClubViewModel
) {
    var selectedMatchdayTab by remember { mutableStateOf("COMMENTARY") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Broadcaster Quality Score Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, ClubScarlet, RoundedCornerShape(16.dp))
                    .padding(top = 10.dp)
                    .testTag("broadcaster_match_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "LIVE TV BROADCAST SIGNAL",
                        color = ClubScarlet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(ClubScarlet),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("APX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text("Apex Reds", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        // Minute and Live score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ClubScarlet)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "LIVE MIN ${match.liveMinute}'",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(match.scoreHome?.toString() ?: "0", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
                                Text(" - ", color = GoldAccent, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                Text(match.scoreAway?.toString() ?: "0", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
                            }
                            Text("APEX ARENA", color = LightGreyText, fontSize = 10.sp, letterSpacing = 1.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(match.logoText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text(match.opponent, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }

        // Live stats header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MatchdaySubTabButton("COMMENTARY", selectedMatchdayTab) { selectedMatchdayTab = it }
                MatchdaySubTabButton("STATISTICS", selectedMatchdayTab) { selectedMatchdayTab = it }
                MatchdaySubTabButton("LINEUPS", selectedMatchdayTab) { selectedMatchdayTab = it }
            }
        }

        // Dynamic Subviews
        when (selectedMatchdayTab) {
            "COMMENTARY" -> {
                item {
                    Text(
                        "BROADCASTER REACTION COMMENTARY",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
                items(match.commentary) { log ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (log.isKeyMoment) ClubVelvetDark.copy(alpha = 0.6f) else CharcoalSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (log.isKeyMoment) GoldAccent else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (log.isKeyMoment) GoldAccent else ClubScarlet)
                                    .size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${log.minute}'",
                                    color = if (log.isKeyMoment) DarkObsidian else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = log.message,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            "STATISTICS" -> {
                // TV-style live statistical graph of possession, corners, etc.
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSurface)
                            .padding(16.dp)
                    ) {
                        Text(
                            "LIVE STATISTICAL MATRIX",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        StatRow("Possession", match.stats.possessionHome, match.stats.possessionAway, "%")
                        Spacer(modifier = Modifier.height(12.dp))
                        StatRow("Total Shots", match.stats.shotsHome, match.stats.shotsAway)
                        Spacer(modifier = Modifier.height(12.dp))
                        StatRow("Corners", match.stats.cornersHome, match.stats.cornersAway)
                        Spacer(modifier = Modifier.height(12.dp))
                        StatRow("Yellow Cards", match.stats.yellowCardsHome, match.stats.yellowCardsAway)
                        Spacer(modifier = Modifier.height(12.dp))
                        StatRow("Red Cards", match.stats.redCardsHome, match.stats.redCardsAway)
                        Spacer(modifier = Modifier.height(12.dp))
                        StatRow("Expected Goals (xG)", (match.stats.xGHome * 10).toInt(), (match.stats.xGAway * 10).toInt(), divisor = 10f)
                    }
                }

                // Momentum Canvas Graph
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSurface)
                            .padding(16.dp)
                    ) {
                        Text(
                            "MATCHDAY PRESSURE MOMENTUM",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            "Apex pressure vs Opponent. Spline map over length of match.",
                            color = LightGreyText,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Draw momentum graph with Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(0.5.dp, LightGreyText.copy(alpha = 0.3f))
                        ) {
                            val path = Path()
                            val width = size.width
                            val height = size.height
                            val midY = height / 2

                            // Draw baseline
                            drawLine(
                                color = LightGreyText.copy(alpha = 0.4f),
                                start = Offset(0f, midY),
                                end = Offset(width, midY),
                                strokeWidth = 1f
                            )

                            // Define momentum points
                            val momentumPoints = listOf(
                                0.2f to 0.1f,
                                0.25f to -0.4f,
                                0.35f to -0.6f,
                                0.45f to 0.2f,
                                0.55f to -0.2f,
                                0.65f to -0.7f,
                                0.82f to 0.5f,
                                1.0f to -0.8f
                            )

                            path.moveTo(0f, midY)
                            for (p in momentumPoints) {
                                val targetX = p.first * width
                                val targetY = midY + (p.second * midY)
                                path.lineTo(targetX, targetY)
                            }

                            // Draw Spline
                            drawPath(
                                path = path,
                                color = ClubScarlet,
                                style = Stroke(width = 4f)
                            )

                            // Draw high points indicators
                            drawCircle(
                                color = GoldAccent,
                                radius = 6f,
                                center = Offset(width * 0.65f, midY - (0.7f * midY))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0'", color = LightGreyText, fontSize = 10.sp)
                            Text("Halftime Live", color = LightGreyText, fontSize = 10.sp)
                            Text("90' End", color = LightGreyText, fontSize = 10.sp)
                        }
                    }
                }
            }
            "LINEUPS" -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSurface)
                            .padding(16.dp)
                    ) {
                        Text(
                            "TACTICAL LINEUPS (FORM: 4-3-3)",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("APEX REDS (HOME)", color = ClubScarlet, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                match.lineupsHome.forEach { name ->
                                    Text("• $name", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${match.opponent.uppercase()} (AWAY)", color = LightGreyText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                match.lineupsAway.forEach { name ->
                                    Text("• $name", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun StatRow(label: String, valHome: Int, valAway: Int, suffix: String = "", divisor: Float = 1f) {
    val homeScaled = valHome / divisor
    val awayScaled = valAway / divisor
    val total = valHome + valAway
    val homeRatio = if (total > 0) valHome.toFloat() / total else 0.5f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(if (divisor > 1) "$homeScaled$suffix" else "$valHome$suffix", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(label, color = LightGreyText, fontSize = 12.sp)
            Text(if (divisor > 1) "$awayScaled$suffix" else "$valAway$suffix", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Progress double bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(homeRatio.coerceIn(0.05f, 0.95f))
                    .background(ClubScarlet)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - homeRatio).coerceIn(0.05f, 0.95f))
                    .background(LightGreyText)
            )
        }
    }
}

@Composable
fun MatchdaySubTabButton(label: String, selected: String, onClick: (String) -> Unit) {
    val isSel = label == selected
    val textColor = if (isSel) GoldAccent else Color.White
    val borderCol = if (isSel) GoldAccent else Color.Transparent

    Box(
        modifier = Modifier
            .border(1.dp, borderCol, RoundedCornerShape(18.dp))
            .background(if (isSel) ClubScarlet.copy(alpha = 0.15f) else CharcoalSurface)
            .clickable { onClick(label) }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// SCREEN 3: PREMIUM CLUB TV (STREAMING)
// ==========================================
@Composable
fun ClubTVScreen(
    videos: List<Video>
) {
    var activePreviewVideo by remember { mutableStateOf<Video?>(videos.firstOrNull()) }
    var isSimulatedPlaying by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Netflix-style Main Cinematic Player Header
        item {
            activePreviewVideo?.let { v ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        "NETFLIX-QUALITY CLUB STREAMS",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Video Canvas Screen Simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                            .border(1.dp, ClubScarlet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { isSimulatedPlaying = !isSimulatedPlaying }
                            .testTag("video_player_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSimulatedPlaying) {
                            Text("Playing: ${v.title}", color = Color.White, fontWeight = FontWeight.Bold)
                            // Pulse circle indicator
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(ClubScarlet)
                            )
                        } else {
                            // Render atmospheric stadium graphics in code
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(ClubVelvetDark, Color.Black)
                                    )
                                )
                                drawCircle(
                                    color = GoldAccent.copy(alpha = 0.15f),
                                    radius = 120f,
                                    center = center
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "PlayStream",
                                tint = GoldAccent,
                                modifier = Modifier.size(54.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(v.duration, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(v.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${v.category} • ${v.views}", color = LightGreyText, fontSize = 12.sp)
                        Text(
                            if (isSimulatedPlaying) "TAP TO PAUSE REPLAY" else "TAP TO STREAM REPLAY",
                            color = ClubScarlet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Horizontal video categories
        item {
            Text("MATCH REPLAYS & HIGHLIGHTS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(videos.filter { it.category == "HIGHLIGHTS" || it.category == "REPLAYS" }) { video ->
                    VideoSlideRow(video, isSelected = video == activePreviewVideo) {
                        activePreviewVideo = it
                        isSimulatedPlaying = false
                    }
                }
            }
        }

        // Horizontal documentary list
        item {
            Text("CLUB DOCUMENTARIES & INTERVIEWS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(videos.filter { it.category == "FEATURES" }) { video ->
                    VideoSlideRow(video, isSelected = video == activePreviewVideo) {
                        activePreviewVideo = it
                        isSimulatedPlaying = false
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun VideoSlideRow(video: Video, isSelected: Boolean, onClick: (Video) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick(video) }
            .border(
                1.5.dp,
                if (isSelected) GoldAccent else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color.DarkGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = ClubVelvetDark.copy(alpha = 0.5f))
                }
                Text(
                    video.thumbnailLabel,
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    video.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(video.duration, color = LightGreyText, fontSize = 10.sp)
                    Text(video.views, color = LightGreyText, fontSize = 10.sp)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 4: PLAYER HUB & PROFILES
// ==========================================
@Composable
fun PlayersScreen(
    players: List<Player>,
    viewModel: ClubViewModel
) {
    val selectedPlayer by viewModel.selectedPlayer.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Horizontal selectors
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    "OFFICIAL APEX SQUAD",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(players) { p ->
                        val isSel = p == selectedPlayer
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) ClubScarlet else CharcoalSurface)
                                .border(1.dp, if (isSel) GoldAccent else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectPlayer(p) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "No.${p.number} ${p.name.split(" ").last()}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // High Fidelity Player Bio Dashboard Card
        item {
            selectedPlayer?.let { p ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("player_profile_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Player Vector Face Silhouette
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(ClubScarlet)
                                    .border(1.5.dp, GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    p.number.toString(),
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldAccent)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        p.position.uppercase(),
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(p.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Academy Graduate MVP", color = LightGreyText, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = LightGreyText.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // BIO DETAILS Grid
                        Row(modifier = Modifier.fillMaxWidth()) {
                            BioDetail("DOB", p.birthDate, Modifier.weight(1f))
                            BioDetail("Height", p.height, Modifier.weight(1f))
                            BioDetail("Weight", p.weight, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("2025/2026 SEASON PERFORMANCE", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Stats Dashboard Matrix
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatCardMetric("MATCHES", p.matches.toString())
                            StatCardMetric("GOALS", p.goals.toString())
                            StatCardMetric("ASSISTS", p.assists.toString())
                            StatCardMetric("RATING", p.rating.toString(), isGold = true)
                        }

                        // Detailed Bio text
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            p.bio,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("ACCOMPLISHMENTS & SILVERWARE", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            p.achievements.forEach { award ->
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("🏆 $award", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BioDetail(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(label, color = LightGreyText, fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCardMetric(label: String, value: String, isGold: Boolean = false) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.width(74.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = if (isGold) GoldAccent else Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, color = LightGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 5: TICKETING & 3D ARENA EXPERIENCE
// ==========================================
@Composable
fun TicketingScreen(
    purchasedTickets: List<Ticket>,
    viewModel: ClubViewModel
) {
    var selectedStadiumSector by remember { mutableStateOf<String?>("SOCIOS DECK - SECTOR A") }
    var calculatedPrice by remember { mutableStateOf(75.0) }
    var isBookingConfirmedMsg by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    "ARENA TICKETS & WAYFINDING",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "Apex Arena 3D Seat Mapping Simulator",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Beautiful Interactive Stadium Map drawn via Canvas!
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier.fillMaxWidth().testTag("interactive_stadium_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "TAP SECTOR TO BOOK SEAT",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Draw Stadium top view bowl
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    // Map coordinates to simulated sectors
                                    val x = offset.x / size.width
                                    val y = offset.y / size.height
                                    if (y < 0.35f) {
                                        selectedStadiumSector = "NORTH STAND - TIER 2"
                                        calculatedPrice = 50.0
                                    } else if (y > 0.65f) {
                                        selectedStadiumSector = "SOUTH VIP EXECUTIVE"
                                        calculatedPrice = 145.0
                                    } else if (x < 0.4f) {
                                        selectedStadiumSector = "SOCIOS DECK - SECTOR A"
                                        calculatedPrice = 75.0
                                    } else {
                                        selectedStadiumSector = "EAST SUITE - SECTION C"
                                        calculatedPrice = 85.0
                                    }
                                    isBookingConfirmedMsg = ""
                                }
                            }
                    ) {
                        val bowlWidth = size.width
                        val bowlHeight = size.height

                        // Draw Stadium Green Field Center Pitch
                        drawRoundRect(
                            color = PitchGreen,
                            topLeft = Offset(bowlWidth * 0.32f, bowlHeight * 0.28f),
                            size = Size(bowlWidth * 0.36f, bowlHeight * 0.44f),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        // Draw Pitch Lines
                        drawRect(
                            color = Color.White.copy(alpha = 0.5f),
                            topLeft = Offset(bowlWidth * 0.32f, bowlHeight * 0.28f),
                            size = Size(bowlWidth * 0.36f, bowlHeight * 0.44f),
                            style = Stroke(width = 2f)
                        )

                        // Draw outer circle stands
                        drawOval(
                            color = ClubScarlet.copy(alpha = 0.2f),
                            topLeft = Offset(bowlWidth * 0.12f, bowlHeight * 0.08f),
                            size = Size(bowlWidth * 0.76f, bowlHeight * 0.84f),
                            style = Stroke(width = 18f)
                        )

                        // Draw Sector Selections color indicator
                        val highlightColor = GoldAccent.copy(alpha = 0.7f)
                        val defaultColor = Color.White.copy(alpha = 0.3f)

                        // Sector Socis A (Left)
                        drawArc(
                            color = if (selectedStadiumSector == "SOCIOS DECK - SECTOR A") highlightColor else defaultColor,
                            startAngle = 135f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(bowlWidth * 0.08f, bowlHeight * 0.04f),
                            size = Size(bowlWidth * 0.84f, bowlHeight * 0.92f),
                            style = Stroke(width = 24f)
                        )

                        // Sector Suites C (Right)
                        drawArc(
                            color = if (selectedStadiumSector == "EAST SUITE - SECTION C") highlightColor else defaultColor,
                            startAngle = -45f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(bowlWidth * 0.08f, bowlHeight * 0.04f),
                            size = Size(bowlWidth * 0.84f, bowlHeight * 0.92f),
                            style = Stroke(width = 24f)
                        )

                        // Sector North Tier (Top)
                        drawArc(
                            color = if (selectedStadiumSector == "NORTH STAND - TIER 2") highlightColor else defaultColor,
                            startAngle = 225f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(bowlWidth * 0.08f, bowlHeight * 0.04f),
                            size = Size(bowlWidth * 0.84f, bowlHeight * 0.92f),
                            style = Stroke(width = 24f)
                        )

                        // Sector South VIP (Bottom)
                        drawArc(
                            color = if (selectedStadiumSector == "SOUTH VIP EXECUTIVE") highlightColor else defaultColor,
                            startAngle = 45f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(bowlWidth * 0.08f, bowlHeight * 0.04f),
                            size = Size(bowlWidth * 0.84f, bowlHeight * 0.92f),
                            style = Stroke(width = 24f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(selectedStadiumSector ?: "SOCIOS DECK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Estimated Cost: $${calculatedPrice}", color = LightGreyText, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.buyVIPExperience(selectedStadiumSector ?: "Deck A", calculatedPrice)
                                isBookingConfirmedMsg = "Ticket successfully booked and saved to wallet!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet),
                            modifier = Modifier.testTag("book_selected_seat_btn")
                        ) {
                            Text("BUY SEAT VIA G-PAY", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isBookingConfirmedMsg.isNotEmpty()) {
                        Text(
                            isBookingConfirmedMsg,
                            color = Color.Green,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Digital Ticket Wallet Header
        item {
            Text("YOUR DIGITAL WALLET & STADIUM QR CODES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // Purchased Ticket Lists (QR code wallets)
        items(purchasedTickets) { ticket ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (ticket.isVIP) GoldAccent else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (ticket.isVIP) GoldAccent else ClubScarlet)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (ticket.isVIP) "VIP EXPERIENCE" else "VERIFIED SEAT",
                                color = if (ticket.isVIP) Color.Black else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            "vs ${ticket.matchOpponent}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(ticket.competition, color = LightGreyText, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${ticket.section} • ${ticket.row} • ${ticket.seat}", color = LightGreyText, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // QR Simulator drawn in material boxes
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // QR Pixels simulation
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(color = Color.Black, topLeft = Offset(0f, 0f), size = Size(18f, 18f))
                                drawRect(color = Color.Black, topLeft = Offset(size.width - 18f, 0f), size = Size(18f, 18f))
                                drawRect(color = Color.Black, topLeft = Offset(0f, size.height - 18f), size = Size(18f, 18f))
                                drawRect(color = Color.Black, topLeft = Offset(size.width / 2 - 9f, size.height / 2 - 9f), size = Size(18f, 18f))
                            }
                        }
                        Text(
                            ticket.qrCodeId.takeLast(8),
                            color = GoldAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// ==========================================
// SCREEN 6: CLUB SHOP (E-COMMERCE)
// ==========================================
@Composable
fun ShopScreen(
    items: List<ShopItem>,
    cart: Map<ShopItem, Int>,
    viewModel: ClubViewModel
) {
    var sizeSetting by remember { mutableStateOf("M") }
    val cartSum = cart.entries.sumOf { it.key.price * it.value }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    "PREMIUM MERCHANDISING CLUB SHOP",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "Official Scarlet Armor & Fan Collectibles",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Cart Drawer / Checkout alert if cart is not empty
        if (cart.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("cart_indicator_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ClubVelvetDark.copy(alpha = 0.8f)),
                    border = BorderStroke(1.dp, GoldAccent)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "YOUR SHOPPING BAG (${cart.values.sum()} ITEMS)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                "Total: $${String.format("%.2f", cartSum)}",
                                color = GoldAccent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { viewModel.checkoutCart() },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.weight(1f).testTag("checkout_button")
                            ) {
                                Text("CHECKOUT", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // Kits and items
        items(items) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_${product.id}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Jersey Drawing placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClubScarlet.copy(alpha = 0.2f))
                            .border(1.dp, ClubScarlet.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👕", fontSize = 24.sp)
                            Text(
                                product.category,
                                color = GoldAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(product.description, color = LightGreyText, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "$${product.price}",
                                color = GoldAccent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            
                            // Quantity selector
                            val count = cart[product] ?: 0
                            if (count > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.removeFromCart(product) }) {
                                        Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                    Text("$count", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                                    IconButton(onClick = { viewModel.addToCart(product) }) {
                                        Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.addToCart(product) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet),
                                    modifier = Modifier.testTag("add_to_cart_${product.id}")
                                ) {
                                    Text("ADD TO BAG", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// ==========================================
// SCREEN 7: FAN COMMUNITY & POLLS
// ==========================================
@Composable
fun CommunityScreen(
    polls: List<CommunityPoll>,
    activeMatch: Match?,
    viewModel: ClubViewModel
) {
    var predictionValue by remember { mutableStateOf<String?>(null) }
    var confirmedPredictionMsg by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    "SUPPORTERS HUB & FAN ENGAGEMENT",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "Predict Scores, Vote, and Earn Status Points",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Fixture Prediction Card for Next Match
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ClubScarlet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .testTag("prediction_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "NEXT FIXTURE PREDICTIONS",
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Apex Reds vs London Gunners",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Predict the outcome to earn 150 loyalty points!",
                        color = LightGreyText,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PredictionOptionButton("HOME WIN (RED)", isSelected = predictionValue == "HOME") {
                            predictionValue = "HOME"
                            confirmedPredictionMsg = "Prediction recorded: Home Win! 🔴"
                        }
                        PredictionOptionButton("DRAW SCENARIO", isSelected = predictionValue == "DRAW") {
                            predictionValue = "DRAW"
                            confirmedPredictionMsg = "Prediction recorded: Draw Match! 🤝"
                        }
                        PredictionOptionButton("AWAY WIN", isSelected = predictionValue == "AWAY") {
                            predictionValue = "AWAY"
                            confirmedPredictionMsg = "Prediction recorded: Away Win! 🏳️"
                        }
                    }

                    if (confirmedPredictionMsg.isNotEmpty()) {
                        Text(
                            confirmedPredictionMsg,
                            color = Color.Green,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Poll Voting Cards
        item {
            Text("ACTIVE CLUB INFLUENCE POLLS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(polls) { poll ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("poll_${poll.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("VOTABLE POLL", color = ClubScarlet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        if (poll.userVote != null) {
                            Text("VOTE RECORDED (+100 PTS)", color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        poll.question,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    poll.options.forEachIndexed { idx, option ->
                        val isUserPick = poll.userVote == idx
                        val totalVotes = poll.votes.sum().coerceAtLeast(1)
                        val ratio = poll.votes[idx].toFloat() / totalVotes
                        val percentage = (ratio * 100).toInt()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .border(
                                    1.dp,
                                    if (isUserPick) GoldAccent else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.voteInPoll(poll.id, idx) }
                        ) {
                            // ratio filling gauge
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(if (isUserPick) GoldAccent.copy(alpha = 0.15f) else ClubScarlet.copy(alpha = 0.12f))
                                    .align(Alignment.CenterStart)
                                    .padding(vertical = 12.dp)
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .align(Alignment.Center),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(option, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("$percentage% (${poll.votes[idx]} Votes)", color = LightGreyText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun RowScope.PredictionOptionButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ClubScarlet else Color.White.copy(alpha = 0.06f))
            .border(1.dp, if (isSelected) GoldAccent else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// ==========================================
// SCREEN 8: INTERACTIVE TROPHY MUSEUM & HISTORY
// ==========================================
@Composable
fun HistoryMuseumScreen() {
    val trophies = listOf(
        Trophy("League Champions", 18, "2024, 2022, 2018, 2015, 2011, 2008, 1999...", "The premier top-flight football league championship of the land. We have dominated decades of sports history."),
        Trophy("Champions Cup", 3, "2023, 2019, 2005", "Europe's most prestigious club competition, secured after magical stadium nights under the floodlights."),
        Trophy("FA Trophy Cups", 9, "2024, 2021, 2017, 2012, 2006, 2002...", "The oldest and most storied domestic elimination cup tournament in club history."),
        Trophy("Club World Cup", 10, "2024, 2023, 2019...", "A showcase of world dominance, solidifying our reign on the global stage.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    "INTERACTIVE STORYTELLING & MUSEUM",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "Our Stature: 40 Major Trophies Since 1892",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Trophies Row Display
        items(trophies) { trophy ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    // Big Cup Silhouette Icon
                    Text("🏆", fontSize = 42.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "${trophy.count}x ${trophy.title}",
                            color = GoldAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Winner Years: ${trophy.years}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            trophy.description,
                            color = LightGreyText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Timelines
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("LEGENDARY MOMENTS TIMELINE", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    TimelineEvent("1892", "Apex Reds Foundation is officially filed by local community workers.")
                    TimelineEvent("1999", "Historical Treble win, securing our status as world global powerhouse.")
                    TimelineEvent("2024", "Unbeaten season title capture under Marcus 'Apex' Sterling's magical reign.")
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun TimelineEvent(year: String, text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(year, color = ClubScarlet, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(44.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(40.dp)
                .background(GoldAccent)
                .padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

// ==========================================
// SCREEN 9: AI CLUB FAN ASSISTANT (GEMINI)
// ==========================================
@Composable
fun ClubAssistantScreen(
    viewModel: ClubViewModel
) {
    var chatInputText by remember { mutableStateOf("") }
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
            Text(
                "AI COMPANION CLUB ASSISTANT",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enthusiastic Reds Neural Bot", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "RESET CHAT",
                    color = ClubScarlet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.clearChat() }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat logs
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("chat_messages_column"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages) { chat ->
                    val isModel = chat.first == "model"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isModel) CharcoalSurface else ClubScarlet
                            ),
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .border(
                                    0.5.dp,
                                    if (isModel) GoldAccent else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = chat.second,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                if (isChatLoading) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CharcoalSurface)
                                    .padding(8.dp)
                            ) {
                                Text("Apex Reds writing...", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Suggestions toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ChatSuggestionButton("Tell me about tickets?") { viewModel.sendChatMessage(it) }
            ChatSuggestionButton("Buy home jerseys?") { viewModel.sendChatMessage(it) }
            ChatSuggestionButton("Trophies won?") { viewModel.sendChatMessage(it) }
            ChatSuggestionButton("Marcus Sterling squad details?") { viewModel.sendChatMessage(it) }
        }

        // Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInputText,
                onValueChange = { chatInputText = it },
                placeholder = { Text("Ask about tickets, shirts, or matches...", color = LightGreyText, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = ClubScarlet.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (chatInputText.isNotBlank()) {
                        viewModel.sendChatMessage(chatInputText)
                        chatInputText = ""
                        focusManager.clearFocus()
                    }
                })
            )
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = {
                    if (chatInputText.isNotBlank()) {
                        viewModel.sendChatMessage(chatInputText)
                        chatInputText = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ClubScarlet)
                    .testTag("chat_send_button")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatSuggestionButton(label: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .border(0.5.dp, ClubScarlet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .background(CharcoalSurface)
            .clickable { onClick(label) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// SCREEN 10: ADMIN PLATFORM (SIMULATOR & METRICS)
// ==========================================
@Composable
fun AdminSuiteScreen(
    viewModel: ClubViewModel
) {
    val logs by viewModel.adminNotificationLogs.collectAsState()
    val rev by viewModel.totalRevenue.collectAsState()
    val installs by viewModel.appInstalls.collectAsState()

    var customTitle by remember { mutableStateOf("") }
    var customBody by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("NEWS") }
    var customPushText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    "CONNECTED ADMINISTRATION SYSTEM",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "Configure News, Push Alerter & Live Telemetry",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Telemetry Indicators Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("MEMBER INSTALLS", color = LightGreyText, fontSize = 10.sp)
                        Text(installs.toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("▲ +12% this week", color = Color.Green, fontSize = 9.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL REVENUE", color = LightGreyText, fontSize = 10.sp)
                        Text("$${String.format("%.2f", rev)}", color = GoldAccent, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text("▲ Strong kit sales", color = Color.Green, fontSize = 9.sp)
                    }
                }
            }
        }

        // Broadcaster simulator triggers
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("LIVE ACTION STADIUM TRIGGER CONTROLS", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.adminSimulateMatchGoal() },
                            colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("GOAL RED!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.adminSimulateOpponentGoal() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("GOAL BLUE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.adminSimulateYellowCard() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("RED CARD", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Post custom News console
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("PUBLISH OFFICIAL PRESS BULLETINS", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Bulletin Headline Text") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customBody,
                        onValueChange = { customBody = it },
                        label = { Text("Bulletin Report Body Content") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (customTitle.isNotEmpty() && customBody.isNotEmpty()) {
                                viewModel.adminPostCustomNews(customTitle, customBody, selectedCategory)
                                customTitle = ""
                                customBody = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BROADCAST BULLETIN WORLDWIDE", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Push Alert Broadcaster console
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("EMERGENCY PUSH DISPATCHER", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customPushText,
                        onValueChange = { customPushText = it },
                        label = { Text("Notification Push Alert Message") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (customPushText.isNotEmpty()) {
                                viewModel.adminDispatchPushAlert(customPushText)
                                customPushText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("DISPATCH MOBILE ALERT NOW", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// ==========================================
// DYNAMIC MODAL BOXES
// ==========================================
@Composable
fun NewsDetailDialog(
    news: News,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                news.title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ClubScarlet)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(news.category, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = news.body,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet)
            ) {
                Text("DISMISS BULLETIN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CharcoalSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.border(1.dp, GoldAccent, RoundedCornerShape(16.dp))
    )
}

@Composable
fun NotificationsPanel(
    viewModel: ClubViewModel,
    onDismiss: () -> Unit
) {
    val logs by viewModel.adminNotificationLogs.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔴 NOTIFICATION ARCHIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = LightGreyText)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            "No recent official club alerts dispatched yet. Manage these inside our Administration connected tab!",
                            color = LightGreyText,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                    }
                } else {
                    items(logs) { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                        ) {
                            Text(
                                text = log,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ClubScarlet)
            ) {
                Text("CLOSE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CharcoalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
