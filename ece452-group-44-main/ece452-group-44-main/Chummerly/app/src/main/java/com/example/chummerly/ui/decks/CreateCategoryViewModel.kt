package com.example.chummerly.ui.decks

import androidx.lifecycle.ViewModel
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
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val categoryDao: CategoryDao
): ViewModel() {
    private val _uiState = MutableStateFlow(CreateCategoryState())
    val uiState: StateFlow<CreateCategoryState> = _uiState.asStateFlow();

    fun updateName(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                category = currentState.category.copy(name = newValue)
            )
        }
    }

    fun updateColor(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                category = currentState.category.copy(color = newValue)
            )
        }
    }

    fun saveCategory() {
        CoroutineScope(Dispatchers.IO).launch {
            if (_uiState.value.category.name != "") {
                categoryDao.insert(_uiState.value.category)
            }
        }
    }

}

data class CreateCategoryState(
    val category: Category = Category(name = "", color = "")
)
