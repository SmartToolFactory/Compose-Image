package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.util.update
import kotlinx.coroutines.launch

fun Modifier.enhancedZoom(
    enhancedZoomState: EnhancedZoomState,
    key: Any? = Unit,
    onDown: ((EnhancedZoomData) -> Unit)? = null,
    onMove: ((EnhancedZoomData) -> Unit)? = null,
    onUp: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val transformModifier = Modifier.pointerInput(key) {

            detectTransformGestures(
                consume = true,
                onGestureStart = {
                    onDown?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onUp?.invoke(enhancedZoomState.enhancedZoomData)
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

                    onMove?.invoke(enhancedZoomState.enhancedZoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        enhancedZoomState.onDoubleTap {
                            onUp?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            Modifier
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
    }
)
