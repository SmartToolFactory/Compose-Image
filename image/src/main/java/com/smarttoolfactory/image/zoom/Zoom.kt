package com.smarttoolfactory.image.zoom

import androidx.compose.runtime.Immutable

@Immutable
data class Zoom(
    val zoom: Float = 1f,
    val translationX: Float = 0f,
    val translationY: Float = 0f,
    val rotation: Float = 0f
)
