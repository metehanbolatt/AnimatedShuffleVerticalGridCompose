package com.metehanbolat.animatedshuffleverticalgridcompose

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.launch

typealias ItemOffset = Animatable<DpOffset, AnimationVector2D>

fun ItemOffset(offset: DpOffset): ItemOffset = Animatable(offset, DpOffset.VectorConverter)

@Composable
fun <ITEM, KEY> AnimatedVerticalGrid(
    items: List<ITEM>,
    itemKey: (ITEM) -> KEY,
    columns: Int,
    rows: Int,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<DpOffset> = tween(1000),
    itemContent: @Composable (BoxScope.(ITEM) -> Unit),
) = BoxWithConstraints(modifier) {
    val itemKeys = items.map { itemKey(it) }
    val itemSize = remember(rows, columns) {
        val itemWidth = (maxWidth) / columns
        val itemHeight = (maxHeight) / rows
        DpSize(itemWidth, itemHeight)
    }

    val gridOffset = remember(rows, columns, itemSize) {
        (0 until rows).map { column ->
            (0 until columns).map { row ->
                DpOffset(
                    x = itemSize.width * row,
                    y = itemSize.height * column
                )
            }
        }.flatten()
    }

    var itemsOffset by remember { mutableStateOf(mapOf<KEY, ItemOffset>()) }
    key(itemKeys) {
        itemsOffset = items.mapIndexed { index, item ->
            val key = itemKey(item)
            key to when {
                itemsOffset.containsKey(key) -> itemsOffset.getValue(key)
                else -> ItemOffset(gridOffset[index])
            }
        }.toMap()
    }

    items.forEach { item ->
        val offset = itemsOffset.getValue(itemKey(item)).value
        Box(
            modifier = Modifier
                .size(itemSize)
                .offset(offset.x, offset.y)
        ) {
            itemContent(item)
        }
    }
    LaunchedEffect(itemKeys) {
        items.forEachIndexed { index, item ->
            val newOffset = gridOffset[index]
            val itemOffset = itemsOffset.getValue(itemKey(item))
            launch {
                itemOffset.animateTo(newOffset, animationSpec)
            }
        }
    }
}