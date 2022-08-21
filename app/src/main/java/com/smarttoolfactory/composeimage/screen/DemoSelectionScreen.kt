package com.smarttoolfactory.composeimage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.Destinations

@Composable
fun DemoSelectionScreen(onRouteSelected: (String) -> Unit) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        item {
            DemoSelectionCard(
                title = "Image",
                description = "ImageWithConstraints and image with thumbnail",
            ) {
                onRouteSelected(Destinations.Image)
            }
        }

        item {
            DemoSelectionCard(
                title = "Zoom",
                description = "Zoom, Enhanced Zoom, ZoomableImage, EnhancedZoomableImage, AnimatedZoom " +
                        "with different parameters.",
            ) {
                onRouteSelected(Destinations.Zoom)
            }
        }


        item {
            DemoSelectionCard(
                title = "Before/After",
                description = "Before/After Image and Layout that shows progress or difference between " +
                        "two images or Composables with animation and progress",
            ) {
                onRouteSelected(Destinations.BeforeAfter)
            }
        }

        item {
            DemoSelectionCard(
                title = "Transform Scale/Size",
                description = "Transform Scale or Size of a Composable using handles on corners or " +
                        "middle of each side",
            ) {
                onRouteSelected(Destinations.Transform)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoSelectionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(description)
        }
    }
}