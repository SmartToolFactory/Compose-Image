package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.ImageWithConstraintsDemo
import com.smarttoolfactory.composeimage.demo.ThumbnailDemo

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Image Constraints" to { ImageWithConstraintsDemo() },
            "Image Thumbnail" to { ThumbnailDemo() }
        )
    )
}