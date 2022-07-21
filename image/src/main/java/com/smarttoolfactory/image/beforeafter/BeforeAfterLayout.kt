package com.smarttoolfactory.image.beforeafter

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.detectMotionEvents
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.DimensionSubcomposeLayout
import com.smarttoolfactory.image.R
import com.smarttoolfactory.image.util.scale
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.rememberZoomState
import kotlinx.coroutines.launch

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder]. This overload uses [DefaultOverlay] to draw vertical slider and thumb.
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn
 * @param verticalThumbMove when true thumb can move vertically based on user touch
 * @param lineColor color if divider line
 * @param thumbBackgroundColor background color of thumb [Icon]
 * @param thumbTintColor tint color of thumb [Icon]
 * @param thumbShape shape of thumb [Icon]
 * @param thumbElevation elevation of thumb [Icon]
 * @param thumbResource drawable resource that should be used with thumb
 * @param thumbSize size of the thumb in dp
 * @param thumbPositionPercent vertical position of thumb if [verticalThumbMove] is false.
 * It's between [0f-100f] to set thumb's vertical position in layout
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 *
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    lineColor: Color = Color.White,
    verticalThumbMove: Boolean = false,
    thumbBackgroundColor: Color = Color.White,
    thumbTintColor: Color = Color.Gray,
    thumbShape: Shape = CircleShape,
    thumbElevation: Dp = 2.dp,
    @DrawableRes thumbResource: Int = R.drawable.baseline_swap_horiz_24,
    thumbSize: Dp = 36.dp,
    @FloatRange(from = 0.0, to = 100.0) thumbPositionPercent: Float = 85f,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {
    var progress by remember { mutableStateOf(50f) }

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = {
            progress = it
        },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                lineColor = lineColor,
                verticalThumbMove = verticalThumbMove,
                thumbBackgroundColor = thumbBackgroundColor,
                thumbTintColor = thumbTintColor,
                thumbShape = thumbShape,
                thumbElevation = thumbElevation,
                thumbResource = thumbResource,
                thumbSize = thumbSize,
                thumbPositionPercent = thumbPositionPercent
            )
        }
    )
}

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder]. This overload uses [DefaultOverlay] to draw vertical slider and thumb
 * and has [progress] and [onProgressChange] which makes it eligible to animate by changing
 * [progress] value.
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn
 * @param progress current position or progress of before/after
 * @param onProgressChange current position or progress of before/after
 * @param verticalThumbMove when true thumb can move vertically based on user touch
 * @param lineColor color if divider line
 * @param thumbBackgroundColor background color of thumb [Icon]
 * @param thumbTintColor tint color of thumb [Icon]
 * @param thumbShape shape of thumb [Icon]
 * @param thumbElevation elevation of thumb [Icon]
 * @param thumbResource drawable resource that should be used with thumb
 * @param thumbSize size of the thumb in dp
 * @param thumbPositionPercent vertical position of thumb if [verticalThumbMove] is false.
 * It's between [0f-100f] to set thumb's vertical position in layout
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    lineColor: Color = Color.White,
    verticalThumbMove: Boolean = false,
    thumbBackgroundColor: Color = Color.White,
    thumbTintColor: Color = Color.Gray,
    thumbShape: Shape = CircleShape,
    thumbElevation: Dp = 2.dp,
    @DrawableRes thumbResource: Int = R.drawable.baseline_swap_horiz_24,
    thumbSize: Dp = 36.dp,
    @FloatRange(from = 0.0, to = 100.0) thumbPositionPercent: Float = 85f,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
) {

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = { dpSize: DpSize, offset: Offset ->
            DefaultOverlay(
                width = dpSize.width,
                height = dpSize.height,
                position = offset,
                lineColor = lineColor,
                verticalThumbMove = verticalThumbMove,
                thumbBackgroundColor = thumbBackgroundColor,
                thumbTintColor = thumbTintColor,
                thumbShape = thumbShape,
                thumbElevation = thumbElevation,
                thumbResource = thumbResource,
                thumbSize = thumbSize,
                thumbPositionPercent = thumbPositionPercent
            )
        }
    )
}

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder].
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn

 * It's between [0f-100f] to set thumb's vertical position in layout
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 * @param overlay Composable for drawing overlay over this Composable. It returns dimensions
 * of ancestor and touch position
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable BoxScope.() -> Unit = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable BoxScope.() -> Unit = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {
    var progress by remember { mutableStateOf(50f) }

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = {
            progress = it
        },
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = overlay
    )
}

