package com.smarttoolfactory.composeimage.demo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.beforeafter.BeforeAfterImage
import com.smarttoolfactory.image.beforeafter.BeforeAfterLayout

@Composable
fun BeforeAfterImageDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val imageBefore = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.landscape5
        )

        val imageAfter = ImageBitmap.imageResource(
            LocalContext.current.resources, R.drawable.landscape5_after
        )

        val painter: Painter = painterResource(id = R.drawable.baseline_swap_horiz_24)

        val density = LocalDensity.current

        BeforeAfterImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
//                .background(Color.LightGray, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            beforeImage = imageBefore,
            afterImage = imageAfter,
            contentScale = ContentScale.FillBounds
        ) {

            val handlePosition = touchPosition.x
            val posY: Int

            val realPos = handlePosition - with(density) {
                posY = (imageHeight / 3).roundToPx()
                imageWidth.toPx() / 2
            }


            Canvas(modifier = Modifier.size(imageWidth, imageHeight)) {

                val canvasWidth = size.width

                val imagePosition = handlePosition.coerceIn(0f, canvasWidth)

                drawLine(
                    Color.White,
                    strokeWidth = 1.5.dp.toPx(),
                    start = Offset(imagePosition, 0f),
                    end = Offset(imagePosition, size.height)
                )

            }

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .offset {
                        IntOffset(realPos.toInt(), posY)
                    }
                    .shadow(2.dp, CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        BeforeAfterLayout(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
            before = {
                Image(
                    painter = painterResource(id = R.drawable.landscape5),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )


            },
            after = {
                Image(
                    painter = painterResource(id = R.drawable.landscape5_after),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }
        )


        Spacer(modifier = Modifier.height(20.dp))

        BeforeAfterLayout(modifier = Modifier,
            before = {
                Text(text = "Hello World", fontSize = 60.sp, color = Color.Red)

            },
            after = {
                Text(text = "Hello World", fontSize = 60.sp, color = Color.Green)
            }
        )
    }
}