package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.ImageWithConstraints
import kotlinx.coroutines.launch

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
    clipTransformToContentScale: Boolean = false,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {

    val coroutineScope = rememberCoroutineScope()
    val zoomMin = minZoom.coerceAtLeast(.5f)
    val zoomMax = maxZoom.coerceAtLeast(1f)
    val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)

    require(zoomMax >= zoomMin)

    var size by remember { mutableStateOf(Size.Zero) }

    val animatableOffset = remember(imageBitmap, contentScale) {
        Animatable(Offset.Zero, Offset.VectorConverter)
    }
    val animatableZoom = remember(imageBitmap, contentScale) { Animatable(zoomInitial) }

    val zoomModifier = Modifier
        .clipToBounds()
        .graphicsLayer {
            val zoom = animatableZoom.value
            translationX = animatableOffset.value.x
            translationY = animatableOffset.value.y
            scaleX = zoom
            scaleY = zoom
        }
        .pointerInput(imageBitmap, contentScale) {

            detectTransformGestures(
                onGesture = { _,
                              gesturePan: Offset,
                              gestureZoom: Float,
                              _,
                              _,
                              _ ->

                    var zoom = animatableZoom.value
                    val offset = animatableOffset.value

                    zoom = (zoom * gestureZoom).coerceIn(zoomMin, zoomMax)
                    val newOffset = offset + gesturePan.times(zoom)

                    val maxX = (size.width * (zoom - 1) / 2f).coerceAtLeast(0f)
                    val maxY = (size.height * (zoom - 1) / 2f).coerceAtLeast(0f)

                    coroutineScope.launch {
                        animatableZoom.snapTo(zoom)
                    }
                    coroutineScope.launch {
                        animatableOffset.snapTo(
                            Offset(
                                newOffset.x.coerceIn(-maxX, maxX),
                                newOffset.y.coerceIn(-maxY, maxY)
                            )
                        )
                    }
                }
            )
        }
        .pointerInput(imageBitmap, contentScale) {
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        animatableOffset.animateTo(Offset.Zero, spring())
                    }
                    coroutineScope.launch {
                        animatableZoom.animateTo(zoomInitial, spring())
                    }
                }
            )
        }

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

        size = with(LocalDensity.current) {
            Size(
                width = imageWidth.toPx(),
                height = imageHeight.toPx()
            )
        }

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

