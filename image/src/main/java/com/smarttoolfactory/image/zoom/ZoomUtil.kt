package com.smarttoolfactory.image.zoom

import androidx.compose.ui.graphics.GraphicsLayerScope

/**
 * Calculate zoom level and zoom value when user double taps
 */
internal fun calculateZoom(
    zoomLevel: ZoomLevel,
    initial: Float,
    min: Float,
    max: Float
): Pair<ZoomLevel, Float> {

    val newZoomLevel: ZoomLevel
    val newZoom: Float

    when (zoomLevel) {
        ZoomLevel.Initial -> {
            newZoomLevel = ZoomLevel.Max
            newZoom = max.coerceAtMost(3f)
        }
        ZoomLevel.Max -> {
            newZoomLevel = ZoomLevel.Min
            newZoom = if (min == initial) (min + max.coerceAtMost(3f)) / 2 else min
        }
        else -> {
            newZoomLevel = ZoomLevel.Initial
            newZoom = initial.coerceAtMost(2f)
        }
    }
    return Pair(newZoomLevel, newZoom)
}

/**
 * Update graphic layer with [zoomState]
 */
internal fun GraphicsLayerScope.update(zoomState: ZoomState) {

    // Set zoom
    val zoom = zoomState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    // Set pan
    val pan = zoomState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY

    // Set rotation
    this.rotationZ = zoomState.rotation
}

