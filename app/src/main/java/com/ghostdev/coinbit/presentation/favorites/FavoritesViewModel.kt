package com.ghostdev.coinbit.presentation.favorites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.usecase.GetFavoriteCoinsUseCase
import com.ghostdev.coinbit.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class FavoritesState(
    val favoriteCoins: List<Coin> = emptyList(),
    val isLoading: Boolean = false
)

sealed class FavoritesEvent {
    data class RemoveFavorite(val coinId: String) : FavoritesEvent()
    data object Refresh : FavoritesEvent()
}

class FavoritesViewModel(
    private val getFavoriteCoinsUseCase: GetFavoriteCoinsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _state = mutableStateOf(FavoritesState())
    val state: State<FavoritesState> = _state
    
    init {
        loadFavorites()
    }
    
    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.RemoveFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.coinId)
                    // The Flow will automatically update the UI
                }
            }
            FavoritesEvent.Refresh -> {
                loadFavorites()
            }
        }
    }
    
    private fun loadFavorites() {
        getFavoriteCoinsUseCase().onEach { coins ->
            _state.value = _state.value.copy(
                favoriteCoins = coins,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }
}
