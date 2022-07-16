package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.smarttoolfactory.gesture.detectTransformGestures
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to.
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    key: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()

        val boundPan = zoomState.limitPan && !zoomState.rotationEnabled
        val clipToBounds = (clip || boundPan)

        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val transformModifier = Modifier.pointerInput(key) {
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd(zoomState.zoomData)
                },
                onGesture = { _, gesturePan, gestureZoom, gestureRotate, _, _ ->

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            size = size,
                            gestureZoom = gestureZoom,
                            gesturePan = gesturePan,
                            gestureRotate = gestureRotate
                        )
                    }

                    onGesture(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
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
        properties["key"] = key
        properties["clip"] = clip
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to.
 * [key1], [key2] are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    key1: Any?,
    key2: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()

        val boundPan = zoomState.limitPan && !zoomState.rotationEnabled
        val clipToBounds = (clip || boundPan)

        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val transformModifier = Modifier.pointerInput(key1, key2) {
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart(zoomState.zoomData)
                },
                onGestureEnd = {
                    onGestureEnd(zoomState.zoomData)
                },
                onGesture = { _, gesturePan, gestureZoom, gestureRotate, _, _ ->

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            size = size,
                            gestureZoom = gestureZoom,
                            gesturePan = gesturePan,
                            gestureRotate = gestureRotate
                        )
                    }

                    onGesture(zoomState.zoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
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
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["consume"] = consume
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
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
fun Modifier.zoom(
    vararg keys: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
) = composed(
    factory = {
        val coroutineScope = rememberCoroutineScope()

        val boundPan = zoomState.limitPan && !zoomState.rotationEnabled
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

                    coroutineScope.launch {
                        zoomState.updateZoomState(
                            size = size,
                            gestureZoom = gestureZoom,
                            gesturePan = gesturePan,
                            gestureRotate = gestureRotate
                        )
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
        properties["consume"] = consume
        properties["zoomState"] = zoomState
        properties["onGestureStart"] = onGestureStart
        properties["onGesture"] = onGesture
        properties["onGestureEnd"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to.
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 */
fun Modifier.zoom(
    key: Any? = Unit,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    key = key,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = {},
    onGestureEnd = {},
    onGesture = {}
)

/**
 * Modifier that zooms in or out of Composable set to.
 * [key1] and [key2] are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 */
fun Modifier.zoom(
    key1: Any? ,
    key2: Any? ,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    key1 = key1,
    key2 = key2,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = {},
    onGestureEnd = {},
    onGesture = {}
)

/**
 * Modifier that zooms in or out of Composable set to.
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param zoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 */
fun Modifier.zoom(
    vararg keys: Any?,
    zoomState: ZoomState,
    clip: Boolean = true,
) = zoom(
    keys = keys,
    clip = clip,
    consume = true,
    zoomState = zoomState,
    onGestureStart = {},
    onGestureEnd = {},
    onGesture = {}
)
