package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterImageDemo
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterLayoutDemo

@Composable
fun BeforeAfterDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Before/After Image" to { BeforeAfterImageDemo() },
            "Before/After Layout" to { BeforeAfterLayoutDemo() }
        )
    )
}
