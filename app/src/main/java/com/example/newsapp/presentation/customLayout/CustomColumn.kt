package com.example.newsapp.presentation.customLayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun CustomColumn() {

    DiagonalPlacement(
        modifier = Modifier.fillMaxSize()
            .padding(0.dp),
        content = {
            repeat(100) {
                Box(modifier = Modifier
                    .size(24.dp)
                    .background(color = Color.Cyan))
            }
        })

}


@Composable
fun DiagonalPlacement(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        // 1. Measure every child.
        // We pass the incoming constraints so children size themselves normally.
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
//            measurable.measure(constraints)
        }

        // 2. Decide our own size.
        // For a diagonal, the total width/height is the sum of each child's
        // width/height, since each one is shifted right & down by the previous.
        val width = placeables.sumOf { it.width }
        val height = placeables.sumOf { it.height }

        // 3. Place the children.
        layout(
            width  = width.coerceIn(constraints.minWidth, constraints.maxWidth),
            height = height.coerceIn(constraints.minHeight, constraints.maxHeight)
//            width,height
        ) {
            var x = 0
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = x, y = y)
                x += placeable.width   // move right by this child's width
                y += placeable.height  // move down by this child's height
            }
        }

    }

}