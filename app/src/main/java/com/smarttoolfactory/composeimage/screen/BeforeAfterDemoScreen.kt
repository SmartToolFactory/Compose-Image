package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterImageDemo
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterLayoutDemo

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BeforeAfterDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Before/After Image" to { BeforeAfterImageDemo() },
            "Before/After Layout" to { BeforeAfterLayoutDemo() }
        )
    )
}