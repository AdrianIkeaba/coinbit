package com.ghostdev.coinbit.presentation.coinlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.presentation.components.ShimmerCoinListLoading
import com.ghostdev.coinbit.presentation.navigation.Screen
import com.ghostdev.coinbit.ui.theme.AccentPink
import com.ghostdev.coinbit.ui.theme.BackgroundDark
import com.ghostdev.coinbit.ui.theme.CardBackground
import com.ghostdev.coinbit.ui.theme.DividerColor
import com.ghostdev.coinbit.ui.theme.GreenPositive
import com.ghostdev.coinbit.ui.theme.PrimaryPurple
import com.ghostdev.coinbit.ui.theme.RedNegative
import com.ghostdev.coinbit.ui.theme.SurfaceDark
import com.ghostdev.coinbit.ui.theme.SurfaceVariant
import com.ghostdev.coinbit.ui.theme.TextPrimary
import com.ghostdev.coinbit.ui.theme.TextSecondary
import com.ghostdev.coinbit.ui.theme.TextTertiary
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CoinListScreen(
    navController: NavController,
    viewModel: CoinListViewModel = koinViewModel()
) {
    val state = viewModel.state.value
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CoinBit",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorites Button
                        if (!state.isLoading) {
                            IconButton(
                                onClick = { navController.navigate(Screen.Favorites.route) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorites",
                                    tint = AccentPink
                                )
                            }
                        }
                        
                        // Refresh Button
                        if (!state.isLoading) {
                            IconButton(
                                onClick = { viewModel.onEvent(CoinListEvent.Refresh) },
                                enabled = !state.isRefreshing
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = if (state.isRefreshing) TextTertiary else PrimaryPurple
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(CoinListEvent.SearchQueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search coins...", color = TextTertiary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = DividerColor,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryPurple
                    ),
                    singleLine = true
                )
            }
        },
        containerColor = BackgroundDark,
        modifier = Modifier
            .safeDrawingPadding()
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onEvent(CoinListEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.isLoading) {
                    ShimmerCoinListLoading()
                } else if (state.error.isNotBlank()) {
                    ErrorMessage(
                        onRetry = { viewModel.onEvent(CoinListEvent.Refresh) }
                    )
                } else if (state.coins.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.coins,
                            key = { it.id }
                        ) { coin ->
                            CoinListItem(
                                coin = coin,
                                onItemClick = {
                                    navController.navigate(Screen.CoinDetail.createRoute(coin.id))
                                },
                                onFavoriteClick = {
                                    viewModel.onEvent(CoinListEvent.ToggleFavorite(coin.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinListItem(
    coin: Coin,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val priceChange = coin.priceChangePercentage24h ?: 0.0
    val isPositive = priceChange >= 0
    val changeColor = if (isPositive) GreenPositive else RedNegative
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin Image
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Coin Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coin.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = coin.symbol.uppercase(),
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    
                    coin.marketCapRank?.let { rank ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "#$rank",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            modifier = Modifier
                                .background(SurfaceVariant, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Price and Change
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatPrice(coin.currentPrice),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format(Locale.ENGLISH, "%.2f", priceChange)}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = changeColor
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Favorite Icon
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (coin.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (coin.isFavorite) AccentPink else TextSecondary
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
        Spacer(modifier = Modifier.height(16.dp))
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

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No coins found",
            color = TextSecondary,
            fontSize = 16.sp
        )
    }
}

private fun formatPrice(price: Double): String {
    return when {
        price >= 1 -> {
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            formatter.format(price)
        }
        else -> {
            String.format("$%.6f", price)
        }
    }
}
