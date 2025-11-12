package com.ghostdev.coinbit.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ghostdev.coinbit.domain.model.ChartData
import com.ghostdev.coinbit.ui.theme.*
import kotlin.math.abs

@Composable
fun PriceLineChart(
    chartData: ChartData,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true
) {
    val lineColor = if (isPositive) GreenPositive else RedNegative
    val gradientStartColor = if (isPositive) GreenPositive.copy(alpha = 0.3f) else RedNegative.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .background(CardBackground)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val priceEntries = chartData.prices
            if (priceEntries.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            val spacing = width / (priceEntries.size - 1).coerceAtLeast(1)

            // Extract price values
            val priceValues = priceEntries.map { it.value }

            // Find min and max for scaling
            val minPrice = priceValues.minOrNull() ?: 0.0
            val maxPrice = priceValues.maxOrNull() ?: 1.0
            val priceRange = abs(maxPrice - minPrice).coerceAtLeast(0.001)

            // Create points for the line
            val points = priceValues.mapIndexed { index, price ->
                val x = index * spacing
                val y = height - ((price - minPrice) / priceRange * height).toFloat()
                Offset(x, y)
            }

            // Draw gradient fill under the line
            val fillPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, height)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                    lineTo(points.last().x, height)
                    close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientStartColor,
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw the line
            val linePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
