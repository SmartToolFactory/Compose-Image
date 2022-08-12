package com.smarttoolfactory.composeimage.demo.zoom

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color(0xffECEFF1))
    ) {

        val imageBitmapLarge = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape1
        )

        EnhancedZoomModifierSample(imageBitmap = imageBitmapLarge)
        CallbackAndCropSample(imageBitmap = imageBitmapLarge)
    }
}

@Composable
private fun EnhancedZoomModifierSample(imageBitmap: ImageBitmap) {
    Column(
        modifier = Modifier
    ) {

        val width = imageBitmap.width
        val height = imageBitmap.height

        Text(
            text = "Enhanced Zoom Modifier",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "clip = true\n" +
                    "limitPan = false\n" +
                    "moveToBoundsEnabled = true",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .enhancedZoom(
                    clip = true,
                    enhancedZoomState = rememberEnhancedZoomState(
                        minZoom = .5f,
                        imageSize = IntSize(width, height),
                        limitPan = false,
                        moveToBoundsEnabled = true
                    )
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "clip = true\n" +
                    "limitPan = true\n" +
                    "moveToBoundsEnabled = true",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .enhancedZoom(
                    clip = true,
                    enhancedZoomState = rememberEnhancedZoomState(
                        minZoom = .5f,
                        imageSize = IntSize(width, height),
                        limitPan = true,
                        moveToBoundsEnabled = true
                    )
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "clip = true\n" +
                    "limitPan = true\n" +
                    "moveToBoundsEnabled = true\n" +
                    "flingGestureEnabled = true",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .enhancedZoom(
                    clip = true,
                    enhancedZoomState = rememberEnhancedZoomState(
                        imageSize = IntSize(width, height),
                        limitPan = false,
                        moveToBoundsEnabled = true,
                        flingGestureEnabled = true
                    )
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "clip = true\n" +
                    "rotate = true\n" +
                    "moveToBoundsEnabled = false\n" +
                    "flingGestureEnabled = true",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .enhancedZoom(
                    clip = true,
                    enhancedZoomState = rememberEnhancedZoomState(
                        imageSize = IntSize(width, height),
                        minZoom = .5f,
                        limitPan = true,
                        rotationEnabled = true,
                        moveToBoundsEnabled = false,
                        flingGestureEnabled = true
                    )
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
private fun CallbackAndCropSample(imageBitmap: ImageBitmap) {
    Spacer(modifier = Modifier.height(40.dp))

    // ⚠️ getting Rect and creating bitmap on each frame is for demonstration only
    // get rect on up motion and when gesture finished running
    Text(
        text = "Callback and Crop",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    var rectCrop by remember {
        mutableStateOf(
            Rect(
                offset = Offset.Zero,
                size = Size(
                    imageBitmap.width.toFloat(),
                    imageBitmap.height.toFloat()
                )
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        val widthInDp = maxWidth
        val heightInDp = maxHeight


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
            flingGestureEnabled = true,
            imageSize = IntSize(imageBitmap.width, imageBitmap.height)
        )

        val modifier = Modifier
            .clipToBounds()
            .enhancedZoom(
                enhancedZoomState = zoomState,
                onGestureStart = { zoomData: EnhancedZoomData ->
                    rectDraw = zoomData.imageRegion
                    rectCrop = zoomData.visibleRegion
                },
                onGesture = { zoomData: EnhancedZoomData ->
                    rectDraw = zoomData.imageRegion
                    rectCrop = zoomData.visibleRegion
                },
                onGestureEnd = { zoomData: EnhancedZoomData ->
                    rectDraw = zoomData.imageRegion
                    rectCrop = zoomData.visibleRegion
                }
            )
            .size(widthInDp, heightInDp)

        Box {
            Image(
                modifier = modifier,
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            DrawingOverlay(
                modifier = Modifier.size(widthInDp, heightInDp),
                rect = rectDraw
            )


            Text(
                "isAnimating: ${zoomState.isAnimationRunning}, " +
                        "isPanAnimating: ${zoomState.isPanning}, " +
                        "isZooming: ${zoomState.isZooming}\n" +
                        "rectDraw: $rectDraw\n" +
                        "rectCrop: $rectCrop",
                color = Color.Cyan
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    if (rectCrop.size != Size.Zero) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Visible Section of Image")
            Image(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .aspectRatio(4 / 3f),
                bitmap = Bitmap.createBitmap(
                    imageBitmap.asAndroidBitmap(),
                    rectCrop.left.toInt(),
                    rectCrop.top.toInt(),
                    rectCrop.width.toInt().coerceAtMost(imageBitmap.width),
                    rectCrop.height.toInt().coerceAtMost(imageBitmap.height),
                ).asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    rect: Rect
) {
    Canvas(modifier = modifier) {
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


