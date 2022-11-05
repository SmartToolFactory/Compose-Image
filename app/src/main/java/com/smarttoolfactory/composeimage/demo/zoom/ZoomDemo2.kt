package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ZoomDemo2() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    var contentScale by remember { mutableStateOf(ContentScale.Fit) }

    var limitPan by remember { mutableStateOf(true) }
    var zoomable by remember { mutableStateOf(true) }
    var pannable by remember { mutableStateOf(true) }
    var rotatable by remember { mutableStateOf(true) }
    var clip by remember { mutableStateOf(true) }
    var clipTransformToContentScale by remember { mutableStateOf(true) }
    var consume by remember { mutableStateOf(true) }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 20.dp,
            topEnd = 20.dp
        ),
        sheetGesturesEnabled = true,
        sheetContent = {
            SheetContent(
                contentScale = contentScale,
                onContentScaleChange = { contentScale = it },
                limitPan = limitPan,
                onLimitPanChange = { limitPan = it },
                zoomable = zoomable,
                onZoomableChange = { zoomable = it },
                pannable = pannable,
                onPannableChange = { pannable = it },
                rotatable = rotatable,
                onRotatableChange = { rotatable = it },
                clip = clip,
                onClipChange = { clip = it },
                clipTransformToContentScale = clipTransformToContentScale,
                onClipTransformToContentScale = { clipTransformToContentScale = it },
                consume = consume,
                onConsumeChange = { consume = it }
            )
        },
        drawerGesturesEnabled = true,
        drawerScrimColor = Color.Red,
        // This is the height in collapsed state
        sheetPeekHeight = 60.dp,

        ) {
        MainContent(
            contentScale,
            limitPan,
            zoomable,
            pannable,
            rotatable,
            clip,
            clipTransformToContentScale,
            consume
        )
    }
}

@Composable
private fun SheetContent(
    contentScale: ContentScale,
    onContentScaleChange: (ContentScale) -> Unit,
    limitPan: Boolean,
    onLimitPanChange: (Boolean) -> Unit,
    zoomable: Boolean,
    onZoomableChange: (Boolean) -> Unit,
    pannable: Boolean,
    onPannableChange: (Boolean) -> Unit,
    rotatable: Boolean,
    onRotatableChange: (Boolean) -> Unit,
    clip: Boolean,
    onClipChange: (Boolean) -> Unit,
    clipTransformToContentScale: Boolean,
    onClipTransformToContentScale: (Boolean) -> Unit,
    consume: Boolean,
    onConsumeChange: (Boolean) -> Unit,

    ) {

    Spacer(modifier = Modifier.height(20.dp))
    TitleMedium(text = "Change Properties")

    CheckBoxWithTitle(
        label = "Limit Pan(Rotation should be disabled)",
        state = limitPan,
        onStateChange = onLimitPanChange
    )
    CheckBoxWithTitle(label = "Zoomable", state = zoomable, onStateChange = onZoomableChange)
    CheckBoxWithTitle(label = "Pannable", state = pannable, onStateChange = onPannableChange)
    CheckBoxWithTitle(
        label = "Rotatable",
        state = rotatable,
        onStateChange = onRotatableChange
    )
    CheckBoxWithTitle(
        label = "clip(Limit pan forces clip)",
        state = clip,
        onStateChange = onClipChange
    )
    CheckBoxWithTitle(
        label = "clipTransformToContentScale",
        state = clipTransformToContentScale,
        onStateChange = onClipTransformToContentScale
    )
    CheckBoxWithTitle(label = "consume", state = consume, onStateChange = onConsumeChange)

    ContentScaleSelectionMenu(contentScale = contentScale) {
        onContentScaleChange(contentScale)
    }

}

@Composable
private fun MainContent(
    contentScale: ContentScale,
    limitPan: Boolean,
    zoomable: Boolean,
    pannable: Boolean,
    rotatable: Boolean,
    clip: Boolean,
    clipTransformToContentScale: Boolean,
    consume: Boolean
) {
    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape4
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
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
                    keys = keys,
                    zoomState = rememberZoomState(
                        limitPan = limitPan,
                        rotatable = rotatable,
                        keys = keys
                    ),
                    clip = clip,
                    consume = consume,
                    onGestureStart = {
                        text = "onGestureStart()\n$it"
                    },
                    onGesture = {
                        text = "onGesture()\n$it"
                    },
                    onGestureEnd = {
                        text = "onGestureEnd()\n$it"
                    }
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = contentScale
        )

        Text(text)
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

