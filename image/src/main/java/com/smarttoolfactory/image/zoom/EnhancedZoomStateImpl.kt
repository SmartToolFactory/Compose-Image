package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.image.util.getCropRect
import kotlinx.coroutines.coroutineScope


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
    val imageSize: IntSize,
    val containerSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    val flingGestureEnabled: Boolean = true,
    val moveToBoundsEnabled: Boolean = true,
    override var zoomEnabled: Boolean = true,
    override var panEnabled: Boolean = true,
    override var rotationEnabled: Boolean = true,
    override var limitPan: Boolean = false
) : ZoomState(
    1f, 1f, minZoom, maxZoom, zoomEnabled
) {

    var rectDraw = Rect(
        offset = Offset.Zero,
        size = Size(containerSize.width.toFloat(), containerSize.height.toFloat())
    )

    private val velocityTracker = VelocityTracker()

    val enhancedZoomData: EnhancedZoomData
        get() = EnhancedZoomData(
            zoom = animatableZoom.targetValue,
            pan = animatablePan.targetValue,
            rotation = animatableRotation.targetValue,
            drawRect = rectDraw,
            cropRect = calculateRectBounds()
        )

    private fun getBounds(): Offset {
        return getBounds(containerSize)
    }

    // Touch gestures
    open suspend fun onDown(change: PointerInputChange) {}

    open suspend fun onMove(change: PointerInputChange) {}

    open suspend fun onUp(change: PointerInputChange) {}

    // Transform Gestures
    internal open suspend fun onGestureStart(change: PointerInputChange) {}

    open suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) = coroutineScope {

        updateZoomState(
            size = containerSize,
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

    suspend fun onGestureEnd(onAnimationEnd: () -> Unit) {
        if (zoom > 1) {
            fling()
        }
        resetToValidBounds()
        onAnimationEnd()
    }

    // Double Tap
    suspend fun onDoubleTap(onAnimationEnd: () -> Unit) {
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
    }

    private fun calculateRectBounds(): Rect {

        val width = containerSize.width
        val height = containerSize.height
        val zoom = animatableZoom.targetValue
        val pan = animatablePan.targetValue

        // Offset for interpolating offset from (imageWidth/2,-imageWidth/2) interval
        // to (0, imageWidth) interval when
        // transform origin is TransformOrigin(0.5f,0.5f)
        val horizontalCenterOffset = width * (zoom - 1) / 2f
        val verticalCenterOffset = height * (zoom - 1) / 2f

        val bounds = getBounds()

        val offsetX = (horizontalCenterOffset - pan.x.coerceIn(-bounds.x, bounds.x))
            .coerceAtLeast(0f) / zoom
        val offsetY = (verticalCenterOffset - pan.y.coerceIn(-bounds.y, bounds.y))
            .coerceAtLeast(0f) / zoom

        val offset = Offset(offsetX, offsetY)

        val rect = getCropRect(
            bitmapWidth = imageSize.width,
            bitmapHeight = imageSize.height,
            containerWidth = width.toFloat(),
            containerHeight = height.toFloat(),
            pan = offset,
            zoom = zoom,
            rectSelection = rectDraw
        )

        println(
            "😃 EnhancedZoomState calculateRectBounds()\n" +
                    "offsetX: $offsetX, offsetY: $offsetY\n" +
                    "totalHeight: ${rect.bottom > imageSize.height}, offset: $offset\n" +
                    "containerSize: $containerSize, pan: $pan, zoom: $zoom\n" +
                    "rect: $rect, height: ${rect.height}"
        )

        return rect
    }
}
