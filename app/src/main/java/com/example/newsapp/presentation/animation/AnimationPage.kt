package com.example.newsapp.presentation.animation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun AnimationPage(){

    var isAtTop by remember { mutableStateOf(true) }

    val offsetY by animateDpAsState(
        targetValue = if (isAtTop) 0.dp else LocalConfiguration.current.screenHeightDp.dp - 100.dp,
        animationSpec = tween(durationMillis = 500)
    )

    var isAtLeft by remember{ mutableStateOf(true)}

    val offsetX by animateDpAsState(
        targetValue = if(isAtLeft) 0.dp else LocalConfiguration.current.screenWidthDp.dp-100.dp,
        tween(durationMillis = 500)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = offsetX,y= 100.dp)
                .size(80.dp)
                .background(Color.Blue, RoundedCornerShape(12.dp))
                .clickable { isAtLeft = !isAtLeft }
        )
    }
    Box(modifier = Modifier.height(50.dp))

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(y = offsetY,x= 200.dp)
                .size(80.dp)
                .background(Color.Red, RoundedCornerShape(12.dp))
                .clickable { isAtTop = !isAtTop }
        )
    }

}