package com.smarttoolfactory.composeimage.demo

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.beforeafter.BeforeAfterImage
import com.smarttoolfactory.image.beforeafter.ImageOrder
import kotlin.math.roundToInt

@Composable
fun BeforeAfterImageDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val imageBefore = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.image_before_after_shoes_a
        )

        val imageAfter = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.image_before_after_shoes_b
        )

        val imageBefore2 = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.image_before_after_elements_a
        )

        val imageAfter2 = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.image_before_after_elements_b
        )

        val imageBefore3 = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.landscape5_before
        )

        val imageAfter3 = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.landscape5
        )

        Text(
            text = "BeforeAfterImage",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "Order",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        Box {
            BeforeAfterImage(
                modifier = Modifier
                    .shadow(1.dp, RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                beforeImage = imageBefore,
                afterImage = imageAfter,
                contentScale = ContentScale.FillBounds
            )
            Label(
                text = "BEFORE",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            )
            Label(
                text = "AFTER", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box {
            BeforeAfterImage(
                modifier = Modifier
                    .shadow(1.dp, RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                beforeImage = imageBefore,
                afterImage = imageAfter,
                imageOrder = ImageOrder.AfterBefore,
                contentScale = ContentScale.FillBounds
            )
            Label(
                text = "AFTER",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            )
            Label(
                text = "BEFORE", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Zoom(Pinch gesture)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        Box {
            BeforeAfterImage(
                modifier = Modifier
                    .shadow(1.dp, RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                beforeImage = imageBefore3,
                afterImage = imageAfter3,
                imageOrder = ImageOrder.AfterBefore,
                contentScale = ContentScale.FillBounds
            )
            Label(
                text = "AFTER",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            )
            Label(
                text = "BEFORE", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            )
        }


        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Progress animation",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        val transition: InfiniteTransition = rememberInfiniteTransition()

        // Infinite progress animation
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        BeforeAfterImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(3.dp, Color(0xffE91E63), RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            beforeImage = imageBefore2,
            afterImage = imageAfter2,
            progress = progress,
            onProgressChange = {},
            contentScale = ContentScale.FillBounds
        ) {
            Text(
                "${(progress).roundToInt()}%",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff03A9F4)
            )
        }
    }
}

@Composable
private fun Label(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(Color.Black.copy(alpha = .4f), RoundedCornerShape(50))
            .padding(8.dp)
    )
}