package com.smarttoolfactory.composeimage.demo

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.ContentScaleSelectionMenu
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.zoom.ZoomableImage

@Composable
fun ZoomableImageDemo() {

    Column(modifier = Modifier.fillMaxSize()) {
        val imageBitmapLarge = ImageBitmap.imageResource(
            LocalContext.current.resources,
            R.drawable.landscape4
        )

        var contentScale by remember { mutableStateOf(ContentScale.Fit) }

        ContentScaleSelectionMenu(contentScale = contentScale) {
            contentScale = it
        }

        Text(
            text = "clipTransformToContentScale false",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        ZoomableImage(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            imageBitmap = imageBitmapLarge,
            contentScale = contentScale,
            clipTransformToContentScale = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "clipTransformToContentScale true",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        ZoomableImage(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            imageBitmap = imageBitmapLarge,
            contentScale = contentScale,
            clipTransformToContentScale = true
        )
    }
}