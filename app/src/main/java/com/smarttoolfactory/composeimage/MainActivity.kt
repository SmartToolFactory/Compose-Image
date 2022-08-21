@file:OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)

package com.smarttoolfactory.composeimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.smarttoolfactory.composeimage.demo.ImageWithConstraintsDemo
import com.smarttoolfactory.composeimage.demo.ThumbnailDemo
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterImageDemo
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterLayoutDemo
import com.smarttoolfactory.composeimage.demo.transform.EditScaleDemo
import com.smarttoolfactory.composeimage.demo.transform.EditSizeDemo
import com.smarttoolfactory.composeimage.demo.zoom.AnimatedZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomCropDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.ZoomDemo
import com.smarttoolfactory.composeimage.ui.theme.ComposeImageTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeImageTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Uncomment to display demos on pager in one screen
//                    HomeContent()
                    DemoNavGraph()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPagerApi
@Composable
private fun HomeContent() {

    val pagerState: PagerState = rememberPagerState(initialPage = 0)

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage,
                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
                indicator = { tabPositions: List<TabPosition> ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[pagerState.currentPage]
                        ),
                        height = 4.dp
                    )
                },
                edgePadding = 4.dp
            ) {
                // Add tabs for all of our pages
                tabList.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) {

        HorizontalPager(
            modifier = Modifier.padding(it),
            state = pagerState,
            count = tabList.size
        ) { page: Int ->

            when (page) {
                0 -> ImageWithConstraintsDemo()
                1 -> ThumbnailDemo()
                2 -> ZoomDemo()
                3 -> EnhancedZoomDemo()
                4 -> EnhancedZoomCropDemo()
                5 -> AnimatedZoomDemo()
                6 -> BeforeAfterImageDemo()
                7 -> BeforeAfterLayoutDemo()
                8 -> EditScaleDemo()
                else -> EditSizeDemo()
            }
        }
    }
}


internal val tabList =
    listOf(
        "Image Constraints",
        "Image Thumbnail",
        "Zoom",
        "Enhanced Zoom",
        "Enhanced Zoom Crop",
        "Animated Zoom",
        "Before/After Image",
        "Before/After Layout",
        "Editable Scale",
        "Editable Size",
    )