package com.example.chummerly

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector) {
    object Home : Screen("Home", Icons.Filled.Home)
    object Decks : Screen("Decks", Icons.Filled.List)
    object Profile : Screen("Profile", Icons.Filled.Person)
    object Settings : Screen("Settings", Icons.Filled.Settings)
}
