package com.example.chummerly.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.Category
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.data.room.CardDao
import com.example.chummerly.data.room.CategoryDao
import com.example.chummerly.data.room.DeckDao
import com.example.chummerly.model.Deck
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
class MergeDecksViewModel @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val categoryDao: CategoryDao
): ViewModel() {
    private val _uiState = MutableStateFlow(MergeDecksState())
    val uiState: StateFlow<MergeDecksState> = _uiState.asStateFlow();

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deckDao.getAll().collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            decks = it
                        )
                    }
                }
            }
        }
    }

    fun loadCategories() {
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

    // id = Deck ID, order = which deck (ie. first or second)
    fun getCardsForDeckId(id: Long, order: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (order == 0) {
                    cardDao.trueFindByDeckId(id).collect {
                        _uiState.update { currentState ->
                            currentState.copy(
                                cardsA = it
                            )
                        }
                    }
                } else {
                    cardDao.trueFindByDeckId(id).collect {
                        _uiState.update { currentState ->
                            currentState.copy(
                                cardsB = it
                            )
                        }
                    }
                }
            }
        }
    }

    fun saveDeck(name: String, cat: String?, desc: String, newDeck: Deck) {
        CoroutineScope(Dispatchers.IO).launch {
            if (name != "") {
                val deckId = deckDao.insert(DeckEntity(name = name, categoryId = cat, description = desc, theme = "theme" ));
                val newCards = newDeck.getCards();
                val newCardEntities : ArrayList<CardEntity> = arrayListOf();
                for (c in newCards) {
                    newCardEntities.add(CardEntity( deckId = deckId, front = c.getQuestion(), back = c.getAnswer(), font = "", tags = "" ));
                }
                cardDao.insertAll(newCardEntities.toList())
            }
        }
    }
}

data class MergeDecksState(
    val decks: List<DeckEntity> = emptyList(),
    val cardsA: List<CardEntity> = emptyList(),
    val cardsB: List<CardEntity> = emptyList(),
    val categories: List<Category> = emptyList()
)
