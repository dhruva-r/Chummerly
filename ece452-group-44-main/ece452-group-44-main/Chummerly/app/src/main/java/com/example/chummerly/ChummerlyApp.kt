package com.example.chummerly

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.chummerly.ui.theme.ChummerlyTheme
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChummerlyApp(readTextFunction: (String) -> Unit) {
    ChummerlyTheme() {
        val navController = rememberNavController()

        //TODO: Finish logic to show bottom nav bar
        val showNavBar = true

        Scaffold(
            bottomBar = {
                if (showNavBar) {
                    ChummerlyNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            ChummerlyNavGraph(Modifier.padding(innerPadding), navController, startDestination = Screen.Decks.route, readTextFunction = readTextFunction)
        }
    }
}
