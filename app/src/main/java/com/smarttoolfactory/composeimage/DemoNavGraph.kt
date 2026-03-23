package com.smarttoolfactory.composeimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttoolfactory.composeimage.demo.zoom.CentroidDebugDemo
import com.smarttoolfactory.composeimage.demo.zoom.SubsamplingDemo
import com.smarttoolfactory.composeimage.demo.zoom.ZoomComparisonDemo
import com.smarttoolfactory.composeimage.screen.BeforeAfterDemoScreen
import com.smarttoolfactory.composeimage.screen.DemoSelectionScreen
import com.smarttoolfactory.composeimage.screen.ImageDemoScreen
import com.smarttoolfactory.composeimage.screen.TransformDemoScreen
import com.smarttoolfactory.composeimage.screen.ZoomDemoScreen


@Composable
fun DemoNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.Home,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(route = Destinations.Home) { navBackEntryStack ->
            DemoSelectionScreen { route: String ->
                navController.navigate(route)
            }
        }

        composable(route = Destinations.Image) { navBackEntryStack ->
            ImageDemoScreen()
        }
        composable(route = Destinations.Subsampling) { navBackEntryStack ->
            SubsamplingDemo()
        }
        composable(route = Destinations.CentroidDebug) { navBackEntryStack ->
            CentroidDebugDemo()
        }
        composable(route = Destinations.ZoomComparison) { navBackEntryStack ->
            ZoomComparisonDemo()
        }
        composable(route = Destinations.Zoom) { navBackEntryStack ->
            ZoomDemoScreen()
        }
        composable(route = Destinations.Transform) { navBackEntryStack ->
            TransformDemoScreen()
        }
        composable(route = Destinations.BeforeAfter) { navBackEntryStack ->
            BeforeAfterDemoScreen()
        }
    }
}

object Destinations {
    const val Home = "selection_screen"
    const val Image = "image_screen"
    const val Subsampling = "subsampling_screen"
    const val CentroidDebug = "centroid_debug_screen"
    const val ZoomComparison = "zoom_comparison_screen"
    const val Zoom = "zoom_screen"
    const val Transform = "transform_screen"
    const val BeforeAfter = "before_after_screen"
}
