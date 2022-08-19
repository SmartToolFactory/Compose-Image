package com.smarttoolfactory.composeimage.demo

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.smarttoolfactory.composeimage.ContentScaleSelectionMenu
import com.smarttoolfactory.composeimage.ImageSelectionButton
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.ImageWithConstraints

/**
 * This demo is for comparing results with [Image] and [ImageWithConstraints]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageWithConstraintsDemo() {

    println("🔥 ImageWithConstraintsDemo")

    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources, R.drawable.landscape2
    )

    var imageBitmap by remember { mutableStateOf(imageBitmapLarge) }

    Scaffold(
        floatingActionButton = {
            ImageSelectionButton {
                imageBitmap = it
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                ImageScale(imageBitmap = imageBitmap)
            }
        }
    )
}

@Composable
fun ImageScale(imageBitmap: ImageBitmap) {

    var contentScale by remember { mutableStateOf(ContentScale.Fit) }
    ContentScaleSelectionMenu(contentScale = contentScale) {
        contentScale = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val modifier = Modifier
            .background(Color.LightGray)
            .border(2.dp, Color.Red)
            .fillMaxWidth()
            .aspectRatio(4 / 3f)


        ImageWithConstraintsSample(modifier, imageBitmap, contentScale)
        Spacer(modifier = Modifier.height(30.dp))
        DrawSample(modifier, imageBitmap, contentScale)
        Spacer(modifier = Modifier.height(30.dp))
        CropSample(modifier, imageBitmap, contentScale)
        Spacer(modifier = Modifier.height(30.dp))
        ImageSample(modifier, imageBitmap, contentScale)
    }

}

@Composable
private fun ImageWithConstraintsSample(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale
) {

    var text by remember { mutableStateOf("") }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "ImageWithConstraints ContentScale",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    ImageWithConstraints(
        modifier = modifier,
        imageBitmap = imageBitmap,
        contentDescription = null,
        contentScale = contentScale
    ) {

        val imageWidth = this.imageWidth.value.toInt()
        val imageHeight = this.imageHeight.value.toInt()
        val bitmapRect = this.rect

        text = "Image width: ${imageWidth}dp, height: ${imageHeight}dp\n" +
                "Bitmap Rect: $bitmapRect"

        Spacer(
            modifier = Modifier
                .size(this.imageWidth, this.imageHeight)
                .border(3.dp, Color.Yellow)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        tonalElevation = 4.dp,
        color = Color.LightGray,
        contentColor = Color.White
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun CropSample(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale
) {

    Text(
        text = "Crop Using ImageScope",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    var showDialog by remember { mutableStateOf(false) }

    var bitmapRect by remember(imageBitmap, contentScale) {
        mutableStateOf(
            IntRect(
                offset = IntOffset.Zero,
                size = IntSize(imageBitmap.width, imageBitmap.height)
            )
        )
    }

    val croppedImage = remember(imageBitmap, bitmapRect) {
        Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            bitmapRect.left,
            bitmapRect.top,
            bitmapRect.width,
            bitmapRect.height
        ).asImageBitmap()
    }

    ImageWithConstraints(
        modifier = modifier,
        imageBitmap = imageBitmap,
        contentDescription = null,
        contentScale = contentScale
    ) {
        bitmapRect = this.rect
    }


    Button(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = { showDialog = true }) {
        Text("Crop")
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = !showDialog },

            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                Button(onClick = { showDialog = !showDialog }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = !showDialog }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
private fun DrawSample(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale
) {

    Text(
        text = "Draw Using ImageScope",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    ImageWithConstraints(
        modifier = modifier,
        imageBitmap = imageBitmap,
        contentDescription = null,
        contentScale = contentScale
    ) {

        val imageWidth = this.imageWidth
        val imageHeight = this.imageHeight

        Drawing(modifier = Modifier.size(imageWidth, imageHeight))
    }
}

// This is a Sample to show ImageWithConstraints match with Compose Image content scale changes
@Composable
private fun ImageSample(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale
) {
    Text(
        text = "Image Content Scale",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    Image(
        modifier = modifier,
        bitmap = imageBitmap,
        contentDescription = null,
        contentScale = contentScale
    )
}

@Composable
private fun Drawing(modifier: Modifier) {

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }


    val transition: InfiniteTransition = rememberInfiniteTransition()

    // Infinite phase animation for PathEffect
    val phase by transition.animateFloat(
        initialValue = .9f,
        targetValue = .3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color = Color.Green

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = 15f
            strokeCap = StrokeCap.Round


            this.asFrameworkPaint().apply {
                val transparent = color
                    .copy(alpha = 0f)
                    .toArgb()

                this.color = transparent
            }
        }
    }

    paint.asFrameworkPaint().setShadowLayer(
        30f * phase,
        0f,
        0f,
        color
            .copy(alpha = phase)
            .toArgb()
    )

    // Path is what is used for drawing line on Canvas
    val path = remember(modifier) { Path() }

    val drawModifier = modifier
        .clipToBounds()
        .pointerMotionEvents(
            onDown = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Down
                pointerInputChange.consume()
            },
            onMove = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Move
                pointerInputChange.consume()
            },
            onUp = { pointerInputChange: PointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consume()
            },
            delayAfterDownInMillis = 25L
        )

    Canvas(modifier = drawModifier) {
        when (motionEvent) {
            MotionEvent.Down -> {
                path.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
            }

            MotionEvent.Move -> {
                path.quadraticBezierTo(
                    previousPosition.x,
                    previousPosition.y,
                    (previousPosition.x + currentPosition.x) / 2,
                    (previousPosition.y + currentPosition.y) / 2

                )

                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                path.lineTo(currentPosition.x, currentPosition.y)
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
            }

            else -> Unit
        }

        this.drawIntoCanvas {

            it.drawPath(path, paint)

            drawPath(
                color = Color.White.copy((0.4f + phase).coerceAtMost(1f)),
                path = path,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}
