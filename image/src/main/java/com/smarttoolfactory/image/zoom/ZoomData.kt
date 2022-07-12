package com.smarttoolfactory.image.zoom

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

/**
 * class that contains current zoom, pan and rotation information
 */
@Immutable
data class ZoomData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f
)
