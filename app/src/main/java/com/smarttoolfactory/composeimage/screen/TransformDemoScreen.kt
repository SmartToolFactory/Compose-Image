package com.smarttoolfactory.composeimage.screen

import androidx.compose.runtime.Composable
import com.smarttoolfactory.composeimage.PagerContent
import com.smarttoolfactory.composeimage.demo.transform.EditScaleDemo
import com.smarttoolfactory.composeimage.demo.transform.EditSizeDemo

@Composable
fun TransformDemoScreen() {
    PagerContent(
        content = mapOf<String, @Composable () -> Unit>(
            "Editable Scale" to { EditScaleDemo() },
            "Editable Size" to { EditSizeDemo() }
        )
    )
}
