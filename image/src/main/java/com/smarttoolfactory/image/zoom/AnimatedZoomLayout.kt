package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.SlotsEnum


/**
 * Layout that can zoom, rotate, pan its content with fling and moving back to bounds animation.
 */
@Composable
fun AnimatedZoomLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    AnimatedZoomSubcomposeLayout(
        modifier = modifier,
        mainContent = { content() }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .border(5.dp, Color.Red)
                .animatedZoom(
                    animatedZoomState = rememberAnimatedZoomState(
                        minZoom = .5f,
                        maxZoom = 30f,
                        contentSize = it
                    ),
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * SubcomposeLayout for getting dimensions of [mainContent] while laying out with [modifier]
 * Use of this layout is suitable when size of parent doesn't match content
 * and size [mainContent] is required inside [dependentContent] to use [mainContent] size
 * as reference or dimensions for child composables inside [dependentContent].
 *
 */
@Composable
private fun AnimatedZoomSubcomposeLayout(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (DpSize) -> Unit
) {

    val density = LocalDensity.current

    SubcomposeLayout(
        modifier = modifier
    ) { constraints: Constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent)
            .map {
                it.measure(constraints.copy(minWidth = 0, minHeight = 0))
            }

        // Get max width and height of main component
        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.forEach { placeable: Placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val dependentPlaceables: List<Placeable> = subcompose(SlotsEnum.Dependent) {
            val dpSize = density.run { DpSize(maxWidth.toDp(), maxHeight.toDp()) }
            dependentContent(dpSize)
        }
            .map { measurable: Measurable ->
                measurable.measure(constraints)
            }

        layout(constraints.maxWidth, constraints.maxHeight) {


            dependentPlaceables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}
