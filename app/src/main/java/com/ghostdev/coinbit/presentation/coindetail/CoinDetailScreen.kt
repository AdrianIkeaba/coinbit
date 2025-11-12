package com.ghostdev.coinbit.presentation.coindetail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ghostdev.coinbit.domain.model.CoinDetail
import com.ghostdev.coinbit.presentation.components.PriceLineChart
import com.ghostdev.coinbit.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    navController: NavController,
    viewModel: CoinDetailViewModel = koinViewModel()
) {
    val state = viewModel.state.value
    
    LaunchedEffect(coinId) {
        viewModel.onEvent(CoinDetailEvent.LoadCoin(coinId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.coinDetail?.name ?: "Loading...",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    state.coinDetail?.let { coin ->
                        IconButton(
                            onClick = { viewModel.onEvent(CoinDetailEvent.ToggleFavorite) }
                        ) {
                            Icon(
                                imageVector = if (coin.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (coin.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (coin.isFavorite) AccentPink else TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    LoadingState()
                }
                state.error != null -> {
                    ErrorState(
                        onRetry = { viewModel.onEvent(CoinDetailEvent.Retry) }
                    )
                }
                state.coinDetail != null -> {
                    CoinDetailContent(
                        coinDetail = state.coinDetail,
                        chartData = state.chartData,
                        selectedTimeRange = state.selectedTimeRange,
                        onTimeRangeSelected = { timeRange ->
                            viewModel.onEvent(CoinDetailEvent.TimeRangeSelected(timeRange))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CoinDetailContent(
    coinDetail: CoinDetail,
    chartData: com.ghostdev.coinbit.domain.model.ChartData?,
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with coin image and basic info
        CoinHeader(coinDetail)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Price Chart Section
        ChartSection(
            chartData = chartData,
            selectedTimeRange = selectedTimeRange,
            onTimeRangeSelected = onTimeRangeSelected,
            isPositiveChange = (coinDetail.priceChangePercentage24h ?: 0.0) >= 0
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Statistics Grid
        StatisticsSection(coinDetail)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Additional Info
        AdditionalInfoSection(coinDetail)
    }
}

@Composable
fun CoinHeader(coinDetail: CoinDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coinDetail.image,
                contentDescription = coinDetail.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coinDetail.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = coinDetail.symbol.uppercase(),
                    fontSize = 16.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = formatPrice(coinDetail.currentPrice),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val changePercentage = coinDetail.priceChangePercentage24h ?: 0.0
                    val isPositive = changePercentage >= 0
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", changePercentage)}%",
                        color = if (isPositive) GreenPositive else RedNegative,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "24h",
                        color = TextTertiary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChartSection(
    chartData: com.ghostdev.coinbit.domain.model.ChartData?,
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
    isPositiveChange: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Price Chart",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time Range Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeRange.entries.forEach { timeRange ->
                    TimeRangeChip(
                        timeRange = timeRange,
                        isSelected = timeRange == selectedTimeRange,
                        onClick = { onTimeRangeSelected(timeRange) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart
            if (chartData != null && chartData.prices.isNotEmpty()) {
                PriceLineChart(
                    chartData = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    isPositive = isPositiveChange
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = PrimaryPurple,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeRangeChip(
    timeRange: TimeRange,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) PrimaryPurple else SurfaceVariant,
            contentColor = if (isSelected) TextPrimary else TextSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = timeRange.label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun StatisticsSection(coinDetail: CoinDetail) {
    Text(
        text = "Statistics",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Market Cap",
                value = formatLargeNumber(coinDetail.marketCap),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Volume 24h",
                value = formatLargeNumber(coinDetail.totalVolume),
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "24h High",
                value = formatPrice(coinDetail.high24h),
                modifier = Modifier.weight(1f),
                valueColor = GreenPositive
            )
            StatCard(
                title = "24h Low",
                value = formatPrice(coinDetail.low24h),
                modifier = Modifier.weight(1f),
                valueColor = RedNegative
            )
        }
        
        coinDetail.marketCapRank?.let { rank ->
            if (rank > 0) {
                StatCard(
                    title = "Market Cap Rank",
                    value = "#$rank",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AdditionalInfoSection(coinDetail: CoinDetail) {
    Text(
        text = "Additional Information",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            coinDetail.circulatingSupply?.let { supply ->
                if (supply > 0) {
                    InfoRow(
                        label = "Circulating Supply",
                        value = formatLargeNumber(supply)
                    )
                }
            }
            coinDetail.totalSupply?.let { supply ->
                if (supply > 0) {
                    InfoRow(
                        label = "Total Supply",
                        value = formatLargeNumber(supply)
                    )
                }
            }
            coinDetail.maxSupply?.let { supply ->
                if (supply > 0) {
                    InfoRow(
                        label = "Max Supply",
                        value = formatLargeNumber(supply)
                    )
                }
            } ?: run {
                InfoRow(
                    label = "Max Supply",
                    value = "∞ Unlimited"
                )
            }
            if (coinDetail.ath > 0) {
                InfoRow(
                    label = "All-Time High",
                    value = formatPrice(coinDetail.ath)
                )
            }
            if (coinDetail.atl > 0) {
                InfoRow(
                    label = "All-Time Low",
                    value = formatPrice(coinDetail.atl)
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = PrimaryPurple,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading coin details...",
                color = TextSecondary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Unable to pull data. Please try again.",
                color = TextSecondary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple
                )
            ) {
                Text("Retry")
            }
        }
    }
}

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(price)
}

fun formatLargeNumber(number: Double): String {
    return when {
        number >= 1_000_000_000_000 -> String.format("$%.2fT", number / 1_000_000_000_000)
        number >= 1_000_000_000 -> String.format("$%.2fB", number / 1_000_000_000)
        number >= 1_000_000 -> String.format("$%.2fM", number / 1_000_000)
        number >= 1_000 -> String.format("$%.2fK", number / 1_000)
        else -> String.format("$%.2f", number)
    }
}

fun formatLargeNumber(number: Long): String {
    return formatLargeNumber(number.toDouble())
}
