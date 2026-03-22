package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.zoom.AnimatedZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomCropDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomDemo2
import com.smarttoolfactory.composeimage.demo.zoom.SubsamplingDemo
import com.smarttoolfactory.composeimage.demo.zoom.ZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.ZoomDemo2
import com.smarttoolfactory.composeimage.demo.zoom.ZoomableListDemo

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
            "Subsampling" to { SubsamplingDemo() },
            "Zoomable List" to { ZoomableListDemo() }
        )
    )
}
