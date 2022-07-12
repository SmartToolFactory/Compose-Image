package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
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
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param initialZoom initial value of zoom
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
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
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    clip: Boolean = true,
    limitPan: Boolean = true,
    consume: Boolean = true,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
) = composed(
    factory = {

        val coroutineScope = rememberCoroutineScope()
        val zoomMin = minZoom.coerceAtLeast(.5f)
        val zoomMax = maxZoom.coerceAtLeast(1f)
        val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)

        require(zoomMax >= zoomMin)

        val animatablePan = remember {
            Animatable(Offset.Zero, Offset.VectorConverter)
        }
        val animatableZoom = remember { Animatable(zoomInitial) }
        val animatableRotation = remember { Animatable(0f) }

        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val boundPan = limitPan && !rotationEnabled
        val clipToBounds = (clip || boundPan)


        val transformModifier = Modifier.pointerInput(keys) {
            detectTransformGestures(
                consume = consume,
                onGestureStart = {
                    onGestureStart(
                        ZoomData(
                            zoom = animatableZoom.value,
                            pan = animatablePan.value,
                            rotation = animatableRotation.value
                        )
                    )
                },
                onGestureEnd = {
                    onGestureEnd(
                        ZoomData(
                            zoom = animatableZoom.value,
                            pan = animatablePan.value,
                            rotation = animatableRotation.value
                        )
                    )
                },
                onGesture = { centroid, gesturePan, gestureZoom, gestureRotate,
                              _,
                              _ ->

                    var zoom = animatableZoom.value
                    val offset = animatablePan.value
                    val rotation = if (rotationEnabled) {
                        animatableRotation.value + gestureRotate
                    } else {
                        0f
                    }

                    if (zoomEnabled) {
                        zoom = (zoom * gestureZoom).coerceIn(zoomMin, zoomMax)
                    }

                    val newOffset = offset + gesturePan.times(zoom)

                    val maxX = (size.width * (zoom - 1) / 2f)
                        .coerceAtLeast(0f)
                    val maxY = (size.height * (zoom - 1) / 2f)
                        .coerceAtLeast(0f)


                    if (zoomEnabled) {
                        coroutineScope.launch {
                            animatableZoom.snapTo(zoom)
                        }
                    }

                    if (panEnabled) {
                        coroutineScope.launch {
                            animatablePan.snapTo(
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
                            animatableRotation.snapTo(rotation)
                        }
                    }

                    onGesture(
                        ZoomData(
                            zoom = animatableZoom.value,
                            pan = animatablePan.value,
                            rotation = animatableRotation.value
                        )
                    )
                }
            )
        }

        val tapModifier = Modifier.pointerInput(keys) {
            detectTapGestures(
                onDoubleTap = {

                    val (newZoomLevel, newZoom) = calculateZoom(
                        zoomLevel = zoomLevel,
                        initial = zoomInitial,
                        min = minZoom,
                        max = maxZoom
                    )

                    zoomLevel = newZoomLevel

                    coroutineScope.launch {
                        animatablePan.animateTo(Offset.Zero, spring())
                    }
                    coroutineScope.launch {
                        animatableZoom.animateTo(newZoom, spring())
                    }
                    coroutineScope.launch {
                        animatableRotation.animateTo(0f, spring())
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            val zoom = animatableZoom.value

            // Set zoom
            scaleX = zoom
            scaleY = zoom

            // Set pan
            val translationX = animatablePan.value.x
            val translationY = animatablePan.value.y
            this.translationX = translationX
            this.translationY = translationY

            // Set rotation
            rotationZ = animatableRotation.value
//                    TransformOrigin(0f, 0f).also { transformOrigin = it }
            onGesture(
                ZoomData(
                    zoom = animatableZoom.value,
                    pan = animatablePan.value,
                )
            )
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(transformModifier)
                .then(tapModifier)
                .then(graphicsModifier)

        )

    },
    inspectorInfo = {

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
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 */
fun Modifier.zoom(
    vararg keys: Any?,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    rotate: Boolean = false,
    clip: Boolean = true,
    limitPan: Boolean = true,

    ) = zoom(
    keys = keys,
    initialZoom = initialZoom,
    minZoom = minZoom,
    maxZoom = maxZoom,
    clip = clip,
    limitPan = limitPan,
    rotationEnabled = rotate,
    consume = true,
    onGestureStart = {},
    onGestureEnd = {},
    onGesture = {}
)