package com.smarttoolfactory.composeimage.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.ContentScaleSelectionMenu
import com.smarttoolfactory.composeimage.ImageSelectionButton
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThumbnailDemo() {

    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape4
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

                ThumbnailDemoSamples(imageBitmap)
            }
        }
    )
}

@Composable
private fun ThumbnailDemoSamples(imageBitmap: ImageBitmap) {

    val modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .aspectRatio(4 / 3f)

    var contentScale by remember { mutableStateOf(ContentScale.Fit) }
    ContentScaleSelectionMenu(contentScale = contentScale) {
        contentScale = it
    }

    Spacer(modifier = Modifier.height(20.dp))
    ThumbnailScaleModeCustomImageSample(modifier, imageBitmap, contentScale)
    ThumbnailCallbackSample(modifier, imageBitmap, contentScale)
    ThumbnailPositionChangeSample(modifier)
    ThumbnailUIPropertiesSample(modifier)
}

@Composable
private fun ThumbnailScaleModeCustomImageSample(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale
) {

    ExpandableColumnWithTitle(
        title = "Custom Image",
        color = MaterialTheme.colorScheme.primary,
        initialExpandState = true
    ) {

        Text(
            "Open an image using FloatingActionButton or change ContentScale " +
                    "using dropdown menu."
        )

        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            contentScale = contentScale,
            thumbnailState = rememberThumbnailState(
                shadow = MaterialShadow(8.dp, spotColor = Color.LightGray),
                border = Border(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.LightGray,
                            Color.White
                        )
                    ), 2.dp
                )
            ),
            contentDescription = null
        ) {
            Box(
                modifier = Modifier.size(imageWidth, imageHeight)
            )
        }
    }
}

@Composable
private fun ThumbnailCallbackSample(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale
) {

    var center by remember { mutableStateOf(Offset.Unspecified) }
    var offset by remember { mutableStateOf(Offset.Unspecified) }

    ExpandableColumnWithTitle(
        title = "Callbacks",
        color = MaterialTheme.colorScheme.primary,
        initialExpandState = true
    ) {
        Text(
            "Canvas is added as content to Thumbnail to get center of thumbnail and " +
                    "user's touch position with exact linear interpolation for any scaling mode of ScalableImage"
        )

        Text("Offset: $offset")

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(Color.LightGray)

        ) {

            ImageWithThumbnail(
                imageBitmap = imageBitmap,
                modifier = modifier,
                contentDescription = null,
                contentScale = contentScale,
                onThumbnailCenterChange = {
                    center = it
                },
                onMove = {
                    offset = it
                }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.Yellow)
                ) {

                    if (center.isSpecified && center.isFinite) {
                        drawCircle(Color.Red, radius = 5.dp.toPx(), center = center)
                    }
                    if (offset.isSpecified && offset.isFinite) {
                        drawCircle(Color.Green, radius = 5.dp.toPx(), center = offset)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThumbnailPositionChangeSample(
    modifier: Modifier
) {

    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape5
    )

    ExpandableColumnWithTitle(
        title = "Thumbnail Position",
        color = MaterialTheme.colorScheme.primary,
        initialExpandState = false
    ) {
        Text(
            "Change position of thumbnail from first one to second based on " +
                    "touch proximity to Thumbnail"
        )
        Text(text = "TopLeft-TopRight")
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "BottomRight-TopLeft")
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            thumbnailState = rememberThumbnailState(
                position = ThumbnailPosition.BottomRight,
                moveTo = ThumbnailPosition.TopLeft
            ),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "TopRight-BottomLeft")
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            thumbnailState = rememberThumbnailState(
                position = ThumbnailPosition.TopRight,
                moveTo = ThumbnailPosition.BottomLeft
            ),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "TopLeft not movable")
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            contentDescription = null,
            thumbnailState = rememberThumbnailState(dynamicPosition = false)
        )
    }
}

@Composable
private fun ThumbnailUIPropertiesSample(
    modifier: Modifier
) {

    val imageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape4
    )

    ExpandableColumnWithTitle(
        title = "Thumbnail Shape",
        color = MaterialTheme.colorScheme.primary,
        initialExpandState = false
    ) {
        Text(
            "Change shape of thumbnail"
        )
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            contentDescription = null,
            thumbnailState = rememberThumbnailState(
                shape = CutCornerShape(topEndPercent = 25)
            )
        )
        Spacer(modifier = Modifier.height(30.dp))

        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            contentDescription = null,
            thumbnailState = rememberThumbnailState(
                shape = CircleShape
            )
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Change border")
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            thumbnailState = rememberThumbnailState(
                border = Border(color = Color.Red, strokeWidth = 3.dp)
            ),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Change shadow")
        ImageWithThumbnail(
            imageBitmap = imageBitmap,
            modifier = modifier,
            thumbnailState = rememberThumbnailState(
                size = DpSize(100.dp, 90.dp),
                        shadow = MaterialShadow (
                        8.dp,
                spotColor = Color.Red,
                ambientShadowColor = Color.Yellow,
            )
        ),
        contentDescription = null
        )
    }
}

/**
 * Column with full width title and expand icon that can expand/shrink with [AnimatedVisibility].
 * @param title text on top of the column that is visible on both states.
 * @param color of [title].
 * @param initialExpandState whether this composable should be expanded initially.
 * @param content is the content that should be expended or hidden.
 */
@Composable
private fun ExpandableColumnWithTitle(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    title: String,
    color: Color,
    initialExpandState: Boolean = true,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initialExpandState) }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {

        Row(
            modifier = Modifier
                .padding(5.dp)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = title,
                fontSize = 22.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess
                else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = color
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                content()
            }
        }
    }
}