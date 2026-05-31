package com.example.newsapp.presentation.StockScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsapp.domain.model.Stock
import com.example.newsapp.presentation.viewmodel.StockUiState
import com.example.newsapp.presentation.viewmodel.StockViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun WatchlistScreen(
    viewModel: StockViewModel = koinViewModel(),
    onStockClick: (String) -> Unit,
) {
    val uiState by viewModel.stocks.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar()

        when (val state = uiState) {
            is StockUiState.Loading -> LoadingState()
            is StockUiState.Error   -> ErrorState(state.message) { viewModel.getWatchlist() }
            is StockUiState.Success -> StockList(state.stocks, onStockClick)
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Markets",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Live · Finnhub",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StockList(
    stocks: List<Stock>,
    onStockClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    ) {
        items(stocks, key = { it.symbol }) { stock ->
            StockRow(stock = stock, onClick = { onStockClick(stock.symbol) })
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

@Composable
private fun StockRow(
    stock: Stock,
    onClick: () -> Unit,
) {
    val isUp = stock.change >= 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Symbol avatar
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stock.symbol.take(2),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }

        // Name + symbol
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stock.symbol,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
            Text(
                text = stock.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Price + change badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${stock.price.format2()}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(3.dp))
            ChangeBadge(isUp = isUp, changePercent = stock.changePercent)
        }
    }
}

@Composable
fun ChangeBadge(isUp: Boolean, changePercent: Double) {
    val bg  = if (isUp) Color(0xFFE8F8F0) else Color(0xFFFDECEA)
    val fg  = if (isUp) Color(0xFF1D9E75) else Color(0xFFD85A30)
    val sign = if (isUp) "+" else ""

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = "$sign${changePercent.format2()}%",
            fontSize = 11.sp,
            color = fg,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Something went wrong", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

// Extension
fun Double.format2() = "%.2f".format(this)