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
import kotlin.math.roundToInt

/**
 * This demo uses checkbox to change properties in one place
 */
@Composable
fun ZoomDemo2() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffECEFF1))
    ) {

        val imageBitmap = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape4
        )

        var contentScale by remember { mutableStateOf(ContentScale.Fit) }

        ContentScaleSelectionMenu(contentScale = contentScale) {
            contentScale = it
        }

        var limitPan by remember { mutableStateOf(true) }
        var zoomable by remember { mutableStateOf(true) }
        var pannable by remember { mutableStateOf(true) }
        var rotatable by remember { mutableStateOf(true) }
        var clip by remember { mutableStateOf(true) }
        var clipTransformToContentScale by remember { mutableStateOf(true) }
        var consume by remember { mutableStateOf(true) }

        CheckBoxWithTitle(
            label = "Limit Pan(Rotation should be disabled)",
            state = limitPan,
            onStateChange = { limitPan = it })
        CheckBoxWithTitle(label = "Zoomable", state = zoomable, onStateChange = { zoomable = it })
        CheckBoxWithTitle(label = "Pannable", state = pannable, onStateChange = { pannable = it })
        CheckBoxWithTitle(
            label = "Rotatable",
            state = rotatable,
            onStateChange = { rotatable = it })
        CheckBoxWithTitle(label = "clip(Limit pan forces clip)", state = clip, onStateChange = { clip = it })
        CheckBoxWithTitle(
            label = "clipTransformToContentScale",
            state = clipTransformToContentScale,
            onStateChange = { clipTransformToContentScale = it }
        )
        CheckBoxWithTitle(label = "consume", state = consume, onStateChange = { consume = it })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ZoomableImageDemo(
                imageBitmap = imageBitmap,
                contentScale = contentScale,
                limitPan = limitPan,
                zoomable = zoomable,
                pannable = pannable,
                rotatable = rotatable,
                clip = clip,
                clipTransformToContentScale = clipTransformToContentScale,
                consume = consume
            )

            ZoomModifierDemo(
                imageBitmap = imageBitmap,
                contentScale = contentScale,
                limitPan = limitPan,
                zoomable = zoomable,
                pannable = pannable,
                rotatable = rotatable,
                clip = clip,
                clipTransformToContentScale = clipTransformToContentScale,
                consume = consume,
            )
        }
    }
}

@Composable
private fun ZoomableImageDemo(
    imageBitmap: ImageBitmap,
    contentScale: ContentScale,
    limitPan: Boolean,
    zoomable: Boolean,
    pannable: Boolean,
    rotatable: Boolean,
    clip: Boolean,
    clipTransformToContentScale: Boolean,
    consume: Boolean
) {
    Text(
        text = "ZoomableImage",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    TitleMedium(text = "Parameter version")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        limitPan = limitPan,
        zoomable = zoomable,
        pannable = pannable,
        rotatable = rotatable,
        clip = clip,
        clipTransformToContentScale = clipTransformToContentScale,
        consume = consume
    )

    Spacer(modifier = Modifier.height(20.dp))
    TitleMedium(text = "rememberZoomState version")

    ZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        zoomState = rememberZoomState(
            limitPan = limitPan,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            keys = arrayOf(
                limitPan,
                zoomable,
                pannable,
                rotatable,
                clip,
                clipTransformToContentScale
            )
        ),
        clip = clip,
        clipTransformToContentScale = clipTransformToContentScale,
        consume = consume
    )

    Spacer(modifier = Modifier.height(20.dp))
    TitleMedium(text = "gesture callbacks")

    var text by remember {
        mutableStateOf(
            "Use pinch or fling gesture\n" +
                    "to observe data"
        )
    }

    Image(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f)
            .zoom(
                zoomState = rememberZoomState(
                    limitPan = false,
                    rotatable = true,
                    keys = arrayOf(
                        limitPan,
                        zoomable,
                        pannable,
                        rotatable,
                        clip,
                        clipTransformToContentScale
                    )
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
        bitmap = imageBitmap,
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )

    Text(text)
}

@Composable
private fun ZoomModifierDemo(
    imageBitmap: ImageBitmap,
    contentScale: ContentScale,
    limitPan: Boolean,
    zoomable: Boolean,
    pannable: Boolean,
    rotatable: Boolean,
    clip: Boolean,
    clipTransformToContentScale: Boolean,
    consume: Boolean
) {
    Column(
        modifier = Modifier
    ) {

        val keys = arrayOf(
            contentScale,
            limitPan,
            zoomable,
            pannable,
            rotatable,
            clip,
            clipTransformToContentScale
        )

        Text(
            text = "Zoom Modifier",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        TitleMedium(text = "Modifier.zoom() applied to Image")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .zoom(
                    keys = keys,
                    clip = clip,
                    consume = consume,
                    zoomState = rememberZoomState(
                        limitPan = limitPan,
                        zoomable = zoomable,
                        pannable = pannable,
                        rotatable = rotatable,
                        keys = keys
                    ),
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = contentScale
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = "Modifier.zoom applied to Canvas")
        DrawPolygonPath(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp, clip = false)
                .background(Color.White)
                .fillMaxWidth()
                .height(200.dp)
                .zoom(
                    keys = keys,
                    clip = clip,
                    consume = consume,
                    zoomState = rememberZoomState(
                        limitPan = limitPan,
                        zoomable = zoomable,
                        pannable = pannable,
                        rotatable = rotatable,
                        keys = keys
                    ),
                )
        )
    }
}

@Composable
private fun DrawPolygonPath(modifier: Modifier) {
    var sides by remember { mutableStateOf(6f) }
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

