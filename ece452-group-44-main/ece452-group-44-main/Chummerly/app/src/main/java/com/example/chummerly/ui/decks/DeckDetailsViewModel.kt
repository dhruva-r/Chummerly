package com.example.chummerly.ui.decks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.data.room.CardDao
import com.example.chummerly.data.room.DeckDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeckDetailsViewModel @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(DeckDetailsState())
    val uiState: StateFlow<DeckDetailsState> = _uiState.asStateFlow();

    private val deckId: Long? = savedStateHandle["deckId"]

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (deckId != null) {
                    deckDao.loadDeckAndCards(deckId).collect {
                        if (it.isNotEmpty()) {
                            val deckWithCards = it.entries.first()

                            _uiState.update { currentState ->
                                currentState.copy(
                                    deck = deckWithCards.key,
                                    cards = deckWithCards.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun deleteDeck() {
        CoroutineScope(Dispatchers.IO).launch {
            if (deckId != null) {
                deckDao.delete(uiState.value.deck)
            }
        }
    }

    fun updateKeyword(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                keyword = newValue
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            _uiState.update { currentState ->
                currentState.copy(
                    cards = cardDao.filterByKeyword("%$newValue%"),
                )
            }
        }
    }

}

data class DeckDetailsState (
    val deck: DeckEntity = DeckEntity(name = "", categoryId = null, description = "", theme = ""),
    val cards: List<CardEntity> = emptyList(),
    val keyword: String = ""
)