package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Wraps demo content with a visual overlay that shows the pinch centroid in real time.
 *
 * The cyan marker stays at the centroid where the current pinch gesture started, while the
 * magenta marker follows the live centroid as the pointers move.
 */
@Composable
fun PinchCentroidDebugOverlay(
    modifier: Modifier = Modifier,
    showLegend: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    var anchorCentroid by remember { mutableStateOf<Offset?>(null) }
    var centroid by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    var pinchStarted = false
                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val currentCentroid = calculateCentroid(event.changes)
                        centroid = currentCentroid

                        if (currentCentroid != null && !pinchStarted) {
                            anchorCentroid = currentCentroid
                            pinchStarted = true
                        }
                    } while (event.changes.any { it.pressed })

                    centroid = null
                }
            }
    ) {
        content()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerRadius = 12.dp.toPx()
            val centerCrosshairRadius = 24.dp.toPx()
            val liveRadius = 14.dp.toPx()
            val liveInnerRadius = 5.dp.toPx()
            val liveCrosshairRadius = 28.dp.toPx()
            val strokeWidth = 2.dp.toPx()

            val pinchAnchor = anchorCentroid

            if (pinchAnchor != null) {
                drawLine(
                    color = Color(0xff00BCD4),
                    start = pinchAnchor.copy(x = pinchAnchor.x - centerCrosshairRadius),
                    end = pinchAnchor.copy(x = pinchAnchor.x + centerCrosshairRadius),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xff00BCD4),
                    start = pinchAnchor.copy(y = pinchAnchor.y - centerCrosshairRadius),
                    end = pinchAnchor.copy(y = pinchAnchor.y + centerCrosshairRadius),
                    strokeWidth = strokeWidth
                )
                drawCircle(
                    color = Color(0xff00BCD4),
                    radius = centerRadius,
                    center = pinchAnchor
                )
                drawCircle(
                    color = Color.White,
                    radius = centerRadius - strokeWidth,
                    center = pinchAnchor
                )
                drawCircle(
                    color = Color(0xff00BCD4),
                    radius = 4.dp.toPx(),
                    center = pinchAnchor
                )
            }

            val currentCentroid = centroid ?: return@Canvas

            if (pinchAnchor != null) {
                drawLine(
                    color = Color(0xff00BCD4),
                    start = pinchAnchor,
                    end = currentCentroid,
                    strokeWidth = strokeWidth
                )
            }
            drawLine(
                color = Color.White,
                start = currentCentroid.copy(x = currentCentroid.x - liveCrosshairRadius),
                end = currentCentroid.copy(x = currentCentroid.x + liveCrosshairRadius),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = Color.White,
                start = currentCentroid.copy(y = currentCentroid.y - liveCrosshairRadius),
                end = currentCentroid.copy(y = currentCentroid.y + liveCrosshairRadius),
                strokeWidth = strokeWidth
            )
            drawCircle(
                color = Color.White,
                radius = liveRadius,
                center = currentCentroid
            )
            drawCircle(
                color = Color(0xffD81B60),
                radius = liveRadius - strokeWidth,
                center = currentCentroid
            )
            drawCircle(
                color = Color.White,
                radius = liveInnerRadius,
                center = currentCentroid
            )
        }

        if (showLegend) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                text = "CYAN = pinch start\nMAGENTA = current pinch centroid",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Returns the centroid of the currently pressed pointers when at least two fingers are down.
 * Single-pointer gestures return `null` so the overlay only reflects pinch interactions.
 */
private fun calculateCentroid(changes: List<PointerInputChange>): Offset? {
    val activeChanges = changes.filter { it.pressed }

    if (activeChanges.size < 2) {
        return null
    }

    var totalX = 0f
    var totalY = 0f

    activeChanges.forEach { change ->
        totalX += change.position.x
        totalY += change.position.y
    }

    return Offset(
        x = totalX / activeChanges.size,
        y = totalY / activeChanges.size
    )
}
