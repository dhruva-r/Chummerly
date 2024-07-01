package com.example.chummerly.ui.study

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
// Implemented with the assistance of chatGpt
@HiltViewModel
class SpacedStudyViewModel @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(SpacedDeckDetailsState())
    val uiState: StateFlow<SpacedDeckDetailsState> = _uiState.asStateFlow();
    private var currentCardIndex: Int = 0
    private val studyProgress = mutableListOf<StudyProgressItem>()
    private val deckId: Long? = savedStateHandle["deckId"]


    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (deckId != null) {
                    cardDao.trueFindByDeckId(deckId).collect {
                        _uiState.update { currentState ->
                            currentState.copy(
                                studyList = it.filter { it.progress < 5 },
                                currentCardIndex = 0 // Reset the currentCardIndex when new data is received
                            )
                        }
                        // Initialize studyProgress whenever studyList is updated
                        studyProgress.clear()
                        studyProgress.addAll(it.map { cardEntity -> StudyProgressItem(cardEntity.id, 0) })
                    }
                    _uiState.update { currentState -> currentState.copy(
                        deck = deckDao.findById(deckId)
                    ) }
                }
            }
        }
    }

    fun getNextCard(reviewCount: Int) {
        viewModelScope.launch {
            if (currentCardIndex < studyProgress.size) {

                val cardId = _uiState.value.studyList[currentCardIndex].id
                val currentProgress = studyProgress[currentCardIndex]
                val newReviewCount = reviewCount

                studyProgress[currentCardIndex] = StudyProgressItem(cardId, newReviewCount)
                if(reviewCount == 1){
                    updateProgress(1)
                    //cardDao.findById(cardId).progress = cardDao.findById(cardId).progress + 1
                }else if(reviewCount == 2){
                    //cardDao.findById(cardId).progress = cardDao.findById(cardId).progress + 2
                    updateProgress(3)
                } else {
                    //cardDao.findById(cardId).progress = cardDao.findById(cardId).progress + 3
                    updateProgress(5)
                }
                // Calculate the next review time based on successful reviews
                val nextReviewTime = calculateNextReviewTime(newReviewCount)
                val nextReviewMillis = System.currentTimeMillis() + nextReviewTime

                _uiState.update { currentState ->
                    currentState.copy(
                        currentCardIndex = currentCardIndex + 1,
                        nextReviewTimeMillis = nextReviewMillis
                    )
                }
                //System.out.println("curr card:" + currentCardIndex)
            } else {
                System.out.println("heyo is this working?")
                currentCardIndex = studyProgress.size
            }
        }
    }
    private fun updateProgress(newValue: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                card = currentState.studyList[currentCardIndex].copy(progress = newValue)
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            cardDao.updateCards(_uiState.value.card)
        }
    }

    // Helper function to calculate next review time based on successful reviews
    private fun calculateNextReviewTime(reviewCount: Int): Long {
        if(reviewCount == 1){
            return 60_000L
        } else if(reviewCount == 2){
            return 10*60_000L
        } else {
            return 24*3600_000L
        }

    }


}

data class SpacedDeckDetailsState(
    val studyList: List<CardEntity> = emptyList(),
    val deck: DeckEntity = DeckEntity(name = "", categoryId = null, description = "", theme = ""),
    val nextReviewTimeMillis: Long = 0,
    var currentCardIndex: Int = 0,
    var card: CardEntity = CardEntity(id = 0, front = "", back = "", font = "", deckId = 0, tags = "", progress = 0)
)
data class StudyProgressItem(
    val cardId: Long = 0,
    val reviewCount: Int = 0
)




