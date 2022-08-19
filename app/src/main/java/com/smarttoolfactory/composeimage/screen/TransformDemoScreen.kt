package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.transform.EditScaleDemo
import com.smarttoolfactory.composeimage.demo.transform.EditSizeDemo

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransformDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Editable Scale" to { EditScaleDemo() },
            "Editable Size" to { EditSizeDemo() }
        )
    )
}