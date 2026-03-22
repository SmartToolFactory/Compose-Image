package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.ImageWithConstraintsDemo
import com.smarttoolfactory.composeimage.demo.ThumbnailDemo

@Composable
fun ImageDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Image Constraints" to { ImageWithConstraintsDemo() },
            "Image Thumbnail" to { ThumbnailDemo() }
        )
    )
}
