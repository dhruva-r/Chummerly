package com.example.chummerly.ui.decks

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
class CreateDeckViewModel @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val categoryDao: CategoryDao
): ViewModel() {
    private val _uiState = MutableStateFlow(CreateDeckUiState())
    val uiState: StateFlow<CreateDeckUiState> = _uiState.asStateFlow();

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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

    fun updateFront(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                front = newValue
            )
        }
    }

    fun updateBack(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                back = newValue
            )
        }
    }

    fun updateFont(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                font = newValue
            )
        }
    }

    fun updateTags(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                tags = newValue
            )
        }
    }

    fun saveDeck() {
        CoroutineScope(Dispatchers.IO).launch {
            if (_uiState.value.deck.name != "") {
                val deckId = deckDao.insert(_uiState.value.deck)
                _uiState.update { currentState ->
                    currentState.copy(
                        deck = currentState.deck.copy(id = deckId)
                    )
                }
            }
        }
    }

    fun addCard() {
        val card = CardEntity(
            deckId = _uiState.value.deck.id,
            front = _uiState.value.front,
            back = _uiState.value.back,
            font = _uiState.value.font,
            tags = _uiState.value.tags
        )
        val currentCards = _uiState.value.cards
        currentCards.add(card)
        _uiState.update { currentState ->
            currentState.copy(
                cards = currentCards,
                front = "",
                back = "",
                font = "",
                tags = ""
            )
        }
    }

    fun saveCards() {
        CoroutineScope(Dispatchers.IO).launch {
            cardDao.insertAll(_uiState.value.cards.toList())
        }
    }
}

data class CreateDeckUiState(
    val deck: DeckEntity = DeckEntity(name = "", description = "", theme = ""),
    val cards: ArrayList<CardEntity> = arrayListOf(),
    val categories: List<Category> = emptyList(),
    val front: String = "",
    val back: String = "",
    val font: String = "",
    val tags: String = ""
)
