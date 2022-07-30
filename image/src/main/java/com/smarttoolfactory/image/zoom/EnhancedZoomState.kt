package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


/**
 * * Create and [remember] the [ZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 *  [key1] is used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 *
 * @param initialZoom zoom set initially
 * @param initialRotation rotation set initially
 * @param minZoom minimum zoom value this Composable can possess
 * @param maxZoom maximum zoom value this Composable can possess
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomEnabled when set to true zoom is enabled
 * @param panEnabled when set to true pan is enabled
 * @param rotationEnabled when set to true rotation is enabled
 */
@Composable
fun rememberEnhancedZoomState(
    size: Size,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    limitPan: Boolean = false,
    key1: Any? = Unit
): EnhancedZoomState {
    return remember(key1) {
        EnhancedZoomState(
            size = size,
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomEnabled = zoomEnabled,
            panEnabled = panEnabled,
            rotationEnabled = rotationEnabled,
            limitPan = limitPan
        )
    }
}

/**
 * * Create and [remember] the [ZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 *  [key1] or [key2] are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 *
 * @param initialZoom zoom set initially
 * @param initialRotation rotation set initially
 * @param minZoom minimum zoom value this Composable can possess
 * @param maxZoom maximum zoom value this Composable can possess
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomEnabled when set to true zoom is enabled
 * @param panEnabled when set to true pan is enabled
 * @param rotationEnabled when set to true rotation is enabled
 */
@Composable
fun rememberEnhancedZoomState(
    size: Size,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    limitPan: Boolean = false,
    key1: Any?,
    key2: Any?,
): EnhancedZoomState {
    return remember(key1, key2) {
        EnhancedZoomState(
            size = size,
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomEnabled = zoomEnabled,
            panEnabled = panEnabled,
            rotationEnabled = rotationEnabled,
            limitPan = limitPan
        )
    }
}

/**
 * * Create and [remember] the [ZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 * @param initialZoom zoom set initially
 * @param initialRotation rotation set initially
 * @param minZoom minimum zoom value this Composable can possess
 * @param maxZoom maximum zoom value this Composable can possess
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomEnabled when set to true zoom is enabled
 * @param panEnabled when set to true pan is enabled
 * @param rotationEnabled when set to true rotation is enabled
 * @param keys are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 */
@Composable
fun rememberEnhancedZoomState(
    size: Size,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    limitPan: Boolean = false,
    vararg keys: Any?
): EnhancedZoomState {
    return remember(keys) {
        EnhancedZoomState(
            size = size,
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomEnabled = zoomEnabled,
            panEnabled = panEnabled,
            rotationEnabled = rotationEnabled,
            limitPan = limitPan
        )
    }
}

/**
 *  * State of the zoom. Allows the developer to change zoom, pan,  translate,
 *  or get current state by
 * calling methods on this object. To be hosted and passed to [Modifier.zoom]
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.

 * @param zoomEnabled when set to true zoom is enabled
 * @param panEnabled when set to true pan is enabled
 * @param rotationEnabled when set to true rotation is enabled
 */
open class EnhancedZoomState constructor(
    var size: Size,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    override var zoomEnabled: Boolean = true,
    override var panEnabled: Boolean = true,
    override var rotationEnabled: Boolean = true,
    override var limitPan: Boolean = false
) : ZoomState(
    initialZoom, initialRotation, minZoom, maxZoom, zoomEnabled
) {

    val isZooming = animatableZoom.isRunning
    val isPanning = animatablePan.isRunning
    val isRotating = animatableRotation.isRunning

    var rectDraw = Rect(offset = Offset.Zero, size = size)
    var rectImage = rectDraw.copy()
    var rectCrop = rectDraw

    private val velocityTracker = VelocityTracker()

    val enhancedZoomData: EnhancedZoomData
        get() = EnhancedZoomData(
            zoom = animatableZoom.targetValue,
            pan = animatablePan.targetValue,
            rotation = animatableRotation.targetValue,
            drawRect = rectDraw,
            cropRect = rectCrop
        )


    // Touch gestures
    internal open suspend fun onDown(change: PointerInputChange) = coroutineScope {

    }

    internal open suspend fun onMove(change: PointerInputChange) = coroutineScope {

    }

    internal open suspend fun onUp(change: PointerInputChange) = coroutineScope {

    }

    // Transform Gestures
    internal open suspend fun onGestureStart() = coroutineScope {

    }

    internal open suspend fun onGesture() = coroutineScope {

    }

    internal suspend fun onGestureEnd() {
        val velocity = velocityTracker.calculateVelocity()
        fling(Offset(velocity.x, velocity.y))
    }

    internal fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }


    private suspend fun fling(velocity: Offset) = coroutineScope {
        launch {
            val animationResult = animatablePan.animateDecay(
                velocity,
                exponentialDecay()
            )

//            if (!animationResult.endState.isRunning) {
//                resetTracking()
//            }
        }
    }


    internal fun resetTracking() {
        velocityTracker.resetTracking()
    }

    override suspend fun updateZoomState(
        size: IntSize,
        gesturePan: Offset,
        gestureZoom: Float,
        gestureRotate: Float,
    ) {
        val zoom = (zoom * gestureZoom).coerceIn(zoomMin, zoomMax)
        val rotation = if (rotationEnabled) {
            rotation + gestureRotate
        } else {
            0f
        }


        if (panEnabled) {
            val newOffset = pan + gesturePan.times(zoom)
            snapPanTo(newOffset)
            rectImage.translate(newOffset)
        }

        if (zoomEnabled) {
            snapZoomTo(zoom)
        }

        if (rotationEnabled) {
            snapRotationTo(rotation)
        }
    }
}