package com.horgaring.dateapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.horgaring.dateapp.data.api.TokenManager
import com.horgaring.dateapp.ui.screens.ChatScreen
import com.horgaring.dateapp.ui.screens.ConversationScreen
import com.horgaring.dateapp.ui.screens.LoginScreen
import com.horgaring.dateapp.ui.screens.ProfileScreen
import com.horgaring.dateapp.ui.screens.SwipeScreen
import com.horgaring.dateapp.ui.theme.DateAppTheme
import com.horgaring.dateapp.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DateAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DatingAppNavigation()
                }
            }
        }
    }
}

@Composable
fun DatingAppNavigation() {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = viewModel()

    val startDest = if (TokenManager.isLoggedIn) "swipe" else "login"

    NavHost(
        navController = navController,
        startDestination = startDest,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(200)) }
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("swipe") {
            SwipeScreen(navController = navController)
        }
        composable("chat") {
            ChatScreen(navController = navController, chatViewModel = chatViewModel)
        }
        composable(
            route = "conversation/{conversationId}",
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ConversationScreen(
                navController = navController,
                conversationId = conversationId,
                chatViewModel = chatViewModel
            )
        }
    }
}