package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.transform.Transform
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to.
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 */
fun Modifier.zoom(
    vararg keys: Any?,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    clip: Boolean = true,
    onChange: (Transform) -> Unit = {}
) = composed(
    factory = {

        val coroutineScope = rememberCoroutineScope()
        val zoomMin = minZoom.coerceAtLeast(.5f)
        val zoomMax = maxZoom.coerceAtLeast(1f)
        val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)

        require(zoomMax >= zoomMin)

        var size by remember { mutableStateOf(Size.Zero) }


        val animatableOffset = remember {
            Animatable(Offset.Zero, Offset.VectorConverter)
        }
        val animatableZoom = remember { Animatable(zoomInitial) }

        Modifier
//            .then(if (clip) Modifier.clipToBounds() else Modifier)
            .graphicsLayer {
                val zoom = animatableZoom.value
                translationX = animatableOffset.value.x
                translationY = animatableOffset.value.y
                scaleX = zoom
                scaleY = zoom
                this.clip = clip

                onChange(Transform(translationX, translationY, scaleX, scaleY))
            }
            .pointerInput(keys) {

                detectTransformGestures(
                    onGesture = { _,
                                  gesturePan: Offset,
                                  gestureZoom: Float,
                                  _,
                                  _,
                                  _ ->

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
                                Offset(
                                    newOffset.x.coerceIn(-maxX, maxX),
                                    newOffset.y.coerceIn(-maxY, maxY)
                                )
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
            .onSizeChanged {
                size = it.toSize()
            }
    },
    inspectorInfo = {

    }
)