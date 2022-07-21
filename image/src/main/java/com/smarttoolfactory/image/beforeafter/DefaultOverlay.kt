package com.smarttoolfactory.image.beforeafter

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.R

/**
 * Default overlay for [BeforeAfterImage] and [BeforeAfterLayout] that draws line and
 * thumb with properties provided.
 *
 * @param width of the [BeforeAfterImage] or [BeforeAfterLayout]. You should get width from
 * scope of these Composables and pass to calculate bounds correctly
 * @param height of the [BeforeAfterImage] or [BeforeAfterLayout]. You should get height from
 * scope of these Composables and pass to calculate bounds correctly
 * @param position current position or progress of before/after
 * @param verticalThumbMove when true thumb can move vertically based on user touch
 * @param lineColor color if divider line
 * @param thumbBackgroundColor background color of thumb [Icon]
 * @param thumbTintColor tint color of thumb [Icon]
 * @param thumbShape shape of thumb [Icon]
 * @param thumbElevation elevation of thumb [Icon]
 * @param thumbResource drawable resource that should be used with thumb
 * @param thumbSize size of the thumb in dp
 * @param thumbPositionPercent vertical position of thumb if [verticalThumbMove] is false
 * It's between [0f-100f] to set thumb's vertical position in layout
 */
@Composable
internal fun DefaultOverlay(
    width: Dp,
    height: Dp,
    position: Offset,
    verticalThumbMove: Boolean = false,
    lineColor: Color = Color.White,
    thumbBackgroundColor: Color = Color.White,
    thumbTintColor: Color = Color.Gray,
    thumbShape: Shape = CircleShape,
    thumbElevation: Dp = 2.dp,
    @DrawableRes thumbResource: Int = R.drawable.baseline_swap_horiz_24,
    thumbSize: Dp = 36.dp,
    @FloatRange(from = 0.0, to = 100.0) thumbPositionPercent: Float = 85f,
) {

    var thumbPosX = position.x
    var thumbPosY = position.y

    val linePosition: Float

    val density = LocalDensity.current

    with(density) {
        val thumbRadius = (thumbSize / 2).toPx()
        val imageWidthInPx = width.toPx()
        val imageHeightInPx = height.toPx()

        val horizontalOffset = imageWidthInPx / 2
        val verticalOffset = imageHeightInPx / 2

        linePosition = thumbPosX.coerceIn(0f, imageWidthInPx)
        thumbPosX -= horizontalOffset

        thumbPosY = if (verticalThumbMove) {
            (thumbPosY - verticalOffset)
                .coerceIn(
                    -verticalOffset + thumbRadius,
                    verticalOffset - thumbRadius
                )
        } else {
            ((imageHeightInPx * thumbPositionPercent / 100f - thumbRadius) - verticalOffset)
        }
    }

    Box(
        modifier = Modifier.size(width, height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            drawLine(
                lineColor,
                strokeWidth = 1.5.dp.toPx(),
                start = Offset(linePosition, 0f),
                end = Offset(linePosition, size.height)
            )
        }

        Icon(
            painter = painterResource(id = thumbResource),
            contentDescription = null,
            tint = thumbTintColor,
            modifier = Modifier
                .offset {
                    IntOffset(thumbPosX.toInt(), thumbPosY.toInt())
                }
                .shadow(thumbElevation, thumbShape)
                .background(thumbBackgroundColor)
                .size(thumbSize)
                .padding(4.dp)
        )
    }
}
