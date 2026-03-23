package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.composeimage.TitleMedium
import com.smarttoolfactory.image.zoom.ZoomableImage

/**
 * Standalone image-based demo for visually validating centroid-stable zoom behavior.
 */
@Composable
fun CentroidDebugDemo() {
    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape4
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffECEFF1))
    ) {
        TitleMedium(text = "Centroid Debug")
        Text(
            text = "Pinch directly on the image. Cyan locks to the centroid where the pinch started, and magenta shows the live centroid while zooming.",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        PinchCentroidDebugOverlay(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .border(2.dp, Color(0xff37474F))
                .background(Color.LightGray)
        ) {
            ZoomableImage(
                modifier = Modifier.fillMaxSize(),
                imageBitmap = imageBitmap,
                contentScale = ContentScale.FillBounds,
                clip = true,
                limitPan = false
            )
        }
    }
}
