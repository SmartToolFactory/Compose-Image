package com.smarttoolfactory.composeimage.demo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.smarttoolfactory.composeimage.ImageSelectionButton
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.ImageWithConstraints

/**
 * This demo is for comparing results with [Image] and [ImageWithConstraints]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageWithConstraintsDemo() {

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
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                ImageScale(imageBitmap = imageBitmap)
            }
        }
    )
}

@Composable
fun ImageScale(imageBitmap: ImageBitmap) {

    val modifier = Modifier
        .background(Color.LightGray)
        .border(2.dp, Color.Red)
        .fillMaxWidth()
        .aspectRatio(4 / 3f)

    var text by remember { mutableStateOf("") }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "ImageWithConstraints ContentScale",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )

    var contentScale by remember { mutableStateOf(ContentScale.Fit) }
    ContentScaleSelectionMenu(contentScale = contentScale) {
        contentScale = it
    }

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

    Text(
        text = "Image Content Scale",
        fontSize = 20.sp,
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
