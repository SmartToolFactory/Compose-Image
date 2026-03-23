package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.image.util.coerceIn
import com.smarttoolfactory.image.util.rotateBy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


/**
 *  * State of the enhanced. Allows to change zoom, pan,  translate,
 *  or get current state by
 * calling methods on this object. To be hosted and passed to [Modifier.zoom]
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 *
 */
@Stable
open class ZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    internal open val zoomable: Boolean = true,
    internal open val pannable: Boolean = true,
    internal open val rotatable: Boolean = true,
    internal open val limitPan: Boolean = false
) {

    internal val zoomMin = minZoom.coerceAtLeast(.5f)
    internal val zoomMax = maxZoom.coerceAtLeast(1f)
    internal val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    internal val rotationInitial = initialRotation % 360

    internal val animatablePanX = Animatable(0f)
    internal val animatablePanY = Animatable(0f)
    internal val animatableZoom = Animatable(zoomInitial)
    internal val animatableRotation = Animatable(rotationInitial)

    internal var size: IntSize = IntSize.Zero

    init {
        animatableZoom.updateBounds(zoomMin, zoomMax)
        require(zoomMax >= zoomMin)
    }

    val pan: Offset
        get() = Offset(animatablePanX.value, animatablePanY.value)

    val zoom: Float
        get() = animatableZoom.value

    val rotation: Float
        get() = animatableRotation.value

    val isZooming: Boolean
        get() = animatableZoom.isRunning

    val isPanning: Boolean
        get() = animatablePanX.isRunning || animatablePanY.isRunning

    val isRotating: Boolean
        get() = animatableRotation.isRunning

    val isAnimationRunning: Boolean
        get() = isZooming || isPanning || isRotating

    internal open fun updateBounds(lowerBound: Offset?, upperBound: Offset?) {
        animatablePanX.updateBounds(lowerBound?.x, upperBound?.x)
        animatablePanY.updateBounds(lowerBound?.y, upperBound?.y)
    }

    /**
     * Get bounds of Composables that can be panned based on zoom level
     * @param size is size of Composable that this modifier is applied to.
     */
    internal open fun getBounds(size: IntSize): Offset {
        return getBounds(size, zoom)
    }

    /**
     * Get panning bounds for an arbitrary zoom value without mutating the current state.
     * This is used when pan limits need to be calculated from the next zoom level before
     * snapping the animatables.
     */
    internal open fun getBounds(size: IntSize, zoom: Float): Offset {
        val maxX = (size.width * (zoom - 1) / 2f).coerceAtLeast(0f)
        val maxY = (size.height * (zoom - 1) / 2f).coerceAtLeast(0f)
        return Offset(maxX, maxY)
    }

    /**
     * Get bounds of Composables that can be panned based on zoom level using [size]
     */
    protected open fun getBounds(): Offset {
        return getBounds(size)
    }

    /**
     * Convenience overload for calculating bounds from this state's current layout size and an
     * explicit zoom value.
     */
    protected open fun getBounds(zoom: Float): Offset {
        return getBounds(size, zoom)
    }

    /**
     * Updates zoom, rotation, and pan from a gesture stream.
     *
     * Transform gestures derive pan from the gesture centroid so the same content point remains
     * under the fingers while zooming or rotating. Pure pan gestures retain the legacy raw-pan
     * behavior.
     */
    open suspend fun updateZoomState(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float = 1f,
    ) {
        val previousZoom = zoom
        val previousPan = pan
        val previousRotation = rotation

        val effectiveZoomChange = if (zoomable) zoomChange else 1f
        val effectiveRotationChange = if (rotatable) rotationChange else 0f

        val newZoom = (previousZoom * effectiveZoomChange).coerceIn(zoomMin, zoomMax)
        val newRotation = if (rotatable) {
            previousRotation + effectiveRotationChange
        } else {
            0f
        }

        if (pannable) {
            val boundPan = limitPan && !rotatable
            val transformGesture =
                abs(effectiveZoomChange - 1f) > TRANSFORM_EPSILON ||
                    abs(effectiveRotationChange) > TRANSFORM_EPSILON

            val newPan = if (transformGesture) {
                calculateAnchoredPan(
                    size = size,
                    centroid = centroid,
                    previousPan = previousPan,
                    previousZoom = previousZoom,
                    previousRotation = previousRotation,
                    newZoom = newZoom,
                    newRotation = newRotation
                )
            } else {
                previousPan + panChange.times(previousZoom)
            }

            val boundedPan = if (boundPan) {
                val bound = getBounds(newZoom)
                updateBounds(bound.times(-1f), bound)
                newPan.coerceIn(-bound.x..bound.x, -bound.y..bound.y)
            } else {
                newPan
            }

            snapZoomTo(newZoom)
            snapRotationTo(newRotation)
            snapPanXto(boundedPan.x)
            snapPanYto(boundedPan.y)
        } else {
            snapZoomTo(newZoom)
            snapRotationTo(newRotation)
        }
    }

    /**
     * Reset [pan], [zoom] and [rotation] with animation.
     */
    internal open suspend fun resetWithAnimation(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f
    ) = coroutineScope {
        launch { animatePanXto(pan.x) }
        launch { animatePanYto(pan.y) }
        launch { animateZoomTo(zoom) }
        launch { animateRotationTo(rotation) }
    }

    internal suspend fun animatePanXto(panX: Float) {
        if (pannable && pan.x != panX) {
            animatablePanX.animateTo(panX)
        }
    }

    internal suspend fun animatePanYto(panY: Float) {
        if (pannable && pan.y != panY) {
            animatablePanY.animateTo(panY)
        }
    }

    internal suspend fun animateZoomTo(zoom: Float) {
        if (zoomable && this.zoom != zoom) {
            val newZoom = zoom.coerceIn(zoomMin, zoomMax)
            animatableZoom.animateTo(newZoom)
        }
    }

    internal suspend fun animateRotationTo(rotation: Float) {
        if (rotatable && this.rotation != rotation) {
            animatableRotation.animateTo(rotation)
        }
    }

    internal suspend fun snapPanXto(panX: Float) {
        if (pannable) {
            animatablePanX.snapTo(panX)
        }
    }

    internal suspend fun snapPanYto(panY: Float) {
        if (pannable) {
            animatablePanY.snapTo(panY)
        }
    }

    internal suspend fun snapZoomTo(zoom: Float) {
        if (zoomable) {
            animatableZoom.snapTo(zoom.coerceIn(zoomMin, zoomMax))
        }
    }

    internal suspend fun snapRotationTo(rotation: Float) {
        if (rotatable) {
            animatableRotation.snapTo(rotation)
        }
    }

    /**
     * Solves the pan required to keep the content under [centroid] fixed while the zoom or
     * rotation changes. The calculation projects the centroid into content space using the
     * previous transform, then reapplies the next transform and offsets pan to match.
     */
    private fun calculateAnchoredPan(
        size: IntSize,
        centroid: Offset,
        previousPan: Offset,
        previousZoom: Float,
        previousRotation: Float,
        newZoom: Float,
        newRotation: Float
    ): Offset {
        val center = Offset(size.width / 2f, size.height / 2f)
        val centroidFromCenter = centroid - center
        val transformedVectorBefore = centroidFromCenter - previousPan
        val contentVector = transformedVectorBefore
            .rotateBy(-previousRotation)
            .times(1f / previousZoom)
        val transformedVectorAfter = contentVector
            .rotateBy(newRotation)
            .times(newZoom)
        return centroidFromCenter - transformedVectorAfter
    }

    private companion object {
        const val TRANSFORM_EPSILON = 0.001f
    }
}
