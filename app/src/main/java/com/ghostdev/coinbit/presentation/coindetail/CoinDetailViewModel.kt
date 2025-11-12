package com.ghostdev.coinbit.presentation.coindetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.coinbit.domain.model.ChartData
import com.ghostdev.coinbit.domain.model.CoinDetail
import com.ghostdev.coinbit.domain.usecase.GetCoinDetailUseCase
import com.ghostdev.coinbit.domain.usecase.GetMarketChartUseCase
import com.ghostdev.coinbit.domain.usecase.ToggleFavoriteUseCase
import com.ghostdev.coinbit.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CoinDetailState(
    val isLoading: Boolean = true,
    val coinDetail: CoinDetail? = null,
    val chartData: ChartData? = null,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.WEEK
)

enum class TimeRange(val days: Int, val label: String) {
    DAY(1, "24H"),
    WEEK(7, "7D"),
    MONTH(30, "30D"),
    YEAR(365, "1Y")
}

sealed class CoinDetailEvent {
    data class LoadCoin(val coinId: String) : CoinDetailEvent()
    data class TimeRangeSelected(val timeRange: TimeRange) : CoinDetailEvent()
    object ToggleFavorite : CoinDetailEvent()
    object Retry : CoinDetailEvent()
}

class CoinDetailViewModel(
    private val getCoinDetailUseCase: GetCoinDetailUseCase,
    private val getMarketChartUseCase: GetMarketChartUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _state = mutableStateOf(CoinDetailState())
    val state: State<CoinDetailState> = _state
    
    private var currentCoinId: String? = null
    private var chartLoadJob: Job? = null
    
    fun onEvent(event: CoinDetailEvent) {
        when (event) {
            is CoinDetailEvent.LoadCoin -> {
                currentCoinId = event.coinId
                loadCoinDetail(event.coinId)
                loadChartData(event.coinId, _state.value.selectedTimeRange.days)
            }
            is CoinDetailEvent.TimeRangeSelected -> {
                _state.value = _state.value.copy(selectedTimeRange = event.timeRange)
                // Cancel any pending chart load to prevent duplicate calls
                chartLoadJob?.cancel()
                currentCoinId?.let { coinId ->
                    // Debounce chart loading to prevent rapid API calls
                    chartLoadJob = viewModelScope.launch {
                        delay(300) // 300ms debounce
                        loadChartData(coinId, event.timeRange.days)
                    }
                }
            }
            CoinDetailEvent.ToggleFavorite -> {
                currentCoinId?.let { coinId ->
                    viewModelScope.launch {
                        toggleFavoriteUseCase(coinId)
                        // Reload coin detail to get updated favorite status
                        loadCoinDetail(coinId)
                    }
                }
            }
            CoinDetailEvent.Retry -> {
                currentCoinId?.let { coinId ->
                    loadCoinDetail(coinId)
                    loadChartData(coinId, _state.value.selectedTimeRange.days)
                }
            }
        }
    }
    
    private fun loadCoinDetail(coinId: String) {
        getCoinDetailUseCase(coinId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        coinDetail = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
    
    private fun loadChartData(coinId: String, days: Int) {
        getMarketChartUseCase(coinId, days).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    // Don't set loading state for chart to avoid disrupting the UI
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(chartData = result.data)
                }
                is Resource.Error -> {
                    // Chart error doesn't affect the main state
                }
            }
        }.launchIn(viewModelScope)
    }
}
