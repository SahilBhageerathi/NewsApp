package com.example.newsapp.presentation.customLiveData

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

class CounterViewModel : ViewModel() {
    val liveData = MyLiveData(0)
}

@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    val owner = LocalLifecycleOwner.current
    var count by rememberSaveable { mutableIntStateOf(viewModel.liveData.temp) }

    LaunchedEffect(Unit) {
        viewModel.liveData.observe(owner) { newVal ->
            count = newVal
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Count: $count",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.liveData.temp++ }) {
            Text("Increment")
        }
    }
}