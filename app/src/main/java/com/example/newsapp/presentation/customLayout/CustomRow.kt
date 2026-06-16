package com.example.newsapp.presentation.customLayout

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun CustomRow() {

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
fun rowLayOut(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Layout(
        modifier = modifier,
        content =content
    ){ measurables, constraints ->

        val placables = measurables.map{measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        var width = 0
        var height = 0

        layout(constraints.maxWidth, constraints.maxHeight){

            placables.forEach {
                it.place(x=width,y= height)
                width += it.width
            }
        }

    }
}