package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


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
@Immutable
open class ZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    internal open val zoomEnabled: Boolean = true,
    internal open val panEnabled: Boolean = true,
    internal open val rotationEnabled: Boolean = true,
    internal open val limitPan: Boolean = false
) {

    internal val zoomMin = minZoom.coerceAtLeast(.5f)
    internal val zoomMax = maxZoom.coerceAtLeast(1f)
    internal val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    internal val rotationInitial = initialRotation % 360

    internal val animatablePan = Animatable(Offset.Zero, Offset.VectorConverter)
    internal val animatableZoom = Animatable(zoomInitial)
    internal val animatableRotation = Animatable(rotationInitial)

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

    val zoomData: ZoomData
        get() = ZoomData(
            zoom = zoom,
            pan = pan,
            rotation = rotation
        )

    internal open fun updateBounds(lowerBound: Offset?, upperBound: Offset?) {
        animatablePan.updateBounds(lowerBound, upperBound)
    }

    internal open fun getBounds(size: IntSize): Offset {
        val maxX = (size.width * (zoom - 1) / 2f).coerceAtLeast(0f)
        val maxY = (size.height * (zoom - 1) / 2f).coerceAtLeast(0f)
        return Offset(maxX, maxY)
    }

    open suspend fun updateZoomState(
        size: IntSize,
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float = 1f,
    ) {
        val newZoom = (this.zoom * zoomChange).coerceIn(zoomMin, zoomMax)

        snapZoomTo(newZoom)
        val newRotation = if (rotationEnabled) {
            this.rotation + rotationChange
        } else {
            0f
        }
        snapRotationTo(newRotation)

        if (panEnabled) {
            val newPan = this.pan + panChange.times(this.zoom)
            val boundPan = limitPan && !rotationEnabled

            if (boundPan) {
                val bound = getBounds(size)
                updateBounds(bound.times(-1f), bound)
            }
            snapPanTo(newPan)
        }
    }

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
        if (panEnabled) {
            animatablePan.animateTo(pan)
        }
    }

    internal suspend fun animateZoomTo(zoom: Float) {
        if (zoomEnabled) {
            animatableZoom.animateTo(zoom)
        }
    }

    internal suspend fun animateRotationTo(rotation: Float) {
        if (rotationEnabled) {
            animatableRotation.animateTo(rotation)
        }
    }

    internal suspend fun snapPanTo(offset: Offset) {
        if (panEnabled) {
            animatablePan.snapTo(offset)
        }
    }

    internal suspend fun snapZoomTo(zoom: Float) {
        if (zoomEnabled) {
            animatableZoom.snapTo(zoom)
        }
    }

    internal suspend fun snapRotationTo(rotation: Float) {
        if (rotationEnabled) {
            animatableRotation.snapTo(rotation)
        }
    }
}
