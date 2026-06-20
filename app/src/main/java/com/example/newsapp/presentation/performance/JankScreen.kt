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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

/**
 * A DELIBERATELY janky list. This is the "before" baseline for performance work.
 *
 * It bundles three of the most common, real-world causes of jank in Compose, each
 * tagged below as a JANK SOURCE. Run it on a real device in a RELEASE build, fling
 * the list hard, and you should feel the stutter. Debug builds are even slower but
 * give misleading numbers, so always measure in release.
 *
 * Tune HEAVY_WORK_ITERATIONS for your device: if scrolling feels smooth, raise it;
 * if the app fully freezes, lower it. We want "janky but usable", not frozen.
 */

private const val ITEM_COUNT = 200
private const val HEAVY_WORK_ITERATIONS = 1_000_000

data class RowData(val id: Int, val title: String)

/**
 * A CPU-bound busy loop that runs on whatever thread calls it. Called from a
 * composable below, it therefore runs on the UI thread during composition and
 * blocks the frame until it finishes. The result is returned and shown on screen
 * so the compiler can't optimize the loop away.
 */
private fun expensiveComputation(seed: Int): Long {
    var acc = 0L
    for (i in 0 until HEAVY_WORK_ITERATIONS) {
        acc += (sqrt((i + seed).toDouble()) * 1.0001).toLong()
    }
    return acc
}

@Composable
fun JankyListScreen() {
    // JANK SOURCE #1 — over-recomposition from a high-frequency state read at the top.
    // `ticker` changes ~10x/second. Because it's read by the Text below (which lives
    // in the same scope that builds the list), the surrounding content keeps
    // recomposing. Combined with #3 this turns idle time into wasted work.
    val ticker = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            ticker.intValue++
            delay(100)
        }
    }

    val rows = remember { List(ITEM_COUNT) { RowData(it, "Row #$it") } }

    Column(Modifier.fillMaxSize()) {
        TickerText(ticker)

        LazyColumn(modifier = Modifier.testTag("janky_list")) {
            // JANK SOURCE #2 — no stable `key`. Without keys, Compose cannot match an
            // item to its previous instance across changes, so it can't preserve or
            // reuse item state efficiently. The fix later will add: key = { it.id }
            items(
                items = rows,
                key = { row -> row.id }) { row ->
                JankyRow(row = row)
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
private fun JankyRow(row: RowData) {
    // JANK SOURCE #3 — expensive work performed DURING composition, on the UI thread.
    // Every time this row enters composition (e.g. scrolled into view), it blocks the
    // frame for the duration of expensiveComputation(). When a fling brings several
    // new rows on screen in one frame, the combined work blows past the ~16.7ms budget
    // and frames are dropped -> visible stutter.

    val result by produceState<Long?>(initialValue = null, key1 = row.id) {
        value = withContext(Dispatchers.Default) {
            expensiveComputation(seed = row.id)
        }
    }


    ListItem(
        headlineContent = { Text(row.title) },
        supportingContent = { Text("computed: $result") },
    )
    HorizontalDivider()
}

@Preview
@Composable
private fun JankyListScreenPreview() {
    JankyListScreen()
}


//initial numbers
//ExampleStartupBenchmark_scrollJankyList
//frameDurationCpuMs   P50   10.5,   P90   29.5,   P95   59.2,   P99  268.0
//Traces: Iteration 0 1 2 3 4

//-------------------------AFTER USING COROUTINE IN JANK 3 BELOW IS ORIGINAL CODE--------------
//@Composable
//private fun JankyRow(row: RowData) {
//    // JANK SOURCE #3 — expensive work performed DURING composition, on the UI thread.
//    // Every time this row enters composition (e.g. scrolled into view), it blocks the
//    // frame for the duration of expensiveComputation(). When a fling brings several
//    // new rows on screen in one frame, the combined work blows past the ~16.7ms budget
//    // and frames are dropped -> visible stutter.
//    val result = expensiveComputation(seed = row.id)
//
//    ListItem(
//        headlineContent = { Text(row.title) },
//        supportingContent = { Text("computed: $result") },
//    )
//    HorizontalDivider()
//}

//ExampleStartupBenchmark_scrollJankyList
//frameDurationCpuMs   P50  10.3,   P90  18.9,   P95  25.3,   P99  34.3
//Traces: Iteration 0 1 2 3 4

//-------------------------------------------AFTER CLEARING JANK 2 :BELOW IS JANK 2 ORIGINAL CODE------------
//LazyColumn(modifier = Modifier.testTag("janky_list")) {
//    // JANK SOURCE #2 — no stable `key`. Without keys, Compose cannot match an
//    // item to its previous instance across changes, so it can't preserve or
//    // reuse item state efficiently. The fix later will add: key = { it.id }
//    items(
//        items = rows,
//        key = { row -> row.id }) { row ->
//        JankyRow(row = row)
//    }
//}

//--------------------------------AFTER FIXING JANK 1-----------------------
//ExampleStartupBenchmark_scrollJankyList
//frameDurationCpuMs   P50  10.8,   P90  20.3,   P95  24.7,   P99  29.4
//Mi A3 - 11 Tests 1/1 completed. (0 skipped) (0 failed)
//Traces: Iteration 0 1 2 3 4

