package com.example.chummerly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.chummerly.data.getMockStudyDeck
import com.example.chummerly.ui.FinishedStudyScreen
import com.example.chummerly.ui.decks.AddCardScreen
import com.example.chummerly.ui.decks.AddDHandWritingScreen
import com.example.chummerly.ui.decks.CreateCategoryScreen
import com.example.chummerly.ui.decks.CreateDeckScreen
import com.example.chummerly.ui.decks.CreateDeckViewModel
import com.example.chummerly.ui.decks.DeckDetailsScreen
import com.example.chummerly.ui.decks.DeckDetailsViewModel
import com.example.chummerly.ui.decks.EditCardScreen
import com.example.chummerly.ui.decks.EditDeckScreen
import com.example.chummerly.ui.decks.ExportDeckScreen
import com.example.chummerly.ui.decks.MergeDecksScreen
import com.example.chummerly.ui.decks.MyDecksScreen
import com.example.chummerly.ui.decks.ViewCardsScreen
import com.example.chummerly.ui.progress.ProgressScreen
import com.example.chummerly.ui.settings.SettingsScreen
import com.example.chummerly.ui.study.ChooseStudyModeScreen
import com.example.chummerly.ui.study.ClassicStudyScreen
import com.example.chummerly.ui.study.SpacedRepetitionStudyScreen
import com.example.chummerly.ui.study.ShuffledStudyScreen
import com.example.chummerly.ui.study.ShuffledStudyViewModel
import com.example.chummerly.ui.study.SpacedStudyViewModel
import com.example.chummerly.ui.study.StudyViewModel
import com.example.chummerly.ui.study.VoiceStudyScreen
import com.example.chummerly.ui.study.VoiceStudyViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChummerlyNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Decks.route,
    readTextFunction: (String) -> Unit
){
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            ChummerlyHome()
        }
        composable(route = Screen.Decks.route) {
            MyDecksScreen(
                onDeckClick = { deckId ->
                    navController.navigate("Deck Details/$deckId")
                }
            )
        }
        composable(route = Screen.Profile.route) {
            ProgressScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
        navigation(startDestination = "Create Deck", route = "Create") {
            composable(route = "Create Deck") {
                CreateDeckScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onFinish = {
                        navController.navigate(Screen.Decks.route) {
                            popUpTo("Create") {
                                inclusive = true
                            }
                        }
                    },
                    onCreateCategory = {
                        navController.navigate("Create Category")
                    }
                )
            }
        }
        composable(route = "Merge Decks") {
            MergeDecksScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "Edit Deck/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {
            EditDeckScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onCreateCategory = {
                    navController.navigate("Create Category")
                }
            )
        }
        composable(
            route = "Add Card/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {
            AddCardScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onClickHandwriting = {
                    navController.navigate("Handwriting")
                }
            )
        }
        composable(route = "Handwriting") {
            AddDHandWritingScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddCard = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = "Create Category") {
            CreateCategoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        navigation(startDestination="Deck Details/{deckId}", route="Deck") {
            composable(
                route = "Deck Details/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.LongType })
            ) { entry ->
                val viewModel = entry.sharedViewModel<DeckDetailsViewModel>(navController)
                val deckId = entry.arguments?.getLong("deckId")
                DeckDetailsScreen(
                    deckDetailsViewModel = viewModel,
                    onStudyClick = {
                        navController.navigate("Choose Study Mode/$deckId")
                    },
                    onViewCardsClick = {
                        navController.navigate("View Cards/$deckId")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddCards = {
                        navController.navigate("Add Card/$deckId")
                    },
                    onEditDeck = {
                        navController.navigate("Edit Deck/$deckId")
                    },
                    onExportDeck = {
                        navController.navigate("Export Deck/$deckId")
                    }
                )
            }
            composable(
                route = "View Cards/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.LongType })
            ) { entry ->
                val viewModel = entry.sharedViewModel<DeckDetailsViewModel>(navController)
                val deckId = entry.arguments?.getLong("deckId")
                ViewCardsScreen(
                    deckDetailsViewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddCards = {
                        navController.navigate("Add Card/$deckId")
                    },
                    onEditCard = { cardId ->
                        navController.navigate("Edit Card/$cardId")
                    }
                );
            }
            composable(
                route = "Edit Card/{cardId}",
                arguments = listOf(navArgument("cardId") { type = NavType.LongType })
            ) {
                EditCardScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onFinish = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = "Export Deck/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.LongType })
            ) {entry ->
                val viewModel = entry.sharedViewModel<DeckDetailsViewModel>(navController)
                ExportDeckScreen(
                    deckDetailsViewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = "Choose Study Mode/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { entry ->
            val deckId = entry.arguments?.getLong("deckId")
            ChooseStudyModeScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onClassicStudyClick = {
                    navController.navigate("Classic Study/$deckId")
                },
                onSpacedRepetitionClick = {
                    navController.navigate("Spaced Repetition Study/$deckId")
                },
                onShuffledStudyClick = {
                    navController.navigate("Shuffled Study/$deckId")
                },
                onVoiceStudyClick = {
                    navController.navigate("Voice Study/$deckId")
                }
            )
        }
        composable(
            route = "Classic Study/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { entry ->
            val deckId = entry.arguments?.getLong("deckId")
            ClassicStudyScreen(
                onClose = {
                    navController.popBackStack("Deck Details/$deckId", false);
                },
                onFinishStudy = {
                    navController.navigate("Finished Study/$deckId")
                },
                readText = readTextFunction
            )
        }
        composable(route = "Spaced Repetition Study/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {entry ->
            val deckId = entry.arguments?.getLong("deckId")
            SpacedRepetitionStudyScreen(
                onClose = {
                    navController.popBackStack("Deck Details/$deckId", false);
                },
                onFinishSpacedStudy = {
                    navController.navigate("Finished Study/${entry.arguments?.getLong("deckId")}")
                },
                readText = readTextFunction
            )
        }
        composable(route = "Shuffled Study/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {entry ->
            val viewModel = entry.sharedViewModel<ShuffledStudyViewModel>(navController)
            val deckId = entry.arguments?.getLong("deckId")
            ShuffledStudyScreen(
                shuffledStudyViewModel = viewModel,
                onClose = {
                    navController.popBackStack("Deck Details/$deckId", false);
                },
                onFinishShuffledStudy = {
                    navController.navigate("Finished Study/${entry.arguments?.getLong("deckId")}")
                },
                readText = readTextFunction
            )
        }
        composable(route = "Voice Study/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {entry ->
            val deckId = entry.arguments?.getLong("deckId")
            VoiceStudyScreen(
                onClose = {
                    navController.popBackStack("Deck Details/$deckId", false);
                },
                onFinishVoiceStudy = {
                    navController.navigate("Finished Study/${entry.arguments?.getLong("deckId")}")
                },
                readText = readTextFunction
            )
        }
        composable(
            route = "Finished Study/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { entry ->
            val deckId = entry.arguments?.getLong("deckId")
            FinishedStudyScreen(
                onFinish = {
                    navController.popBackStack("Deck Details/$deckId", false);
                }
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}
