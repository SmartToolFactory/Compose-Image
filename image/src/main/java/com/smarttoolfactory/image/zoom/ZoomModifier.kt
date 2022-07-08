package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    clip: Boolean = true,
    limitPan: Boolean = true,
    consume: Boolean = true,
    onGestureStart: () -> Unit = {},
    onChange: (Zoom) -> Unit = {},
    onGestureEnd: () -> Unit = {},
) = composed(
    factory = {

        val coroutineScope = rememberCoroutineScope()
        val zoomMin = minZoom.coerceAtLeast(.5f)
        val zoomMax = maxZoom.coerceAtLeast(1f)
        val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)


        require(zoomMax >= zoomMin)

        val animatableOffset = remember {
            Animatable(Offset.Zero, Offset.VectorConverter)
        }
        val animatableZoom = remember { Animatable(zoomInitial) }

        this.then(
            (if (clip) Modifier.clipToBounds() else Modifier)
                .graphicsLayer {
                    val zoom = animatableZoom.value
                    val translationX = animatableOffset.value.x
                    val translationY = animatableOffset.value.y
                    this.translationX = translationX
                    this.translationY = translationY
                    scaleX = zoom
                    scaleY = zoom

                    onChange(
                        Zoom(
                            zoom = zoom,
                            translationX = translationX,
                            translationY
                        )
                    )
                }
                .pointerInput(keys) {
                    detectTransformGestures(
                        consume = consume,
                        onGestureStart = {
                            onGestureStart()
                        },
                        onGestureEnd = {
                            onGestureEnd()
                        },
                        onGesture = { _,
                                      gesturePan: Offset,
                                      gestureZoom: Float,
                                      _,
                                      _,
                                      _ ->

                            println("🔥 PointerInput size: $size")

                            var zoom = animatableZoom.value
                            val offset = animatableOffset.value

                            zoom = (zoom * gestureZoom).coerceIn(zoomMin, zoomMax)
                            val newOffset = offset + gesturePan.times(zoom)

                            val maxX = (size.width * (zoom - 1) / 2f).coerceAtLeast(0f)
                            val maxY = (size.height * (zoom - 1) / 2f).coerceAtLeast(0f)

                            coroutineScope.launch {
                                animatableZoom.snapTo(zoom)
                            }
                            coroutineScope.launch {
                                animatableOffset.snapTo(
                                    if (limitPan) {
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
                    )
                }
                .pointerInput(keys) {
                    detectTapGestures(
                        onDoubleTap = {
                            coroutineScope.launch {
                                animatableOffset.animateTo(Offset.Zero, spring())
                            }
                            coroutineScope.launch {
                                animatableZoom.animateTo(zoomInitial, spring())
                            }
                        }
                    )
                }
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
    clip: Boolean = true,
    limitPan: Boolean = true,

    ) = zoom(
    keys = keys,
    initialZoom = initialZoom,
    minZoom = minZoom,
    maxZoom = maxZoom,
    clip = clip,
    limitPan = limitPan,
    consume = true,
    onGestureStart = {},
    onGestureEnd = {},
    onChange = {}
)