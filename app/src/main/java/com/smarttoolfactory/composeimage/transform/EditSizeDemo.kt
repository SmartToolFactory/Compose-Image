package com.smarttoolfactory.composeimage.transform

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.composeimage.R
import com.smarttoolfactory.image.transform.HandlePlacement
import com.smarttoolfactory.image.transform.MorphLayout

@Composable
fun EditSizeDemo() {
    Column(
        modifier = Modifier
            .background(Color(0xff424242))
            .fillMaxSize()
            .padding(8.dp)
    ) {

        var enabled by remember { mutableStateOf(true) }

        Spacer(modifier = Modifier.height(40.dp))

        var zIndex1 by remember {
            mutableStateOf(0f)
        }

        var zIndex2 by remember {
            mutableStateOf(0f)
        }

        var zIndex3 by remember {
            mutableStateOf(0f)
        }


        val density = LocalDensity.current
        val size = (500 / density.density).dp

        MorphLayout(
            modifier = Modifier.size(size),
            containerModifier = Modifier.zIndex(zIndex1),
            enabled = enabled,
            onDown = {
                zIndex1 = 1f
            },
            onUp = {
                zIndex1 = 0f
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.landscape1),
                contentScale = ContentScale.FillBounds,
                contentDescription = "",
            )
        }

        MorphLayout(
            containerModifier = Modifier.zIndex(zIndex2),
            handleRadius = 20.dp,
            enabled = enabled,
            handlePlacement = HandlePlacement.Side,
            onDown = {
                zIndex2 = 1f
            },
            onUp = {
                zIndex2 = 0f
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.landscape2),
                contentScale = ContentScale.FillBounds,
                contentDescription = ""
            )
        }

        MorphLayout(
            modifier = Modifier.size(width = 300.dp, height = 200.dp),
            containerModifier = Modifier.zIndex(zIndex3),
            enabled = enabled,
            handlePlacement = HandlePlacement.Both,
            onDown = {
                zIndex3 = 1f
            },
            onUp = {
                zIndex3 = 0f
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.landscape3),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )
        }

        MorphLayout(
            enabled = enabled
        ) {
            Text(
                text = "Hello World",
                modifier = Modifier
                    .background(
                        Color.Red,
                        RoundedCornerShape(25)
                    )
                    .padding(2.dp),
                fontSize = 20.sp,
                color = Color.White
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { enabled = !enabled }
        ) {
            Text(if (enabled) "Disable" else "Enable")
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}