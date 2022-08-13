package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.zoom.AnimatedZoomLayout2

@Composable
fun AnimatedZoomDemo() {

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedZoomLayout2(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Green)
        ) {
            Text(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Yellow),
                text = "Hello World........\n" +
                        "asdamsdalşsdkasşl\n" +
                        "asdasdaskdk\n" +
                " asads dasdasdasd asdasdasd",
            )
        }

//        AnimatedZoomLayout2(
//            modifier = Modifier
//                .fillMaxWidth()
//
//                .border(2.dp, Color.Green)
//        ) {
//            Text(
//                modifier = Modifier
//                    .size(100.dp)
//                    .background(Color.Yellow),
//                text = "Hello World",
//            )
//        }
    }
}