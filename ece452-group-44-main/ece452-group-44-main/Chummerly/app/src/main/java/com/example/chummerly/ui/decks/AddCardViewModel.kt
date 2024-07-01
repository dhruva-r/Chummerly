package com.example.chummerly.ui.decks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.data.room.CardDao
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
class AddCardViewModel @Inject constructor(
    private val cardDao: CardDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(AddCardState())
    val uiState: StateFlow<AddCardState> = _uiState.asStateFlow();

    private val deckId: Long? = savedStateHandle["deckId"]

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

    fun saveCard() {
        if (deckId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val card = CardEntity(
                    deckId = deckId,
                    front = _uiState.value.front,
                    back = _uiState.value.back,
                    font = _uiState.value.font,
                    tags = _uiState.value.tags
                )
                cardDao.insert(card)
                _uiState.update { currentState ->
                    currentState.copy(
                        front = "",
                        back = "",
                        font = "",
                        tags = ""
                    )
                }
            }
        }
    }
}

data class AddCardState(
    val front: String = "",
    val back: String = "",
    val font: String = "",
    val tags: String = ""
)
