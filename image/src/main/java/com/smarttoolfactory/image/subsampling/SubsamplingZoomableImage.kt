package com.smarttoolfactory.image.subsampling

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.image.ImageWithConstraints
import com.smarttoolfactory.image.zoom.EnhancedZoomData
import com.smarttoolfactory.image.zoom.enhancedZoom
import com.smarttoolfactory.image.zoom.rememberEnhancedZoomState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Displays a preview painter immediately and decodes higher detail source regions as the user zooms.
 */
@Composable
fun SubsamplingZoomableImage(
    source: SubsamplingImageSource,
    previewPainter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
) {
    val previewIntrinsicSize = previewPainter.intrinsicSize
    val hasValidPreviewSize = previewIntrinsicSize != Size.Unspecified &&
        previewIntrinsicSize.width.isFinite() &&
        previewIntrinsicSize.height.isFinite() &&
        previewIntrinsicSize.width > 0f &&
        previewIntrinsicSize.height > 0f

    if (!hasValidPreviewSize) {
        Image(
            painter = previewPainter,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale
        )
    } else {
        val context = LocalContext.current.applicationContext
        val previewSize = remember(previewIntrinsicSize) {
            IntSize(
                width = previewIntrinsicSize.width.roundToInt(),
                height = previewIntrinsicSize.height.roundToInt()
            )
        }

        val decoderHolder by produceState<BitmapRegionDecoderHolder?>(initialValue = null, context, source) {
            val holder = withContext(Dispatchers.IO) {
                BitmapRegionDecoderHolder.open(context = context, source = source)
            }
            value = holder
            awaitDispose {
                holder.close()
            }
        }

        ImageWithConstraints(
            modifier = modifier,
            alignment = alignment,
            painter = previewPainter,
            contentScale = contentScale,
            alpha = DefaultAlpha,
            colorFilter = null,
            drawImage = false,
            contentDescription = contentDescription,
        ) {
            val density = LocalDensity.current
            val contentSize = IntSize(
                width = with(density) { imageWidth.roundToPx() },
                height = with(density) { imageHeight.roundToPx() }
            )

            val holder = decoderHolder
            if (holder == null) {
                Canvas(
                    modifier = Modifier.size(imageWidth, imageHeight)
                ) {
                    drawPreviewPainter(
                        previewPainter = previewPainter,
                        previewSize = previewSize,
                        previewRect = rect
                    )
                }
            } else {
                val baseSourceRect = remember(holder.key, holder.imageSize, rect, previewSize) {
                    mapPreviewRectToSourceRect(
                        previewRect = rect,
                        previewSize = previewSize,
                        sourceSize = holder.imageSize
                    )
                }

                val subsamplingStartZoom = remember(rect, contentSize) {
                    calculateSubsamplingStartZoom(
                        previewRect = rect,
                        containerSize = contentSize
                    )
                }

                val enhancedZoomState = rememberEnhancedZoomState(
                    imageSize = baseSourceRect.size,
                    minZoom = 1f,
                    maxZoom = 8f,
                    fling = true,
                    moveToBounds = true,
                    zoomable = true,
                    pannable = true,
                    rotatable = false,
                    limitPan = true,
                    key1 = holder.key,
                    key2 = listOf(baseSourceRect, contentSize, contentScale)
                )

                var zoomData by remember(holder.key, baseSourceRect) { mutableStateOf<EnhancedZoomData?>(null) }
                var decodedBitmapResult by remember(holder.key, baseSourceRect) {
                    mutableStateOf<DecodedBitmapResult?>(null)
                }

                val decodeRequest = remember(baseSourceRect, contentSize, subsamplingStartZoom, zoomData) {
                    val currentZoomData = zoomData
                    if (currentZoomData != null && currentZoomData.zoom > subsamplingStartZoom) {
                        calculateDecodeRequest(
                            baseSourceRect = baseSourceRect,
                            visibleRegion = currentZoomData.visibleRegion,
                            containerSize = contentSize,
                            zoom = currentZoomData.zoom
                        )
                    } else {
                        null
                    }
                }

                val activeDecodedRegion = remember(decodedBitmapResult, decodeRequest) {
                    if (decodeRequest == null) {
                        null
                    } else {
                        decodedBitmapResult?.takeIf { decoded ->
                            decoded.inSampleSize <= decodeRequest.inSampleSize &&
                                decoded.sourceRect.contains(decodeRequest.visibleSourceRect)
                        }
                    }
                }

                val decodedImageBitmap: ImageBitmap? = remember(activeDecodedRegion?.bitmap) {
                    activeDecodedRegion?.bitmap?.asImageBitmap()
                }

                LaunchedEffect(enhancedZoomState, contentSize) {
                    if (contentSize.width > 0 && contentSize.height > 0) {
                        enhancedZoomState.size = contentSize
                        withFrameNanos { zoomData = enhancedZoomState.enhancedZoomData }
                    }
                }

                LaunchedEffect(holder.key, decodeRequest) {
                    val request = decodeRequest
                    if (request != null) {
                        val current = decodedBitmapResult
                        val isCurrentRequestCovered = current != null &&
                            current.inSampleSize <= request.inSampleSize &&
                            current.sourceRect.contains(request.visibleSourceRect)

                        if (!isCurrentRequestCovered) {
                            val cacheKey = request.cacheKey(holder.key)
                            val cachedResult = SubsamplingBitmapCache.get(cacheKey)

                            if (cachedResult != null) {
                                decodedBitmapResult = cachedResult
                            } else {
                                val decodedResult = withContext(Dispatchers.IO) {
                                    holder.decodeRegion(request)
                                }

                                if (decodedResult != null) {
                                    SubsamplingBitmapCache.put(cacheKey, decodedResult)
                                    decodedBitmapResult = decodedResult
                                }
                            }
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .size(imageWidth, imageHeight)
                        .onSizeChanged {
                            enhancedZoomState.size = it
                            zoomData = enhancedZoomState.enhancedZoomData
                        }
                        .enhancedZoom(
                            enhancedZoomState = enhancedZoomState,
                            enabled = { zoom, _, _ -> zoom > 1f },
                            onGestureStart = { zoomData = it },
                            onGesture = { zoomData = it },
                            onGestureEnd = { zoomData = it }
                        )
                ) {
                    drawPreviewPainter(
                        previewPainter = previewPainter,
                        previewSize = previewSize,
                        previewRect = rect
                    )

                    if (decodedImageBitmap != null && activeDecodedRegion != null) {
                        drawDecodedRegion(
                            imageBitmap = decodedImageBitmap,
                            decodedRegion = activeDecodedRegion.sourceRect,
                            baseSourceRect = baseSourceRect,
                            filterQuality = FilterQuality.None
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPreviewPainter(
    previewPainter: Painter,
    previewSize: IntSize,
    previewRect: IntRect
) {
    val canvasWidth = size.width
    val canvasHeight = size.height

    val scaledWidth = previewSize.width * canvasWidth / previewRect.width.toFloat()
    val scaledHeight = previewSize.height * canvasHeight / previewRect.height.toFloat()

    translate(
        left = (-scaledWidth + canvasWidth) / 2f,
        top = (-scaledHeight + canvasHeight) / 2f
    ) {
        with(previewPainter) {
            draw(size = Size(scaledWidth, scaledHeight))
        }
    }
}

private fun DrawScope.drawDecodedRegion(
    imageBitmap: ImageBitmap,
    decodedRegion: IntRect,
    baseSourceRect: IntRect,
    filterQuality: FilterQuality
) {
    val regionWithinBase = IntRect(
        offset = IntOffset(
            x = decodedRegion.left - baseSourceRect.left,
            y = decodedRegion.top - baseSourceRect.top
        ),
        size = decodedRegion.size
    )

    val dstOffset = IntOffset(
        x = (regionWithinBase.left * size.width / baseSourceRect.width).roundToInt(),
        y = (regionWithinBase.top * size.height / baseSourceRect.height).roundToInt()
    )
    val dstSize = IntSize(
        width = (regionWithinBase.width * size.width / baseSourceRect.width).roundToInt(),
        height = (regionWithinBase.height * size.height / baseSourceRect.height).roundToInt()
    )

    drawImage(
        image = imageBitmap,
        dstOffset = dstOffset,
        dstSize = dstSize,
        filterQuality = filterQuality
    )
}
