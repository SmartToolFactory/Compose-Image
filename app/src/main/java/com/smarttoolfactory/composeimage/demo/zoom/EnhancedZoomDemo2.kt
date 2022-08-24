package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.ContentScaleSelectionMenu
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.composeimage.TitleMedium
import com.smarttoolfactory.image.zoom.EnhancedZoomableImage
import com.smarttoolfactory.image.zoom.enhancedZoom
import com.smarttoolfactory.image.zoom.rememberEnhancedZoomState

/**
 * This demo uses checkbox to change properties in one place
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnhancedZoomDemo2() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    var contentScale by remember { mutableStateOf(ContentScale.Fit) }

    var limitPan by remember { mutableStateOf(true) }
    var zoomable by remember { mutableStateOf(true) }
    var pannable by remember { mutableStateOf(true) }
    var rotatable by remember { mutableStateOf(true) }
    var clip by remember { mutableStateOf(true) }
    var moveToBounds by remember { mutableStateOf(true) }
    var fling by remember { mutableStateOf(true) }
    var clipTransformToContentScale by remember { mutableStateOf(true) }

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
                moveToBounds = moveToBounds,
                onMoveToBoundsChange = { moveToBounds = it },
                fling = fling,
                onFlingChange = { fling = it },
                clip = clip,
                onClipChange = { clip = it },
                clipTransformToContentScale = clipTransformToContentScale,
                onClipTransformToContentScale = { clipTransformToContentScale = it },
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
            moveToBounds,
            fling,
            clip,
            clipTransformToContentScale
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
    moveToBounds: Boolean,
    onMoveToBoundsChange: (Boolean) -> Unit,
    fling: Boolean,
    onFlingChange: (Boolean) -> Unit,
    clip: Boolean,
    onClipChange: (Boolean) -> Unit,
    clipTransformToContentScale: Boolean,
    onClipTransformToContentScale: (Boolean) -> Unit,
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
        label = "moveToBounds",
        state = moveToBounds,
        onStateChange = onMoveToBoundsChange
    )
    CheckBoxWithTitle(label = "fling", state = fling, onStateChange = onFlingChange)

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
    moveToBounds: Boolean,
    fling: Boolean,
    clip: Boolean,
    clipTransformToContentScale: Boolean
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
        EnhancedZoomableImageDemo(
            imageBitmap = imageBitmap,
            contentScale = contentScale,
            limitPan = limitPan,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            moveToBounds = moveToBounds,
            fling = fling,
            clip = clip,
            clipTransformToContentScale = clipTransformToContentScale,
        )

        EnhancedZoomModifierDemo(
            imageBitmap = imageBitmap,
            contentScale = contentScale,
            limitPan = limitPan,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            moveToBounds = moveToBounds,
            fling = fling,
            clip = clip,
            clipTransformToContentScale = clipTransformToContentScale,
        )
    }
}


@Composable
private fun EnhancedZoomableImageDemo(
    imageBitmap: ImageBitmap,
    contentScale: ContentScale,
    limitPan: Boolean,
    zoomable: Boolean,
    pannable: Boolean,
    rotatable: Boolean,
    moveToBounds: Boolean,
    fling: Boolean,
    clip: Boolean,
    clipTransformToContentScale: Boolean
) {
    Text(
        text = "EnhancedZoomableImage",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    TitleMedium(text = "Parameter version")

    EnhancedZoomableImage(
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
        moveToBounds = moveToBounds,
        fling = fling,
        clip = clip,
        clipTransformToContentScale = clipTransformToContentScale,
    )

    Spacer(modifier = Modifier.height(20.dp))
    TitleMedium(text = "rememberZoomState version")

    EnhancedZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        enhancedZoomState = rememberEnhancedZoomState(
            imageSize = IntSize(imageBitmap.width, imageBitmap.height),
            limitPan = limitPan,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            moveToBounds = moveToBounds,
            fling = fling,
            keys = arrayOf(
                limitPan,
                zoomable,
                pannable,
                rotatable,
                clip,
                clipTransformToContentScale
            )
        ),
        moveToBounds = moveToBounds,
        fling = fling,
        clip = clip,
        clipTransformToContentScale = clipTransformToContentScale,
    )

}

@Composable
private fun EnhancedZoomModifierDemo(
    imageBitmap: ImageBitmap,
    contentScale: ContentScale,
    limitPan: Boolean,
    zoomable: Boolean,
    pannable: Boolean,
    rotatable: Boolean,
    moveToBounds: Boolean,
    fling: Boolean,
    clip: Boolean,
    clipTransformToContentScale: Boolean,
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
            text = "EnhancedZoom Modifier",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        TitleMedium(text = "Modifier.enhancedZoom() applied to Image")
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .enhancedZoom(
                    keys = keys,
                    clip = clip,
                    enhancedZoomState = rememberEnhancedZoomState(
                        imageSize = IntSize(imageBitmap.width, imageBitmap.height),
                        limitPan = limitPan,
                        zoomable = zoomable,
                        pannable = pannable,
                        rotatable = rotatable,
                        moveToBounds = moveToBounds,
                        fling = fling,
                        keys = keys
                    ),
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = contentScale
        )
    }
}
