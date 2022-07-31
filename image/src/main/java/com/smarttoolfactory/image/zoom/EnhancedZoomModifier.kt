package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.transform.TouchRegion
import com.smarttoolfactory.image.util.update
import kotlinx.coroutines.launch

fun Modifier.enhancedZoom(
    vararg keys: Any?,
    touchRegionSize: Float,
    minDimension: Float,
    enhancedZoomState: EnhancedZoomState,
    onDown: (EnhancedZoomData) -> Unit,
    onMove: (EnhancedZoomData) -> Unit,
    onUp: (EnhancedZoomData) -> Unit,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val transformModifier = Modifier.pointerInput(keys) {

            detectTransformGestures(
                consume = true,
                onGestureStart = {
                    onDown(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd()
                    }

                    onUp(enhancedZoomState.enhancedZoomData)
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {

                        enhancedZoomState.onGesture(
                            centroid,
                            pan,
                            zoom,
                            rotate,
                            mainPointer,
                            pointerList
                        )

                        onMove(enhancedZoomState.enhancedZoomData)
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(keys) {
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        enhancedZoomState.resetWithAnimation()
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            Modifier
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["touchRegionRadius"] = touchRegionSize
        properties["minDimension"] = minDimension
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
    }
)

internal fun moveIntoBounds(rectBounds: Rect, rectCurrent: Rect): Rect {
    var width = rectCurrent.width
    var height = rectCurrent.height


    if (width > rectBounds.width) {
        width = rectBounds.width
    }

    if (height > rectBounds.height) {
        height = rectBounds.height
    }

    var rect = Rect(offset = rectCurrent.topLeft, size = Size(width, height))

    if (rect.left < rectBounds.left) {
        rect = rect.translate(rectBounds.left - rect.left, 0f)
    }

    if (rect.top < rectBounds.top) {
        rect = rect.translate(0f, rectBounds.top - rect.top)
    }

    if (rect.right > rectBounds.right) {
        rect = rect.translate(rectBounds.right - rect.right, 0f)
    }

    if (rect.bottom > rectBounds.bottom) {
        rect = rect.translate(0f, rectBounds.bottom - rect.bottom)
    }

    return rect
}

/**
 * Update draw rect based on user touch
 */
fun updateDrawRect(
    distanceToEdgeFromTouch: Offset,
    touchRegion: TouchRegion,
    minDimension: Float,
    rectTemp: Rect,
    rectDraw: Rect,
    change: PointerInputChange
): Rect {

    val position = change.position
    // Get screen coordinates from touch position inside composable
    // and add how far it's from corner to not jump edge to user's touch position
    val screenPositionX = position.x + distanceToEdgeFromTouch.x
    val screenPositionY = position.y + distanceToEdgeFromTouch.y

    return when (touchRegion) {

        // Corners
        TouchRegion.TopLeft -> {

            // Set position of top left while moving with top left handle and
            // limit position to not intersect other handles
            val left = screenPositionX.coerceAtMost(rectTemp.right - minDimension)
            val top = screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)
            Rect(
                left = left,
                top = top,
                right = rectTemp.right,
                bottom = rectTemp.bottom
            )
        }

        TouchRegion.BottomLeft -> {

            // Set position of top left while moving with bottom left handle and
            // limit position to not intersect other handles
            val left = screenPositionX.coerceAtMost(rectTemp.right - minDimension)
            val bottom = screenPositionY.coerceAtLeast(rectTemp.top + minDimension)
            Rect(
                left = left,
                top = rectTemp.top,
                right = rectTemp.right,
                bottom = bottom,
            )

        }

        TouchRegion.TopRight -> {

            // Set position of top left while moving with top right handle and
            // limit position to not intersect other handles
            val right = screenPositionX.coerceAtLeast(rectTemp.left + minDimension)
            val top = screenPositionY.coerceAtMost(rectTemp.bottom - minDimension)

            Rect(
                left = rectTemp.left,
                top = top,
                right = right,
                bottom = rectTemp.bottom,
            )

        }

        TouchRegion.BottomRight -> {

            // Set position of top left while moving with bottom right handle and
            // limit position to not intersect other handles
            val right = screenPositionX.coerceAtLeast(rectTemp.left + minDimension)
            val bottom = screenPositionY.coerceAtLeast(rectTemp.top + minDimension)

            Rect(
                left = rectTemp.left,
                top = rectTemp.top,
                right = right,
                bottom = bottom
            )
        }

        TouchRegion.Inside -> {
            val drag = change.positionChange()

            val scaledDragX = drag.x
            val scaledDragY = drag.y

            rectDraw.translate(scaledDragX, scaledDragY)
        }

        else -> rectDraw
    }
}


fun DrawScope.drawGrid(rect: Rect, color: Color = Color.White) {

    val width = rect.width
    val height = rect.height
    val gridWidth = width / 3
    val gridHeight = height / 3


    drawRect(
        color = color,
        topLeft = rect.topLeft,
        size = rect.size,
        style = Stroke(width = 2.dp.toPx())
    )

    // Horizontal lines
    for (i in 1..2) {
        drawLine(
            color = color,
            start = Offset(rect.left, rect.top + i * gridHeight),
            end = Offset(rect.right, rect.top + i * gridHeight),
            strokeWidth = .7.dp.toPx()
        )
    }

    // Vertical lines
    for (i in 1..2) {
        drawLine(
            color,
            start = Offset(rect.left + i * gridWidth, rect.top),
            end = Offset(rect.left + i * gridWidth, rect.bottom),
            strokeWidth = .7.dp.toPx()
        )
    }
}

