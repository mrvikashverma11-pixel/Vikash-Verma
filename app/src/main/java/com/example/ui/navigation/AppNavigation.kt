package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.ai.AiStudioScreen
import com.example.ui.editor.EditorScreen
import com.example.ui.home.HomeScreen
import com.example.ui.premium.PremiumPlanScreen
import com.example.ui.templates.TemplatesScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianBorderLight
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Templates : Screen("templates", "Templates", Icons.Default.Dashboard)
    object AiStudio : Screen("ai_studio", "AI Tools", Icons.Default.AutoAwesome)
    object Premium : Screen("premium", "VIP Pro", Icons.Default.WorkspacePremium)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Templates,
        Screen.AiStudio,
        Screen.Premium
    )

    val showBottomBar = bottomBarScreens.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianSurface)
                        .border(width = 1.dp, color = ObsidianBorder)
                ) {
                    NavigationBar(
                        containerColor = ObsidianSurface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        bottomBarScreens.forEachIndexed { index, screen ->
                            val isSelected = currentRoute == screen.route

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) ElectricCyan else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        color = if (isSelected) ElectricCyan else TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = CyanPrimary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNewProject = {
                        navController.navigate("editor")
                    },
                    onOpenTemplate = { tmplId ->
                        navController.navigate("editor?templateId=$tmplId")
                    },
                    onOpenProject = { projId ->
                        navController.navigate("editor?projectId=$projId")
                    },
                    onOpenAiStudio = {
                        navController.navigate(Screen.AiStudio.route)
                    },
                    onOpenPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }

            composable(Screen.Templates.route) {
                TemplatesScreen(
                    onSelectTemplate = { tmplId ->
                        navController.navigate("editor?templateId=$tmplId")
                    }
                )
            }

            composable(Screen.AiStudio.route) {
                AiStudioScreen(
                    onOpenEditorWithTemplate = { tmplId ->
                        navController.navigate("editor?templateId=$tmplId")
                    }
                )
            }

            composable(Screen.Premium.route) {
                PremiumPlanScreen()
            }

            composable(
                route = "editor?projectId={projectId}&templateId={templateId}",
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("templateId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                val templateId = backStackEntry.arguments?.getString("templateId")
                EditorScreen(
                    projectId = projectId,
                    templateId = templateId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

