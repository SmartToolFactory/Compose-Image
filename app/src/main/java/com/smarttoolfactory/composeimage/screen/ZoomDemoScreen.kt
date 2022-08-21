package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.zoom.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ZoomDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Zoom" to { ZoomDemo() },
            "Zoom2" to { ZoomDemo2() },
            "Enhanced Zoom" to { EnhancedZoomDemo() },
            "Enhanced Zoom2" to { EnhancedZoomDemo2() },
            "Enhanced Zoom Crop" to { EnhancedZoomCropDemo() },
            "Animated Zoom" to { AnimatedZoomDemo() },
            "Zoomable List" to { ZoomableListDemo() }
        )
    )
}
