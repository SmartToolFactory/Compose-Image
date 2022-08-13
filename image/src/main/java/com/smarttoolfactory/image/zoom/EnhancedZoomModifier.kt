package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.util.update
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param enhancedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [EnhancedZoomData]  of this modifier
 */
fun Modifier.enhancedZoom(
    key: Any? = Unit,
    consume: Boolean = true,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = pan,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        enhancedZoomState.onDoubleTap {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["key"] = key
        properties["consume"] = consume
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)


/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * [key1], [key2] are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param enhancedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [EnhancedZoomData]  of this modifier
 */
fun Modifier.enhancedZoom(
    key1: Any?,
    key2: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1,key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = pan,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1,key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        enhancedZoomState.onDoubleTap {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["key1"] = key1
        properties["key2"] = key2
        properties["consume"] = consume
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)



/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param enhancedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [ZoomData]
 * event propagations
 * @param onGestureStart callback to to notify gesture has started and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [EnhancedZoomData]  of this modifier
 */
fun Modifier.enhancedZoom(
    vararg keys: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = pan,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        enhancedZoomState.onDoubleTap {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["keys"] = keys
        properties["consume"] = consume
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)
