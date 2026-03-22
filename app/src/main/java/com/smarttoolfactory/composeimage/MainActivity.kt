@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.composeimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composeimage.demo.ImageWithConstraintsDemo
import com.smarttoolfactory.composeimage.demo.ThumbnailDemo
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterImageDemo
import com.smarttoolfactory.composeimage.demo.beforeafter.BeforeAfterLayoutDemo
import com.smarttoolfactory.composeimage.demo.transform.EditScaleDemo
import com.smarttoolfactory.composeimage.demo.transform.EditSizeDemo
import com.smarttoolfactory.composeimage.demo.zoom.AnimatedZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomCropDemo
import com.smarttoolfactory.composeimage.demo.zoom.EnhancedZoomDemo
import com.smarttoolfactory.composeimage.demo.zoom.SubsamplingDemo
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
@Composable
private fun HomeContent() {

    val pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabList.size }
    )

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
            state = pagerState
        ) { page: Int ->

            when (page) {
                0 -> ImageWithConstraintsDemo()
                1 -> ThumbnailDemo()
                2 -> SubsamplingDemo()
                3 -> ZoomDemo()
                4 -> EnhancedZoomDemo()
                5 -> EnhancedZoomCropDemo()
                6 -> AnimatedZoomDemo()
                7 -> BeforeAfterImageDemo()
                8 -> BeforeAfterLayoutDemo()
                9 -> EditScaleDemo()
                else -> EditSizeDemo()
            }
        }
    }
}


internal val tabList =
    listOf(
        "Image Constraints",
        "Image Thumbnail",
        "Subsampling",
        "Zoom",
        "Enhanced Zoom",
        "Enhanced Zoom Crop",
        "Animated Zoom",
        "Before/After Image",
        "Before/After Layout",
        "Editable Scale",
        "Editable Size",
    )