/**
 * A composable that lays out and draws a given [beforeContent] and [afterContent]
 * based on [contentOrder].
 *
 * @param enableProgressWithTouch flag to enable drag and change progress with touch
 * @param enableZoom when enabled images are zoomable and pannable
 * @param contentOrder order of composables to be drawn
 * @param progress current position or progress of before/after
 * @param onProgressChange current position or progress of before/after
 * It's between [0f-100f] to set thumb's vertical position in layout
 * @param beforeContent content to be drawn as before Composable
 * @param afterContent content to be drawn as after Composable
 * @param beforeLabel label for [beforeContent]. It's [BeforeLabel] by default
 * @param afterLabel label for [afterContent]. It's [AfterLabel] by default
 * @param overlay Composable for drawing overlay over this Composable. It returns dimensions
 * of ancestor and touch position
 */
@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)? = { BeforeLabel(contentOrder = contentOrder) },
    afterLabel: @Composable (BoxScope.() -> Unit)? = { AfterLabel(contentOrder = contentOrder) },
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {

    Layout(
        modifier = modifier,
        beforeContent = beforeContent,
        afterContent = afterContent,
        beforeLabel = beforeLabel,
        afterLabel = afterLabel,
        progress = progress,
        onProgressChange = onProgressChange,
        contentOrder = contentOrder,
        enableProgressWithTouch = enableProgressWithTouch,
        enableZoom = enableZoom,
        overlay = overlay
    )
}

