package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.smarttoolfactory.gesture.detectTransformGestures
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to.
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * event propagations
 * @param zoomEnabled when set to true zoom is enabled
 * @param panEnabled when set to true pan is enabled
 * @param rotationEnabled when set to true rotation is enabled
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    vararg keys: Any?,
    clip: Boolean = true,
    limitPan: Boolean = true,
    consume: Boolean = true,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    zoomState: ZoomState,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()
        val boundPan = limitPan && !rotationEnabled
        val clipToBounds = (clip || boundPan)

        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val transformModifier = Modifier.pointerInput(keys) {
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd(zoomState.zoomData)
                },
                onGesture = { _, gesturePan, gestureZoom, gestureRotate, _, _ ->

                    var zoom = zoomState.zoom
                    val offset = zoomState.pan

                    val rotation = if (rotationEnabled) {
                        zoomState.rotation + gestureRotate
                    } else {
                        0f
                    }

                    if (zoomEnabled) {
                        zoom = (zoom * gestureZoom).coerceIn(zoomState.zoomMin, zoomState.zoomMax)
                    }

                    val newOffset = offset + gesturePan.times(zoom)

                    val maxX = (size.width * (zoom - 1) / 2f)
                        .coerceAtLeast(0f)
                    val maxY = (size.height * (zoom - 1) / 2f)
                        .coerceAtLeast(0f)

                    if (zoomEnabled) {
                        coroutineScope.launch {
                            zoomState.snapZoomTo(zoom)
                        }
                    }

                    if (panEnabled) {
                        coroutineScope.launch {
                            zoomState.snapPanTo(
                                if (boundPan) {
                                    Offset(
                                        newOffset.x.coerceIn(-maxX, maxX),
                                        newOffset.y.coerceIn(-maxY, maxY)
                                    )
                                } else {
                                    newOffset
                                }
                            )
                        }
                    }

                    if (rotationEnabled) {
                        coroutineScope.launch {
                            zoomState.snapRotationTo(rotation)
                        }
                    }

                    onGesture(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(keys) {
            detectTapGestures(
                onDoubleTap = {

                    val (newZoomLevel, newZoom) = calculateZoom(
                        zoomLevel = zoomLevel,
                        initial = zoomState.zoomInitial,
                        min = zoomState.zoomMin,
                        max = zoomState.zoomMax
                    )

                    zoomLevel = newZoomLevel

                    coroutineScope.launch {
                        zoomState.animatePanTo(Offset.Zero)
                    }

                    coroutineScope.launch {
                        zoomState.animateZoomTo(newZoom)
                    }

                    coroutineScope.launch {
                        zoomState.animateRotationTo(zoomState.rotationInitial)
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(zoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )
    },
    inspectorInfo = {
        name = "zoom"
        properties["keys"] = keys
        properties["clip"] = clip
        properties["limitPan"] = limitPan
        properties["consume"] = consume
        properties["zoomEnabled"] = zoomEnabled
        properties["rotationEnabled"] = rotationEnabled
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to.
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 */
fun Modifier.zoom(
    vararg keys: Any?,
    zoomState: ZoomState,
    rotationEnabled: Boolean = false,
    clip: Boolean = true,
    limitPan: Boolean = true
) = zoom(
    keys = keys,
    clip = clip,
    limitPan = limitPan,
    rotationEnabled = rotationEnabled,
    consume = true,
    zoomState = zoomState,
    onGestureStart = {},
    onGestureEnd = {},
    onGesture = {}
)

private fun GraphicsLayerScope.update(zoomState: ZoomState) {

    // Set zoom
    val zoom = zoomState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    // Set pan
    val pan = zoomState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY

    // Set rotation
    this.rotationZ = zoomState.rotation
}

