package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.coroutineScope

@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f
): ZoomState {
    return remember {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom
        )
    }
}

class ZoomState internal constructor(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f
) {
    internal var zoomLevel = ZoomLevel.Min

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

    val zoomData: ZoomData
        get() = ZoomData(
            zoom = animatableZoom.value,
            pan = animatablePan.value,
            rotation = animatableRotation.value
        )


    fun boundPan(maxX: Float, maxY: Float) {
        animatablePan.updateBounds(
            Offset(-maxX, -maxY),
            Offset(maxX, maxY)
        )
    }

    suspend fun animatePanTo(pan: Offset) = coroutineScope {
        animatablePan.animateTo(pan)
    }

    suspend fun animateZoomTo(zoom: Float) = coroutineScope {
        animatableZoom.animateTo(zoom)
    }

    suspend fun animateRotationTo(rotation: Float) = coroutineScope {
        animatableRotation.animateTo(rotation)
    }

    suspend fun snapPanTo(offset: Offset) = coroutineScope {
        animatablePan.snapTo(offset)
    }

    suspend fun snapZoomTo(zoom: Float) = coroutineScope {
        animatableZoom.snapTo(zoom)
    }

    suspend fun snapRotationTo(rotation: Float) = coroutineScope {
        animatableRotation.snapTo(rotation)
    }
}