package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope


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
    size: IntSize,
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
    size: IntSize,
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
    size: IntSize,
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
    var size: IntSize,
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

    val isAnimationRunning = isZooming || isPanning || isRotating

    var rectDraw =
        Rect(offset = Offset.Zero, size = Size(size.width.toFloat(), size.height.toFloat()))
    var rectCrop by mutableStateOf(rectDraw.copy())

    private val velocityTracker = VelocityTracker()

    val enhancedZoomData: EnhancedZoomData
        get() = EnhancedZoomData(
            zoom = animatableZoom.targetValue,
            pan = animatablePan.targetValue,
            rotation = animatableRotation.targetValue,
            drawRect = rectDraw,
            cropRect = rectCrop
        )

    private fun getBounds(): Offset {
        return getBounds(size)
    }

    // Touch gestures
    open suspend fun onDown(change: PointerInputChange) {}

    open suspend fun onMove(change: PointerInputChange) {}

    open suspend fun onUp(change: PointerInputChange) {}

    // Transform Gestures
    internal open suspend fun onGestureStart(change: PointerInputChange) {}

    internal open suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) = coroutineScope {

        updateZoomState(
            size = size,
            gestureZoom = zoom,
            gesturePan = pan,
            gestureRotate = rotation
        )

        // Fling Gesture
        if (changes.size == 1) {
            addPosition(
                mainPointer.uptimeMillis,
                mainPointer.position
            )
        }
    }

    internal suspend fun onGestureEnd(onAnimationEnd: () -> Unit) {
        if (zoom > 1) {
            fling()
        }
        resetToValidBounds()
        onAnimationEnd()
    }

    // Double Tap
    internal suspend fun onDoubleTap(onAnimationEnd: () -> Unit) {
        resetTracking()
        resetWithAnimation()
        onAnimationEnd()
    }

    /**
     * Resets to bounds with animation and resets tracking for fling animation
     */
    private suspend fun resetToValidBounds() {
        val zoom = zoom.coerceAtLeast(1f)
        val bounds = getBounds()

        val pan = Offset(
            pan.x.coerceIn(-bounds.x, bounds.x),
            pan.y.coerceIn(-bounds.y, bounds.y)
        )

        resetWithAnimation(pan = pan, zoom = zoom)
        resetTracking()
    }


    // Fling gesture
    private fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }

    private suspend fun fling() {
        val velocityTracker = velocityTracker.calculateVelocity()
        val velocity = Offset(velocityTracker.x, velocityTracker.y)

        animatablePan.animateDecay(
            velocity,
            exponentialDecay(
                absVelocityThreshold = 20f
            )
        )
    }

    private fun resetTracking() {
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
        }

        if (zoomEnabled) {
            snapZoomTo(zoom)
        }

        if (rotationEnabled) {
            snapRotationTo(rotation)
        }

        val width = size.width
        val height = size.height
        val offsetX = (width * (zoom - 1) / 2f).coerceAtLeast(0f) - pan.x
        val offsetY = (height * (zoom - 1) / 2f).coerceAtLeast(0f) - pan.y

        rectCrop = getCropRect(
            bitmapWidth = 1024,
            bitmapHeight = 768,
            imageWidth = width.toFloat(),
            imageHeight = height.toFloat(),
            pan = Offset(
                x = offsetX / zoom,
                y = offsetY / zoom,
            ),
            zoom = zoom,
            rectBounds = rectDraw
        )
        println(
            "🔥 EnhancedZoomState updateZoomState()\n" +
                    "offsetX: $offsetX, offsetY: $offsetY" +
                    "size: $size, pan: $pan, zoom: $zoom\n" +
                    "rectCrop: $rectCrop"
        )
    }
}

/**
 * Get rectangle of current transformation of [pan], [zoom] and current bounds of the Composable's
 * selected area as [rectBounds]
 */
fun getCropRect(
    bitmapWidth: Int,
    bitmapHeight: Int,
    imageWidth: Float,
    imageHeight: Float,
    pan: Offset,
    zoom: Float,
    rectBounds: Rect
): Rect {
    val widthRatio = bitmapWidth / imageWidth
    val heightRatio = bitmapHeight / imageHeight

    val width = (widthRatio * rectBounds.width / zoom).coerceIn(0f, bitmapWidth.toFloat())
    val height = (heightRatio * rectBounds.height / zoom).coerceIn(0f, bitmapHeight.toFloat())

    val offsetXInBitmap = (widthRatio * (pan.x + rectBounds.left / zoom))
        .coerceIn(0f, bitmapWidth - width)
    val offsetYInBitmap = heightRatio * (pan.y + rectBounds.top / zoom)
        .coerceIn(0f, bitmapHeight - height)

    return Rect(
        offset = Offset(offsetXInBitmap, offsetYInBitmap),
        size = Size(width, height)
    )
}