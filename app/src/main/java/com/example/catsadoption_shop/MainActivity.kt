package com.example.catsadoption_shop

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.catsadoption_shop.ui.theme.CatsAdoptionShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            CatsAdoptionShopTheme {
                CatsAdoptionShopApp()
            }
        }
    }
}

@Composable
fun CatsAdoptionShopApp() {

    val navController = rememberNavController()

    // Logged-in username (null → user not logged in)
    var loggedInUsername by remember { mutableStateOf<String?>(null) }

    // Start at listings unless logged in
    val startDestination =
        if (loggedInUsername == null) "listings"
        else "dashboard"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // -------------------------------------------------------------
        // 1. CAT LISTINGS SCREEN (DEFAULT)
        // -------------------------------------------------------------
        composable("listings") {
            val context = LocalContext.current
            CatListingsScreen(
                onLoginClick = { navController.navigate("login") },
                onCatCommunicatorClick = {
                    val intent = Intent(context, PermissionCheckActivity::class.java)
                    context.startActivity(intent)
                },
                onAdminClick = { navController.navigate("admin_dashboard") },
                // This triggers IDOR route
                onProfileClick = { navController.navigate("userProfile/1") }
            )
        }

        // -------------------------------------------------------------
        // 2. LOGIN SCREEN
        // -------------------------------------------------------------
        composable("login") {
            LoginScreen(
                onLoginSuccess = { username ->
                    loggedInUsername = username

                    navController.navigate("dashboard") {
                        popUpTo("listings") { inclusive = true }
                    }
                }
            )
        }

        // -------------------------------------------------------------
        // 3. USER DASHBOARD
        // -------------------------------------------------------------
        composable("dashboard") {

            val username = loggedInUsername

            if (username != null) {
                UserDashboardScreen(
                    username = username,
                    onLogout = {
                        loggedInUsername = null
                        navController.navigate("listings") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            } else {
                // If reached without login, redirect
                navController.navigate("listings") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. ADMIN DASHBOARD
        // -------------------------------------------------------------
        composable("admin_dashboard") {
            AdminDashboardScreen()
        }

        // -------------------------------------------------------------
        // 5. USER PROFILE (IDOR VULNERABLE ENDPOINT)
        // -------------------------------------------------------------
        composable(
            route = "userProfile/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: 1

            UserProfileScreen(
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun PreviewCatsAdoptionShopApp() {
    CatsAdoptionShopTheme {
        CatsAdoptionShopApp()
    }
}
