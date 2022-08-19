package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.smarttoolfactory.image.zoom.ZoomLevel
import com.smarttoolfactory.image.zoom.enhancedZoom
import com.smarttoolfactory.image.zoom.rememberEnhancedZoomState

@Composable
fun EnhancedZoomDemo() {

    println("⛺️ EnhancedZoomDemo")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xffECEFF1))
    ) {

        val imageBitmap = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape1
        )

        var contentScale by remember { mutableStateOf(ContentScale.Fit) }

        Spacer(modifier = Modifier.height(40.dp))
        ContentScaleSelectionMenu(contentScale = contentScale) {
            contentScale = it
        }

        EnhancedZoomableImageSample(imageBitmap = imageBitmap, contentScale)
        EnhancedZoomModifierSample(imageBitmap = imageBitmap)
    }
}

@Composable
private fun EnhancedZoomableImageSample(imageBitmap: ImageBitmap, contentScale: ContentScale) {

    TitleMedium(text = "EnhancedZoomableImage")

    TitleMedium(
        text = "clip = true\n" +
                "limitPan = false\n" +
                "moveToBounds = true\n" +
                "clipTransformToContentScale = true"
    )

    EnhancedZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        limitPan = false,
        moveToBounds = true,
        clipTransformToContentScale = true,
        enabled = { zoom, pan, rotation ->
            (zoom > 1.2f)
        }
    )

    Spacer(modifier = Modifier.height(40.dp))

    TitleMedium(
        text = "clip = true\n" +
                "limitPan = false\n" +
                "moveToBounds = true\n" +
                "fling = true"
    )
    EnhancedZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        limitPan = false,
        moveToBounds = true,
        fling = true,
        enabled = { zoom, pan, rotation ->
            (zoom > 1.2f)
        }
    )


    TitleMedium(
        text = "clip = false\n" +
                "limitPan = false\n" +
                "rotatable = true\n" +
                "moveToBounds = false\n" +
                "fling = true"
    )
    EnhancedZoomableImage(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        clip = false,
        limitPan = false,
        rotatable = true,
        moveToBounds = false,
        fling = true,
        enabled = { zoom, pan, rotation ->
            (zoom > 1.2f)
        }
    )

    Spacer(modifier = Modifier.height(40.dp))
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

        TitleMedium(
            text = "clip = true\n" +
                    "limitPan = false\n" +
                    "moveToBounds = true"
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
                        maxZoom = 10f,
                        imageSize = IntSize(width, height),
                        limitPan = false,
                        moveToBounds = true
                    ),
                    zoomOnDoubleTap = { zoomLevel: ZoomLevel ->
                        when (zoomLevel) {
                            ZoomLevel.Mid -> 3f
                            ZoomLevel.Min -> 1f
                            ZoomLevel.Max -> 5f
                        }
                    },
                    enabled = { zoom, pan, rotation ->
                        zoom > 1.2f
                    }
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(
            text = "clip = true\n" +
                    "limitPan = true\n" +
                    "moveToBounds = true"
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
                        moveToBounds = true
                    ),
                    enabled = { zoom, pan, rotation ->
                        (zoom > 1.2f)
                    }
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(
            text = "clip = true\n" +
                    "limitPan = true\n" +
                    "moveToBounds = true\n" +
                    "fling = true"
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
                        moveToBounds = true,
                        fling = true
                    ),
                    enabled = { zoom, pan, rotation ->
                        (zoom > 1.2f)
                    }
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(
            text = "clip = true\n" +
                    "rotate = true\n" +
                    "moveToBounds = false\n" +
                    "fling = true"
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
                        limitPan = true,
                        rotatable = true,
                        moveToBounds = false,
                        fling = true
                    ),
                    enabled = { zoom, pan, rotation ->
                        (zoom > 1.2f)
                    }
                ),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    }
}


