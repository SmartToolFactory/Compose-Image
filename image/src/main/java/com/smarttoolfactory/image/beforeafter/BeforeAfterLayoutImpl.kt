package com.smarttoolfactory.image.beforeafter

import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import com.smarttoolfactory.gesture.detectMotionEvents
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.DimensionSubcomposeLayout
import com.smarttoolfactory.image.util.scale
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.rememberZoomState
import kotlinx.coroutines.launch


@Composable
internal fun Layout(
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
                // Pass size of this Composable this Modifier is attached for constraining operations
                // inside this bounds
                zoomState.size = this.size
                detectTransformGestures(
                    onGesture = { centroid: Offset, panChange: Offset, zoomChange: Float, _, _, _ ->

                        coroutineScope.launch {
                            zoomState.updateZoomState(
                                centroid = centroid,
                                panChange = panChange,
                                zoomChange = zoomChange
                            )
                        }
                    }
                )
            }

            val touchModifier = Modifier.pointerInput(Unit) {
                // Pass size of this Composable this Modifier is attached for constraining operations
                // inside this bounds
                zoomState.size = this.size
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
                // Pass size of this Composable this Modifier is attached for constraining operations
                // inside this bounds
                zoomState.size = this.size
                detectTapGestures(
                    onDoubleTap = {
                        coroutineScope.launch {
                            zoomState.resetWithAnimation()
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
