package com.horgaring.dateapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.horgaring.dateapp.data.UserProfile
import com.horgaring.dateapp.data.repository.DateAppRepository
import com.horgaring.dateapp.ui.viewmodel.SwipeViewModel
import kotlinx.coroutines.launch

@Composable
fun SwipeScreen(
    navController: NavController,
    swipeViewModel: SwipeViewModel = viewModel()
) {
    val profiles by swipeViewModel.profiles.collectAsState()
    val currentIndex by swipeViewModel.currentIndex.collectAsState()
    val isLoading by swipeViewModel.isLoading.collectAsState()
    val showMatchAnimation by swipeViewModel.showMatchAnimation.collectAsState()
    val matchResult by swipeViewModel.matchResult.collectAsState()

    val repository = remember { DateAppRepository() }
    var unreadCount by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        unreadCount = repository.getUnreadNotificationCount()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Профиль",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Поиск",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = { navController.navigate("chat") }) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text(unreadCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Chat,
                                contentDescription = "Чаты",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Card stack area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentIndex < profiles.size) {
                    // Show next card behind (peek)
                    if (currentIndex + 1 < profiles.size) {
                        ProfileCard(
                            profile = profiles[currentIndex + 1],
                            modifier = Modifier
                                .scale(0.92f)
                                .graphicsLayer { alpha = 0.5f }
                        )
                    }
                    // Current card with swipe
                    SwipeableProfileCard(
                        profile = profiles[currentIndex],
                        onSwipeLeft = { swipeViewModel.swipeLeft() },
                        onSwipeRight = { swipeViewModel.swipeRight() }
                    )
                } else if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Анкеты закончились!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Загляните позже — появятся новые люди",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(onClick = { swipeViewModel.loadProfiles() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Обновить")
                        }
                    }
                }
            }

            // Action buttons
            if (currentIndex < profiles.size) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 48.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dislike button
                    FloatingActionButton(
                        onClick = { swipeViewModel.swipeLeft() },
                        modifier = Modifier.size(64.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Пропустить",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Like button
                    FloatingActionButton(
                        onClick = { swipeViewModel.swipeRight() },
                        modifier = Modifier.size(64.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Нравится",
                            tint = Color(0xFF66BB6A),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Match animation overlay
        AnimatedVisibility(
            visible = showMatchAnimation,
            enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(500, easing = EaseOutBack)
            ),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            MatchOverlay(
                matchedUserName = matchResult?.user?.name ?: "",
                onSendMessage = {
                    swipeViewModel.dismissMatch()
                    navController.navigate("chat")
                },
                onKeepSwiping = {
                    swipeViewModel.dismissMatch()
                }
            )
        }
    }
}

@Composable
fun SwipeableProfileCard(
    profile: UserProfile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = (offsetX.value / 40f).coerceIn(-15f, 15f)
            }
            .pointerInput(profile.id) {
                detectDragGestures(
                    onDragEnd = {
                        val currentX = offsetX.value
                        val threshold = 300f
                        when {
                            currentX > threshold -> {
                                coroutineScope.launch {
                                    offsetX.animateTo(1500f, tween(250))
                                    onSwipeRight()
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                            }
                            currentX < -threshold -> {
                                coroutineScope.launch {
                                    offsetX.animateTo(-1500f, tween(250))
                                    onSwipeLeft()
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                            }
                            else -> {
                                coroutineScope.launch {
                                    launch { offsetX.animateTo(0f, spring(dampingRatio = 0.65f, stiffness = 400f)) }
                                    launch { offsetY.animateTo(0f, spring(dampingRatio = 0.65f, stiffness = 400f)) }
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            launch { offsetX.animateTo(0f, spring(stiffness = 500f)) }
                            launch { offsetY.animateTo(0f, spring(stiffness = 500f)) }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            }
    ) {
        ProfileCard(profile = profile)

        // LIKE stamp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .graphicsLayer {
                    alpha = (offsetX.value / 300f).coerceIn(0f, 1f)
                }
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF66BB6A).copy(alpha = 0.9f)
            ) {
                Text(
                    text = "НРАВИТСЯ",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                )
            }
        }

        // NOPE stamp
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .graphicsLayer {
                    alpha = (-offsetX.value / 300f).coerceIn(0f, 1f)
                }
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF5350).copy(alpha = 0.9f)
            ) {
                Text(
                    text = "НЕТ",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!profile.imageUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        AsyncImage(
                            model = profile.imageUrl,
                            contentDescription = "Фото ${profile.name}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${profile.age}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = profile.location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }

                // Bio + interests section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    if (profile.bio.isNotBlank()) {
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }

                    if (profile.interests.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            profile.interests.take(3).forEach { interest ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = interest,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            if (profile.interests.size > 3) {
                                Text(
                                    text = "+${profile.interests.size - 3}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchOverlay(
    matchedUserName: String,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit
) {
    val heartScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val buttonsAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        heartScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        textAlpha.animateTo(1f, animationSpec = tween(400))
        buttonsAlpha.animateTo(1f, animationSpec = tween(400, delayMillis = 200))
    }

    // Pulsing heart
    val infiniteTransition = rememberInfiniteTransition(label = "heartPulse")
    val heartPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE91E63).copy(alpha = 0.9f),
                        Color(0xFFAD1457).copy(alpha = 0.95f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .scale(heartScale.value * heartPulse),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Это взаимно!",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.graphicsLayer { alpha = textAlpha.value }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Вы и $matchedUserName понравились друг другу",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = textAlpha.value }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = buttonsAlpha.value },
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Написать сообщение",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                OutlinedButton(
                    onClick = onKeepSwiping,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.6f))
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(
                        "Продолжить",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
