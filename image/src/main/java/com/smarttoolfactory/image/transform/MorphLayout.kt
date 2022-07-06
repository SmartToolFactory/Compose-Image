package com.smarttoolfactory.image.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.PointerRequisite
import com.smarttoolfactory.gesture.detectPointerTransformGestures

/**
 * Composable that changes dimensions of its content from handles, translates its position
 * when dragged inside bounds
 */
@Composable
fun MorphLayout(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    handlePlacement: HandlePlacement = HandlePlacement.Corner,
    updatePhysicalSize: Boolean = false,
    onDown: () -> Unit = {},
    onMove: (DpSize) -> Unit = {},
    onUp: () -> Unit = {},
    content: @Composable () -> Unit
) {
    MorphSubcomposeLayout(
        modifier = containerModifier,
        handleRadius = handleRadius,
        updatePhysicalSize = updatePhysicalSize,
        mainContent = {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        dependentContent = { intSize: IntSize ->

            val dpSize = with(LocalDensity.current) {
                val rawWidth = intSize.width.toDp()
                val rawHeight = intSize.height.toDp()
                DpSize(rawWidth, rawHeight)
            }

            MorphLayout(
                handleRadius = handleRadius,
                enabled = enabled,
                dpSize = dpSize,
                handlePlacement = handlePlacement,
                onDown = onDown,
                onMove = onMove,
                onUp = onUp,
                content = content
            )
        }
    )
}

@Composable
private fun MorphLayout(
    enabled: Boolean = true,
    handleRadius: Dp,
    dpSize: DpSize,
    handlePlacement: HandlePlacement,
    onDown: () -> Unit = {},
    onMove: (DpSize) -> Unit = {},
    onUp: () -> Unit = {},
    content: @Composable () -> Unit
) {

    val touchRegionRadius: Float
    val minDimension: Float
    val size: Size

    val initialSize = remember {
        DpSize(
            dpSize.width + handleRadius * 2,
            dpSize.height + handleRadius * 2
        )
    }

    var updatedSize by remember {
        mutableStateOf(initialSize)
    }

    with(LocalDensity.current) {
        touchRegionRadius = handleRadius.toPx()
        minDimension = (touchRegionRadius * if (handlePlacement == HandlePlacement.Corner) 4 else 6)
        size = updatedSize.toSize()
    }

    val rectDraw = remember(updatedSize) {
        Rect(offset = Offset.Zero, size = size)
    }

    val editModifier = Modifier
        .morph(
            enabled = enabled,
            initialSize = initialSize,
            touchRegionRadius = touchRegionRadius,
            minDimension = minDimension,
            handlePlacement = handlePlacement,
            onDown = onDown,
            onMove = { dpSizeChange: DpSize ->
                updatedSize = dpSizeChange
                onMove(updatedSize)
            },
            onUp = onUp
        )

    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformModifier = Modifier
        .padding(handleRadius)
        .fillMaxSize()
        .clipToBounds()
        .graphicsLayer {
            translationX = offset.x
            translationY = offset.y
            scaleX = zoom
            scaleY = zoom
        }
        .pointerInput(Unit) {
            detectPointerTransformGestures(
                requisite = PointerRequisite.GreaterThan,
                numberOfPointers = 1,
                onGesture = { _,
                              gesturePan: Offset,
                              gestureZoom: Float,
                              _,
                              _,
                              _ ->
                    val newScale = (zoom * gestureZoom).coerceIn(1f, 3f)
                    val newOffset = offset + gesturePan
                    zoom = newScale

                    val maxX = (size.width * (zoom - 1) / 2f)
                    val maxY = (size.height * (zoom - 1) / 2f)

                    offset = Offset(
                        newOffset.x.coerceIn(-maxX, maxX),
                        newOffset.y.coerceIn(-maxY, maxY)
                    )
                }
            )
        }

    ResizeImpl(
        modifier = editModifier,
        transformModifier = transformModifier,
        touchRegionRadius = touchRegionRadius,
        rectDraw = rectDraw,
        handlePlacement = handlePlacement,
        content = content
    )
}

@Composable
private fun ResizeImpl(
    modifier: Modifier,
    transformModifier: Modifier,
    touchRegionRadius: Float,
    rectDraw: Rect,
    handlePlacement: HandlePlacement,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = transformModifier,
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        HandleOverlay(
            modifier = Modifier.fillMaxSize(),
            radius = touchRegionRadius,
            rectDraw = rectDraw,
            handlePlacement = handlePlacement
        )
    }
}
