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
* [EnhancedZoomData]  of this modifier
*/
fun Modifier.animatedZoom(
    key: Any? = Unit,
    consume: Boolean = true,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = consume,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = pan,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        animatedZoomState.onDoubleTap {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "animatedZoomState"

    }
)
