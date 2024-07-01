package com.example.chummerly.ui.decks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.room.CardDao
import dagger.hilt.android.lifecycle.HiltViewModel
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
class EditCardViewModel @Inject constructor(
    private val cardDao: CardDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(EditCardState())
    val uiState: StateFlow<EditCardState> = _uiState.asStateFlow();

    private val cardId: Long? = savedStateHandle["cardId"]

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (cardId != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            card = cardDao.findById(cardId)
                        )
                    }
                }
            }
        }
    }

    fun updateFront(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                card = currentState.card.copy(front = newValue)
            )
        }
    }

    fun updateBack(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                card = currentState.card.copy(back = newValue)
            )
        }
    }

    fun updateFont(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                card = currentState.card.copy(font = newValue)
            )
        }
    }

    fun updateTags(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                card = currentState.card.copy(tags = newValue)
            )
        }
    }

    fun updateCard() {
        CoroutineScope(Dispatchers.IO).launch {
            cardDao.updateCards(_uiState.value.card)
        }
    }

    fun deleteCard() {
        CoroutineScope(Dispatchers.IO).launch {
            cardDao.deleteById(_uiState.value.card.id)
        }
    }
}

data class EditCardState(
    val card: CardEntity = CardEntity(deckId = 0, front = "", back = "", font = "", tags = "")
)
