package com.smarttoolfactory.composeimage.demo.zoom

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.smarttoolfactory.composeimage.ContentScaleSelectionMenu
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.composeimage.TitleMedium
import com.smarttoolfactory.image.DimensionSubcomposeLayout
import com.smarttoolfactory.image.zoom.EnhancedZoomData
import com.smarttoolfactory.image.zoom.ZoomLevel
import com.smarttoolfactory.image.zoom.enhancedZoom
import com.smarttoolfactory.image.zoom.rememberEnhancedZoomState

/**
 * Demo for getting image rect for cropping on pan, and zoom.
 * Rotation is not working at the moment
 */
@Composable
fun EnhancedZoomCropDemo() {

    println("⛺️ EnhancedZoomCropDemo")


    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape5
    )

    var contentScale by remember { mutableStateOf(ContentScale.FillBounds) }

    Column(modifier = Modifier.fillMaxSize()) {
        ContentScaleSelectionMenu(contentScale = contentScale) {
            contentScale = it
        }

        CallbackAndCropSample(imageBitmap = imageBitmap, contentScale = contentScale)
    }
}

@Composable
private fun CallbackAndCropSample(imageBitmap: ImageBitmap, contentScale: ContentScale) {

    // ⚠️ getting Rect and creating bitmap on each frame is for demonstration only
    // get rect on up motion and when gesture finished running
    TitleMedium(text = "Callback and Crop")
    Text(
        text = "Modifier.enhanced can be used for getting visible image",
        fontSize = 14.sp
    )
    var rectDraw by remember {
        mutableStateOf(
            Rect(
                offset = Offset.Zero,
                size = Size.Zero
            )
        )
    }

    var rectCrop by remember {
        mutableStateOf(
            IntRect(
                offset = IntOffset.Zero,
                size = IntSize(imageBitmap.width, imageBitmap.height)
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }

    val croppedImage = remember(imageBitmap, rectCrop) {
        Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            rectCrop.left,
            rectCrop.top,
            rectCrop.width,
            rectCrop.height
        ).asImageBitmap()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {


        val zoomState = rememberEnhancedZoomState(
            minZoom = 1f,
            maxZoom = 5f,
            fling = true,
            moveToBounds = true,
            // 🔥 This is required for getting visible section of image. If you don't
            // provide it rect returned will be the whole image
            imageSize = IntSize(imageBitmap.width, imageBitmap.height)
        )

        val modifier = Modifier
            .clipToBounds()
            .enhancedZoom(
                enhancedZoomState = zoomState,
                enabled = { zoom, pan, rotation ->
                    true
                },
                zoomOnDoubleTap = { zoomLevel: ZoomLevel ->
                    when (zoomLevel) {
                        ZoomLevel.Min -> 1f
                        ZoomLevel.Mid -> 2f
                        ZoomLevel.Max -> 3f
                    }
                },
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
            .fillMaxWidth()
            .aspectRatio(4 / 3f)

        Column {
            Text(
                text = "isAnimating: ${zoomState.isAnimationRunning}, " +
                        "panning: ${zoomState.isPanning}, " +
                        "zooming: ${zoomState.isZooming}",
                color = if (zoomState.isAnimationRunning) Color.Red else Color.Green,
                fontSize = 14.sp
            )
            Text(
                text = "rectCrop: $rectCrop",
                color = Color.Magenta,
                fontSize = 14.sp
            )

            DimensionSubcomposeLayout(modifier = Modifier.background(Color.LightGray),
                mainContent = {
                    Image(
                        modifier = modifier,
                        bitmap = imageBitmap,
                        contentDescription = null,
                        contentScale = contentScale
                    )
                }
            ) { size: Size ->

                val dpSize = LocalDensity.current.run { size.toDpSize() }
                rectDraw = Rect(Offset.Zero, size = size)
                DrawingOverlay(
                    modifier = Modifier.size(dpSize),
                    rect = rectDraw
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (rectCrop.size != IntSize.Zero) {

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
                            rectCrop.left,
                            rectCrop.top,
                            rectCrop.width.coerceAtMost(imageBitmap.width),
                            rectCrop.height.coerceAtMost(imageBitmap.height),
                        ).asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = { showDialog = true }
                ) {
                    Icon(imageVector = Icons.Default.Crop, contentDescription = null)
                }

            }

            ImageDialog(
                showDialog = showDialog,
                imageBitmap = imageBitmap,
                croppedImage = croppedImage
            ) {
                showDialog = false
            }
        }
    }
}

@Composable
private fun ImageDialog(
    showDialog: Boolean,
    imageBitmap: ImageBitmap,
    croppedImage: ImageBitmap,
    onDismissRequest: () -> Unit
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismissRequest,

            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Original Image",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4 / 3f),
                        bitmap = imageBitmap,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "original image"
                    )

                    Text(
                        text = "Cropped Image",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4 / 3f),
                        bitmap = croppedImage,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "cropped image"
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onDismissRequest() }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { onDismissRequest() }) {
                    Text("Dismiss")
                }
            }
        )
    }

}

@Composable
private fun DrawingOverlay(
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