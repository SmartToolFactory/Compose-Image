package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.composeimage.TitleMedium
import com.smarttoolfactory.image.zoom.ZoomableImage

/**
 * Compares the centroid-stable zoom implementation against a naive drifting zoom.
 *
 * Both samples draw the same centroid overlay so the image content can be compared against the
 * fixed pinch-start marker while pinching on the same off-center detail.
 */
@Preview
@Composable
fun ZoomComparisonDemo() {
    val imageBitmap = ImageBitmap.imageResource(
        LocalResources.current,
        R.drawable.landscape4
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xffECEFF1))
            .padding(bottom = 20.dp)
    ) {
        TitleMedium(text = "Zoom Comparison")
        Text(
            text = "Pinch the same off-center detail in both images. The top image keeps that detail under the pinch-start marker. The bottom image uses the old drifting transform path for comparison.",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Text(
            text = "CYAN = pinch start, MAGENTA = current pinch centroid",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComparisonPane(
            title = "Anchored Zoom",
            description = "Current library behavior. Pinch zoom keeps the touched content stable under the centroid."
        ) {
            StableZoomImage(imageBitmap = imageBitmap)
        }

        Spacer(modifier = Modifier.height(20.dp))

        ComparisonPane(
            title = "Regular Zoom",
            description = "Naive transform path. Zoom scales around the viewport center and mixes in raw pan, so off-center content drifts while pinching."
        ) {
            DriftingZoomImage(imageBitmap = imageBitmap)
        }
    }
}

/**
 * Presents one labeled comparison sample in the stacked zoom comparison demo.
 */
@Composable
private fun ComparisonPane(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        content()
    }
}

/**
 * Shows the current centroid-stable zoomable image implementation.
 */
@Composable
private fun StableZoomImage(imageBitmap: ImageBitmap) {
    PinchCentroidDebugOverlay(
        modifier = demoImageModifier(),
        showLegend = false
    ) {
        ZoomableImage(
            modifier = Modifier.fillMaxSize(),
            imageBitmap = imageBitmap,
            contentScale = ContentScale.FillBounds,
            clip = true,
            limitPan = false
        )
    }
}

/**
 * Shows a naive zoom implementation that ignores the pinch centroid and reproduces drift.
 */
@Composable
private fun DriftingZoomImage(imageBitmap: ImageBitmap) {
    PinchCentroidDebugOverlay(
        modifier = demoImageModifier(),
        showLegend = false
    ) {
        var zoom by remember { mutableStateOf(1f) }
        var pan by remember { mutableStateOf(Offset.Zero) }

        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, panChange, zoomChange, _ ->
                        val previousZoom = zoom
                        val newZoom = (previousZoom * zoomChange).coerceIn(1f, 5f)
                        val newPan = pan + panChange.times(previousZoom)

                        zoom = newZoom
                        pan = newPan
                    }
                }
                .graphicsLayer {
                    scaleX = zoom
                    scaleY = zoom
                    translationX = pan.x
                    translationY = pan.y
                }
        )
    }
}

/**
 * Shared frame styling for each comparison image.
 */
@Composable
private fun demoImageModifier(): Modifier {
    return Modifier
        .fillMaxWidth()
        .aspectRatio(4 / 3f)
        .border(2.dp, Color(0xff37474F))
        .background(Color.LightGray)
        .clipToBounds()
}
