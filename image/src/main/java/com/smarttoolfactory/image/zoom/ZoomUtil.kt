package com.smarttoolfactory.image.zoom

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