@Composable
private fun Layout(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 100.0) progress: Float = 50f,
    onProgressChange: ((Float) -> Unit)? = null,
    enableProgressWithTouch: Boolean = true,
    enableZoom: Boolean = true,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)?,
    afterLabel: @Composable (BoxScope.() -> Unit)?,
    overlay: @Composable ((DpSize, Offset) -> Unit)?
) {
    DimensionSubcomposeLayout(
        modifier = modifier,
        placeMainContent = false,
        mainContent = {
            beforeContent()
        },
        dependentContent = { contentSize: Size ->

            val boxWidth = contentSize.width
            val boxHeight = contentSize.height

            val boxWidthInDp: Dp
            val boxHeightInDp: Dp

            with(LocalDensity.current) {
                boxWidthInDp = boxWidth.toDp()
                boxHeightInDp = boxHeight.toDp()
            }

            // Sales and interpolates from offset from dragging to user value in valueRange
            fun scaleToUserValue(offset: Float) =
                scale(0f, boxWidth, offset, 0f, 100f)

            // Scales user value using valueRange to position on x axis on screen
            fun scaleToOffset(userValue: Float) =
                scale(0f, 100f, userValue, 0f, boxWidth)

            var rawOffset by remember {
                mutableStateOf(
                    Offset(
                        x = scaleToOffset(progress),
                        y = boxHeight / 2f,
                    )
                )
            }

            rawOffset = rawOffset.copy(x = scaleToOffset(progress))

            var isHandleTouched by remember { mutableStateOf(false) }

            val zoomState = rememberZoomState(limitPan = true)
            val coroutineScope = rememberCoroutineScope()

            val transformModifier = Modifier.pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { _: Offset, panChange: Offset, zoomChange: Float, _, _, _ ->

                        coroutineScope.launch {
                            zoomState.updateZoomState(
                                size,
                                gesturePan = panChange,
                                gestureZoom = zoomChange
                            )
                        }
                    }
                )
            }

            val touchModifier = Modifier.pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        val xPos = position.x

                        isHandleTouched =
                            ((rawOffset.x - xPos) * (rawOffset.x - xPos) < 5000)
                    },
                    onMove = {
                        if (isHandleTouched) {
                            rawOffset = it.position
                            onProgressChange?.invoke(
                                scaleToUserValue(rawOffset.x)
                            )
                            it.consume()
                        }
                    },
                    onUp = {
                        isHandleTouched = false
                    }
                )
            }

            val tapModifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        coroutineScope.launch {
                            zoomState.animatePanTo(Offset.Zero)
                        }

                        coroutineScope.launch {
                            zoomState.animateZoomTo(1f)
                        }
                    }
                )
            }

            val graphicsModifier = Modifier.graphicsLayer {
                this.update(zoomState)
            }

            val zoom = zoomState.zoom
            val pan = zoomState.pan
            val handlePosition = rawOffset.x

            val shapeBefore by remember(handlePosition, zoom, pan) {
                mutableStateOf(
                    GenericShape { size: Size, layoutDirection: LayoutDirection ->
                        moveTo(0f, 0f)
                        lineTo(handlePosition, 0f)
                        lineTo(handlePosition, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                )
            }

            val shapeAfter by remember(handlePosition, zoom, pan) {
                mutableStateOf(
                    GenericShape { size: Size, layoutDirection: LayoutDirection ->
                        moveTo(handlePosition, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(handlePosition, size.height)
                        close()
                    }
                )
            }

            val parentModifier = Modifier
                .size(boxWidthInDp, boxHeightInDp)
                .clipToBounds()
                .then(if (enableZoom) transformModifier.then(tapModifier) else Modifier)
                .then(if (enableProgressWithTouch) touchModifier else Modifier)


            val beforeModifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.clip = true
                    this.shape = shapeBefore
                }


            val afterModifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.clip = true
                    this.shape = shapeAfter
                }

            LayoutImpl(
                modifier = parentModifier,
                beforeModifier = beforeModifier,
                afterModifier = afterModifier,
                graphicsModifier = graphicsModifier,
                beforeContent = beforeContent,
                afterContent = afterContent,
                beforeLabel = beforeLabel,
                afterLabel = afterLabel,
                overlay = overlay,
                contentOrder = contentOrder,
                boxWidthInDp = boxWidthInDp,
                boxHeightInDp = boxHeightInDp,
                rawOffset = rawOffset
            )
        }
    )
}

@Composable
private fun LayoutImpl(
    modifier: Modifier,
    beforeModifier: Modifier,
    afterModifier: Modifier,
    graphicsModifier: Modifier,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable (BoxScope.() -> Unit)? = null,
    afterLabel: @Composable (BoxScope.() -> Unit)? = null,
    overlay: @Composable ((DpSize, Offset) -> Unit)? = null,
    contentOrder: ContentOrder,
    boxWidthInDp: Dp,
    boxHeightInDp: Dp,
    rawOffset: Offset,
) {
    Box(modifier = modifier) {

        // BEFORE
        val before = @Composable {
            Box(if (contentOrder == ContentOrder.BeforeAfter) beforeModifier else afterModifier) {
                Box(
                    modifier = Modifier.then(graphicsModifier)
                ) {
                    beforeContent()
                }
                beforeLabel?.invoke(this)
            }
        }

        // AFTER
        val after = @Composable {
            Box(if (contentOrder == ContentOrder.BeforeAfter) afterModifier else beforeModifier) {
                Box(
                    modifier = Modifier.then(graphicsModifier)
                ) {
                    afterContent()
                }
                afterLabel?.invoke(this)
            }
        }

        if (contentOrder == ContentOrder.BeforeAfter) {
            before()
            after()
        } else {
            after()
            before()
        }
    }

    overlay?.invoke(
        DpSize(boxWidthInDp, boxHeightInDp), rawOffset
    )
}
