package com.example.chummerly.ui.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.data.room.CardDao
import com.example.chummerly.data.room.DeckDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
// Implemented with the assistance of chatGpt
@HiltViewModel
class StudyViewModel @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(ClassicDeckDetailsState())
    val uiState: StateFlow<ClassicDeckDetailsState> = _uiState.asStateFlow();

    private val deckId: Long? = savedStateHandle["deckId"]


    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (deckId != null) {
                    cardDao.trueFindByDeckId(deckId).collect {
                        _uiState.update { currentState ->
                            currentState.copy(
                                studyList = it
                            )
                        }
                    }
                    _uiState.update { currentState -> currentState.copy(
                        deck = deckDao.findById(deckId)
                    ) }
                }
            }
        }
    }



}

data class ClassicDeckDetailsState(
    val studyList: List<CardEntity> = emptyList(),
    val deck: DeckEntity = DeckEntity(name = "", categoryId = null, description = "", theme = "")
)

