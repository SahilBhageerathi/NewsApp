package com.example.newsapp.presentation.performance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

private const val ITEM_COUNT = 200

private const val HEAVY_WORK_ITERATIONS = 1_000_000

data class RowData1(val id: Int, val title: String, val computed: Long)

private fun expensiveComputation(seed: Int): Long {
    var acc = 0L
    for (i in 0 until HEAVY_WORK_ITERATIONS) {
        acc += (sqrt((i + seed).toDouble()) * 1.0001).toLong()
    }
    return acc
}


@Composable
fun JankyListScreen2() {
    val ticker = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            ticker.intValue++
            delay(100)
        }
    }

    // Precompute all values ONCE, in the background, before the list renders
    val rows by produceState<List<RowData1>?>(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            List(ITEM_COUNT) { id ->
                RowData1(
                    id = id,
                    title = "Row #$id",
                    computed = expensiveComputation(seed = id)
                )
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        TickerText(ticker)

        if (rows == null) {
            Text("Loading...", Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.testTag("janky_list")) {
                items(items = rows!!, key = { it.id }) { row ->
                    FixedRow(row = row)
                }
            }
        }
    }
}

@Composable
private fun TickerText(ticker: IntState) {
    Text(
        text = "Updates: ${ticker.intValue}",   // the ONLY place ticker is read
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
private fun FixedRow(row: RowData1) {
    // Just reads a stable value — no coroutine, no state updates, zero scroll cost
    ListItem(
        headlineContent = { Text(row.title) },
        supportingContent = { Text("computed: ${row.computed}") },
    )
    HorizontalDivider()
}