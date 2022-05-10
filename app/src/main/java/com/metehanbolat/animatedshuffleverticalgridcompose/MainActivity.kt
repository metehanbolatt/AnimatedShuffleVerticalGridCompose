package com.metehanbolat.animatedshuffleverticalgridcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.metehanbolat.animatedshuffleverticalgridcompose.ui.theme.AnimatedShuffleVerticalGridComposeTheme
import kotlin.math.roundToInt

private const val MIN_SIZE = 2
private const val MAX_SIZE = 6

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimatedShuffleVerticalGridComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AnimatedShuffleVerticalGridCompose()
                }
            }
        }
    }
}

@Composable
fun AnimatedShuffleVerticalGridCompose() {
    var columns by rememberSaveable {
        mutableStateOf((MIN_SIZE + MAX_SIZE) / 2)
    }
    var rows by rememberSaveable {
        mutableStateOf((MIN_SIZE + MAX_SIZE) / 2)
    }
    var items by remember(columns, rows) {
        mutableStateOf(createItems(columns * rows))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AnimatedVerticalGrid(
            items = items,
            itemKey = Item::id,
            columns = columns,
            rows = rows,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Item(item = it)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { items = items.shuffled() },
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .align(CenterHorizontally)
            ) {
                Text(text = "Shuffle")
            }
            ExtSlider(label = "Columns", value = columns, onChanged = { columns = it })
            ExtSlider(label = "Rows", value = rows, onChanged = { rows = it })
        }
    }
}

@Composable
fun Item(item: Item) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(item.color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = item.id.toString())
    }
}

@Composable
fun ExtSlider(
    label: String,
    value: Int,
    onChanged: (Int) -> Unit
) {
    var sliderValue by remember { mutableStateOf(value.toFloat()) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "$label: $value")

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onChanged(it.roundToInt())
            },
            valueRange = MIN_SIZE.toFloat()..MAX_SIZE.toFloat(),
            steps = MAX_SIZE - MIN_SIZE - 1
        )
    }
}
