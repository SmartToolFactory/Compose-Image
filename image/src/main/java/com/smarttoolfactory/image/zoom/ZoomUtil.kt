package com.smarttoolfactory.image.zoom

fun calculateZoom(
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
            newZoom = max
        }
        ZoomLevel.Max -> {
            newZoomLevel = ZoomLevel.Min
            newZoom = if(min == initial) (min +max)/2 else min
        }
        else -> {
            newZoomLevel = ZoomLevel.Initial
            newZoom = initial
        }
    }
    return Pair(newZoomLevel, newZoom)
}