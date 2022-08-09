package com.smarttoolfactory.composeimage.demo

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.zoom.EnhancedZoomData
import com.smarttoolfactory.image.zoom.enhancedZoom
import com.smarttoolfactory.image.zoom.rememberEnhancedZoomState

@Composable
fun EnhancedZoomDemo() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.Gray)
            .padding(top = 40.dp)
    ) {

        val imageBitmapLarge = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape6
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
        ) {
            val width = constraints.maxWidth
            val height = constraints.maxHeight


            var rectCrop by remember {
                mutableStateOf(
                    Rect(
                        offset = Offset.Zero,
                        size = Size(
                            imageBitmapLarge.width.toFloat(),
                            imageBitmapLarge.height.toFloat()
                        )
                    )
                )
            }

            var rectDraw by remember {
                mutableStateOf(
                    Rect(
                        offset = Offset.Zero,
                        size = Size(width.toFloat(), height.toFloat())
                    )
                )
            }


            val zoomState = rememberEnhancedZoomState(
                minZoom = .5f,
                imageSize = IntSize(imageBitmapLarge.width, imageBitmapLarge.height),
                containerSize = IntSize(width, height)
            )

            val modifier = Modifier
                .clipToBounds()
                .enhancedZoom(
                    enhancedZoomState = zoomState,
                    onDown = { zoomData: EnhancedZoomData ->
                        rectDraw = zoomData.drawRect
                        rectCrop = zoomData.cropRect
                    },
                    onMove = { zoomData: EnhancedZoomData ->
                        rectDraw = zoomData.drawRect
                        rectCrop = zoomData.cropRect
                    },
                    onUp = { zoomData: EnhancedZoomData ->
                        rectDraw = zoomData.drawRect
                        rectCrop = zoomData.cropRect
                    }
                )
                .size(maxWidth, maxHeight)


            Image(
                modifier = modifier,
                bitmap = imageBitmapLarge,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            DrawingOverlay(
                modifier = Modifier.size(maxWidth, maxHeight),
                rect = rectDraw,
                touchRegionWidth = 100f
            )

            if (rectCrop.size != Size.Zero) {
                Image(
                    modifier = Modifier
                        .offset {
                            IntOffset(0, 900)
                        }
                        .size(maxWidth, maxHeight),
                    bitmap = Bitmap.createBitmap(
                        imageBitmapLarge.asAndroidBitmap(),
                        rectCrop.left.toInt(),
                        rectCrop.top.toInt(),
                        rectCrop.width.toInt().coerceAtMost(imageBitmapLarge.width),
                        rectCrop.height.toInt().coerceAtMost(imageBitmapLarge.height),
                    ).asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }

            Text(
                "isAnimating: ${zoomState.isAnimationRunning}, " +
                        "isPanAnimating: ${zoomState.isPanning}, " +
                        "isZooming: ${zoomState.isZooming}\n" +
                        "rectDraw: $rectDraw",
                color = Color.Green
            )
        }
    }
}

@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    rect: Rect,
    touchRegionWidth: Float
) {

    val path = remember(rect) {
        Path().apply {

            if (rect != Rect.Zero) {
                // Top left lines
                moveTo(rect.topLeft.x, rect.topLeft.y + touchRegionWidth)
                lineTo(rect.topLeft.x, rect.topLeft.y)
                lineTo(rect.topLeft.x + touchRegionWidth, rect.topLeft.y)

                // Top right lines
                moveTo(rect.topRight.x - touchRegionWidth, rect.topRight.y)
                lineTo(rect.topRight.x, rect.topRight.y)
                lineTo(rect.topRight.x, rect.topRight.y + touchRegionWidth)

                // Bottom right lines
                moveTo(rect.bottomRight.x, rect.bottomRight.y - touchRegionWidth)
                lineTo(rect.bottomRight.x, rect.bottomRight.y)
                lineTo(rect.bottomRight.x - touchRegionWidth, rect.bottomRight.y)

                // Bottom left lines
                moveTo(rect.bottomLeft.x + touchRegionWidth, rect.bottomLeft.y)
                lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
                lineTo(rect.bottomLeft.x, rect.bottomLeft.y - touchRegionWidth)
            }
        }
    }

    Canvas(modifier = modifier) {

        val color = Color.White
        val strokeWidth = 2.dp.toPx()

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRect(Color(0x77000000))

            // Source
            drawRect(
                topLeft = rect.topLeft,
                size = rect.size,
                color = Color.Transparent,
                blendMode = BlendMode.Clear
            )
            restoreToCount(checkPoint)
        }

        drawGrid(rect)
        drawPath(
            path,
            color,
            style = Stroke(strokeWidth * 2)
        )
    }
}


fun DrawScope.drawGrid(rect: Rect, color: Color = Color.White) {

    val width = rect.width
    val height = rect.height
    val gridWidth = width / 3
    val gridHeight = height / 3


    drawRect(
        color = color,
        topLeft = rect.topLeft,
        size = rect.size,
        style = Stroke(width = 2.dp.toPx())
    )

    // Horizontal lines
    for (i in 1..2) {
        drawLine(
            color = color,
            start = Offset(rect.left, rect.top + i * gridHeight),
            end = Offset(rect.right, rect.top + i * gridHeight),
            strokeWidth = .7.dp.toPx()
        )
    }

    // Vertical lines
    for (i in 1..2) {
        drawLine(
            color,
            start = Offset(rect.left + i * gridWidth, rect.top),
            end = Offset(rect.left + i * gridWidth, rect.bottom),
            strokeWidth = .7.dp.toPx()
        )
    }
}


