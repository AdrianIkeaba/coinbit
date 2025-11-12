package com.ghostdev.coinbit.presentation.coinlist

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.usecase.GetCoinsUseCase
import com.ghostdev.coinbit.domain.usecase.SearchCoinsUseCase
import com.ghostdev.coinbit.domain.usecase.ToggleFavoriteUseCase
import com.ghostdev.coinbit.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<Coin> = emptyList(),
    val error: String = "",
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)

class CoinListViewModel(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val searchCoinsUseCase: SearchCoinsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _state = mutableStateOf(CoinListState())
    val state: State<CoinListState> = _state
    
    private var searchJob: Job? = null
    
    init {
        getCoins()
    }
    
    fun onEvent(event: CoinListEvent) {
        when (event) {
            is CoinListEvent.Refresh -> {
                getCoins(forceRefresh = true)
            }
            is CoinListEvent.SearchQueryChanged -> {
                _state.value = state.value.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(300) // Debounce search
                    searchCoins(event.query)
                }
            }
            is CoinListEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.coinId)
                    // Refresh the current view to show updated favorite status
                    if (state.value.searchQuery.isBlank()) {
                        // If not searching, reload from database to get updated favorite status
                        searchCoins("")
                    } else {
                        // If searching, re-run search to get updated results
                        searchCoins(state.value.searchQuery)
                    }
                }
            }
        }
    }
    
    private fun getCoins(forceRefresh: Boolean = false) {
        Log.d("CoinListViewModel", "getCoins called with forceRefresh=$forceRefresh")
        getCoinsUseCase(forceRefresh).onEach { result ->
            Log.d("CoinListViewModel", "Received result: ${result::class.simpleName}")
            when (result) {
                is Resource.Success -> {
                    Log.d("CoinListViewModel", "Success: ${result.data?.size ?: 0} coins")
                    _state.value = state.value.copy(
                        coins = result.data ?: emptyList(),
                        isLoading = false,
                        isRefreshing = false,
                        error = ""
                    )
                }
                is Resource.Error -> {
                    Log.e("CoinListViewModel", "Error: ${result.message}")
                    _state.value = state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        isRefreshing = false
                    )
                }
                is Resource.Loading -> {
                    Log.d("CoinListViewModel", "Loading state")
                    _state.value = state.value.copy(
                        isLoading = !forceRefresh,
                        isRefreshing = forceRefresh
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
    
    private fun searchCoins(query: String) {
        // Always use searchCoinsUseCase to avoid triggering API calls
        // Empty query will search all coins from local database
        searchCoinsUseCase(query).onEach { coins ->
            _state.value = state.value.copy(
                coins = coins,
                isLoading = false,
                error = ""
            )
        }.launchIn(viewModelScope)
    }
}

sealed class CoinListEvent {
    data object Refresh : CoinListEvent()
    data class SearchQueryChanged(val query: String) : CoinListEvent()
    data class ToggleFavorite(val coinId: String) : CoinListEvent()
}
