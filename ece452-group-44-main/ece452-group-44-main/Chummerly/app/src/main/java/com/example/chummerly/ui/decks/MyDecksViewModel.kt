package com.example.chummerly.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.data.room.CardDao
import com.example.chummerly.data.room.DeckDao
import com.example.chummerly.model.Deck
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
class MyDecksViewModel @Inject constructor(
    private val deckDao: DeckDao
): ViewModel() {
    private val _uiState = MutableStateFlow(MyDecksState())
    val uiState: StateFlow<MyDecksState> = _uiState.asStateFlow();

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deckDao.getAll().collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            decks = it,
                            decksExist = it.isNotEmpty()
                        )
                    }
                }
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
                    decks = deckDao.filterByKeyword("%$newValue%"),
                )
            }
        }
    }

    fun sortAlphabetical() {
        _uiState.update { currentState ->
            val sortedList: MutableList<DeckEntity> = currentState.decks.toMutableList()
            sortedList.sortBy { deck: DeckEntity -> deck.name }
            currentState.copy(
                decks = sortedList.toList()
            )
        }
    }

    fun sortByOldest() {
        _uiState.update { currentState ->
            val sortedList: MutableList<DeckEntity> = currentState.decks.toMutableList()
            sortedList.sortBy { deck: DeckEntity -> deck.id }
            currentState.copy(
                decks = sortedList.toList()
            )
        }
    }

    fun sortByNewest() {
        _uiState.update { currentState ->
            val sortedList: MutableList<DeckEntity> = currentState.decks.toMutableList()
            sortedList.sortByDescending { deck: DeckEntity -> deck.id }
            currentState.copy(
                decks = sortedList.toList()
            )
        }
    }


}

data class MyDecksState(
    val decks: List<DeckEntity> = emptyList(),
    val filteredDecks: List<DeckEntity> = emptyList(),
    val decksExist: Boolean = false,
    val keyword: String = ""
)

