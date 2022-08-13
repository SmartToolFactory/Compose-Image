package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


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

    internal val animatablePan = Animatable(Offset.Zero, Offset.VectorConverter)
    internal val animatableZoom = Animatable(zoomInitial)
    internal val animatableRotation = Animatable(rotationInitial)

    internal var size: IntSize = IntSize.Zero

    init {
        require(zoomMax >= zoomMin)
    }

    val pan: Offset
        get() = animatablePan.value

    val zoom: Float
        get() = animatableZoom.value

    val rotation: Float
        get() = animatableRotation.value

    val isZooming: Boolean
        get() = animatableZoom.isRunning

    val isPanning: Boolean
        get() = animatablePan.isRunning

    val isRotating: Boolean
        get() = animatableRotation.isRunning

    val isAnimationRunning: Boolean
        get() = isZooming || isPanning || isRotating

    internal open fun updateBounds(lowerBound: Offset?, upperBound: Offset?) {
        animatablePan.updateBounds(lowerBound, upperBound)
    }

    /**
     * Get bounds of Composables that can be panned based on zoom level
     * @param size is size of Composable that this modifier is applied to.
     */
    internal open fun getBounds(size: IntSize): Offset {
        val maxX = (size.width * (zoom - 1) / 2f).coerceAtLeast(0f)
        val maxY = (size.height * (zoom - 1) / 2f).coerceAtLeast(0f)
        return Offset(maxX, maxY)
    }

    /**
     * Get bounds of Composables that can be panned based on zoom level using [size]
     */
    protected fun getBounds(): Offset {
        return getBounds(size)
    }

    open suspend fun updateZoomState(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float = 1f,
    ) {
        val newZoom = (this.zoom * zoomChange).coerceIn(zoomMin, zoomMax)

        snapZoomTo(newZoom)
        val newRotation = if (rotatable) {
            this.rotation + rotationChange
        } else {
            0f
        }
        snapRotationTo(newRotation)

        if (pannable) {
            val newPan = this.pan + panChange.times(this.zoom)
            val boundPan = limitPan && !rotatable

            if (boundPan) {
                val bound = getBounds(size)
                updateBounds(bound.times(-1f), bound)
            }
            snapPanTo(newPan)
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
        launch { animatePanTo(pan) }
        launch { animateZoomTo(zoom) }
        launch { animateRotationTo(rotation) }
    }

    internal suspend fun animatePanTo(pan: Offset) {
        if (pannable) {
            animatablePan.animateTo(pan)
        }
    }

    internal suspend fun animateZoomTo(zoom: Float) {
        if (zoomable) {
            animatableZoom.animateTo(zoom)
        }
    }

    internal suspend fun animateRotationTo(rotation: Float) {
        if (rotatable) {
            animatableRotation.animateTo(rotation)
        }
    }

    internal suspend fun snapPanTo(offset: Offset) {
        if (pannable) {
            animatablePan.snapTo(offset)
        }
    }

    internal suspend fun snapZoomTo(zoom: Float) {
        if (zoomable) {
            animatableZoom.snapTo(zoom)
        }
    }

    internal suspend fun snapRotationTo(rotation: Float) {
        if (rotatable) {
            animatableRotation.snapTo(rotation)
        }
    }
}
