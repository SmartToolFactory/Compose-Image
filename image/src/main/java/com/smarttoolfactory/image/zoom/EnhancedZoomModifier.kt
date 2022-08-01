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
    vararg keys: Any?,
    touchRegionSize: Float,
    minDimension: Float,
    enhancedZoomState: EnhancedZoomState,
    onDown: (EnhancedZoomData) -> Unit,
    onMove: (EnhancedZoomData) -> Unit,
    onUp: (EnhancedZoomData) -> Unit,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        val transformModifier = Modifier.pointerInput(keys) {

            detectTransformGestures(
                consume = true,
                onGestureStart = {
                    onDown(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onUp(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid,
                            pan,
                            zoom,
                            rotate,
                            mainPointer,
                            pointerList
                        )
                    }

                    onMove(enhancedZoomState.enhancedZoomData)
                }
            )
        }

        val tapModifier = Modifier.pointerInput(keys) {
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        enhancedZoomState.onDoubleTap {
                            onUp(enhancedZoomState.enhancedZoomData)
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
        properties["touchRegionRadius"] = touchRegionSize
        properties["minDimension"] = minDimension
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
    }
)
