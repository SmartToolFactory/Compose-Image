package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.DimensionSubcomposeLayout

@Composable
fun AnimatedZoomLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current

    DimensionSubcomposeLayout(
        placeMainContent = false,
        mainContent = { content() }
    ) {
        Box(
            modifier
                .border(5.dp, Color.Red)
                .animatedZoom(
                    animatedZoomState = rememberAnimatedZoomState(
                        minZoom = .5f,
                        maxZoom = 30f
                    ),
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun AnimatedZoomLayout2(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Box(
        modifier
            .border(4.dp, Color.Red)
            .animatedZoom(
                animatedZoomState = rememberAnimatedZoomState(
                    minZoom = .5f,
                    maxZoom = 30f,
                    contentSize = DpSize(200.dp, 200.dp)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }

}