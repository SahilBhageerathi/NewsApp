package com.example.newsapp.presentation.StockScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.newsapp.domain.model.Stock
import com.example.newsapp.presentation.viewmodel.StockUiState
import com.example.newsapp.presentation.viewmodel.StockViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    symbol: String,
    viewModel: StockViewModel,
    onBack: () -> Unit,
) {
    // find the stock already loaded in the ViewModel
    val uiState by viewModel.stocks.collectAsStateWithLifecycle()
    val stock = (uiState as? StockUiState.Success)?.stocks?.find { it.symbol == symbol }

    if (stock == null) {
        LoadingState()
        return
    }

    val isUp = stock.change >= 0
    val accentColor = if (isUp) Color(0xFF1D9E75) else Color(0xFFD85A30)

    Scaffold(
        topBar = {
            TopBar(stock = stock, accentColor = accentColor, onBack = onBack)
        },
        bottomBar = {
            BottomActionBar(accentColor = accentColor)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PriceHero(stock = stock, accentColor = accentColor)
            StatsGrid(stock = stock)
            AboutCard(stock = stock)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    stock: Stock,
    accentColor: Color,
    onBack: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Column {
                Text(
                    text = stock.symbol,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Text(
                    text = stock.exchange,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Alerts")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun PriceHero(stock: Stock, accentColor: Color) {
    val isUp = stock.change >= 0
    val sign = if (isUp) "+" else ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "$${stock.price.format2()}",
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
            )
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ChangeBadge(isUp = isUp, changePercent = stock.changePercent)
                Text(
                    text = "$sign${stock.change.format2()} today",
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(stock: Stock) {
    val stats = listOf(
        "Open"       to "$${stock.open.format2()}",
        "Prev close" to "$${stock.prevClose.format2()}",
        "Day high"   to "$${stock.high.format2()}",
        "Day low"    to "$${stock.low.format2()}",
    )

    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Key stats",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                stats.take(2).forEach { (label, value) ->
                    StatCard(label = label, value = value)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                stats.drop(2).forEach { (label, value) ->
                    StatCard(label = label, value = value)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun AboutCard(stock: Stock) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stock.name,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
            if (stock.industry.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stock.industry,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (stock.logoUrl.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = stock.logoUrl,
                    contentDescription = "${stock.name} logo",
                    modifier = Modifier.height(32.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(accentColor: Color) {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Buy", fontSize = 15.sp)
            }
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Watch")
            }
        }
    }
}