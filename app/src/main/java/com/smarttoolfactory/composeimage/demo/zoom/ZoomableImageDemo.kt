package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.ContentScaleSelectionMenu
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.composeimage.TitleMedium
import com.smarttoolfactory.image.zoom.ZoomableImage
import com.smarttoolfactory.image.zoom.rememberZoomState
import com.smarttoolfactory.image.zoom.zoom
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ZoomDemo() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xffECEFF1))
    ) {

        var contentScale by remember { mutableStateOf(ContentScale.Fit) }

        ContentScaleSelectionMenu(contentScale = contentScale) {
            contentScale = it
        }

        ZoomableImageDemo(contentScale)
        ZoomModifierDemo()
    }
}

@Composable
private fun ZoomableImageDemo(contentScale: ContentScale) {
    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape4
    )

    Text(
        text = "ZoomableImage",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    TitleMedium(text = "clipTransformToContentScale false")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmapLarge,
        contentScale = contentScale,
        clipTransformToContentScale = false
    )

    Spacer(modifier = Modifier.height(40.dp))

    TitleMedium(text = "clipTransformToContentScale = true")
    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmapLarge,
        contentScale = contentScale,
        clipTransformToContentScale = true
    )

    Spacer(modifier = Modifier.height(40.dp))
    TitleMedium(text = "clip = false\n" +
            "limitPan = false")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmapLarge,
        contentScale = contentScale,
        clip = false,
        limitPan = false

    )

    Spacer(modifier = Modifier.height(40.dp))
    TitleMedium(text = "rotatable = true")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmapLarge,
        contentScale = contentScale,
        clipTransformToContentScale = true,
        rotatable = true
    )

    Spacer(modifier = Modifier.height(40.dp))


    TitleMedium(text = "gesture callbacks")

    var text by remember { mutableStateOf("Use pinch or fling gesture\n" +
            "to observe data") }

    Image(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
            .zoom(
                zoomState = rememberZoomState(
                    limitPan = false,
                    rotatable = true
                ),
                clip = true,
                consume = true,
                onGestureStart = {
                    text = "onGestureStart data: $it"
                },
                onGesture = {
                    text = "onGesture data: $it"


                },
                onGestureEnd = {
                    text = "onGestureEnd data: $it"
                }
            ),
        bitmap = imageBitmapLarge,
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )

    Text(text)
}

@Composable
private fun ZoomModifierDemo() {
    Column(
        modifier = Modifier
    ) {

        val imageBitmapLarge = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape5
        )

        Text(
            text = "Zoom Modifier",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        TitleMedium(text = "Modifier.zoom(clip = true, limitPan = false)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(limitPan = false),
                ),
            bitmap = imageBitmapLarge,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = true, limitPan = true)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(limitPan = true),
                ),
            bitmap = imageBitmapLarge,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = true, rotate = true)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(rotatable = true),

                    ),
            bitmap = imageBitmapLarge,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = false, rotate = true)")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    clip = false,
                    zoomState = rememberZoomState(rotatable = true),
                ),
            bitmap = imageBitmapLarge,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = true, limitPan = false)")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    clip = true,
                    zoomState = rememberZoomState(limitPan = false),
                )
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = false, limitPan = false)")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp, clip = false)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    clip = false,
                    zoomState = rememberZoomState(limitPan = false),
                )
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom(clip = false, limitPan = true)")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp, clip = false)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    clip = false,
                    zoomState = rememberZoomState(
                        limitPan = true
                    )
                )
        )
    }
}

@Composable
private fun DrawPolygonPath(modifier: Modifier) {
    var sides by remember { mutableStateOf(3f) }
    var cornerRadius by remember { mutableStateOf(1f) }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2
        val path = createPolygonPath(cx, cy, sides.roundToInt(), radius)

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(cornerRadius)
            )
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        androidx.compose.material.Text(text = "Sides ${sides.roundToInt()}")
        Slider(
            value = sides,
            onValueChange = { sides = it },
            valueRange = 3f..12f,
            steps = 10
        )

        androidx.compose.material.Text(text = "CornerRadius ${cornerRadius.roundToInt()}")

        Slider(
            value = cornerRadius,
            onValueChange = { cornerRadius = it },
            valueRange = 0f..50f,
        )
    }
}


fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {
    val angle = 2.0 * Math.PI / sides

    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}
