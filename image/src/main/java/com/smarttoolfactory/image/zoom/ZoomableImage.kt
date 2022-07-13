package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import com.smarttoolfactory.image.ImageWithConstraints

/**
 * Zoomable image that zooms in and out in [ [minZoom], [maxZoom] ] interval and translates
 * zoomed image based on pointer position.
 * Double tap gestures reset image translation and zoom to default values with animation.
 *
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value this Composable can possess
 * @param maxZoom maximum zoom value this Composable can possess
 * @param clipTransformToContentScale when set true zoomable image takes borders of image drawn
 * while zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 * @param consume flag to prevent other gestures such as scroll, drag or transform to get
 * event propagations
 * @param zoomEnabled when set to true zoom is enabled
 * @param panEnabled when set to true pan is enabled
 * @param rotationEnabled when set to true rotation is enabled
 * @param onGestureStart callback to to notify gesture has started and return current ZoomData
 * of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current ZoomData
 * of this modifier
 * @param onGestureEnd callback to notify that gesture finished and return current ZoomData
 * of this modifier
 */
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    consume: Boolean = true,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
    clipTransformToContentScale: Boolean = false,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {

    val zoomModifier = Modifier
        .zoom(
            Unit,
            limitPan = limitPan,
            zoomEnabled = zoomEnabled,
            panEnabled = panEnabled,
            rotationEnabled = rotationEnabled,
            zoomState = rememberZoomState(
                initialZoom = initialZoom,
                minZoom = minZoom,
                maxZoom = maxZoom
            ),
            consume = consume,
            onGestureStart = onGestureStart,
            onGesture = onGesture,
            onGestureEnd = onGestureEnd
        )

    ImageWithConstraints(
        modifier = if (clipTransformToContentScale) modifier else modifier.then(zoomModifier),
        imageBitmap = imageBitmap,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        drawImage = !clipTransformToContentScale
    ) {


        if (clipTransformToContentScale) {
            Image(
                bitmap = imageBitmap,
                contentScale = contentScale,
                modifier = zoomModifier,
                alignment = alignment,
                contentDescription = contentDescription,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
            )
        }
    }
}

