package com.example.chummerly.ui.decks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.Category
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.data.room.CardDao
import com.example.chummerly.data.room.CategoryDao
import com.example.chummerly.data.room.DeckDao
import com.example.chummerly.model.Card
import com.example.chummerly.model.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditDeckViewModel @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val categoryDao: CategoryDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(EditDeckState())
    val uiState: StateFlow<EditDeckState> = _uiState.asStateFlow();

    private val deckId: Long? = savedStateHandle["deckId"]

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (deckId != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            deck = deckDao.findById(deckId)
                        )
                    }
                }
                categoryDao.getAll().collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            categories = it
                        )
                    }
                }
            }
        }
    }

    fun updateName(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                deck = currentState.deck.copy(name = newValue)
            )
        }
    }

    fun updateCategory(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                deck = currentState.deck.copy(categoryId = newValue)
            )
        }
    }

    fun updateDescription(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                deck = currentState.deck.copy(description = newValue)
            )
        }
    }

    fun updateTheme(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                deck = currentState.deck.copy(theme = newValue)
            )
        }
    }

    fun updateDeck() {
        CoroutineScope(Dispatchers.IO).launch {
            if (_uiState.value.deck.name != "") {
                deckDao.updateDecks(uiState.value.deck);
            }
        }
    }

}

data class EditDeckState(
    val deck: DeckEntity = DeckEntity(name = "", description = "", theme = ""),
    val categories: List<Category> = emptyList()
)
