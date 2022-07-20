package com.smarttoolfactory.image.beforeafter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Black transparent label to display before or after text
 */
@Composable
fun Label(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(Color.Black.copy(alpha = .5f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
fun BoxScope.BeforeLabel(contentOrder: ContentOrder) {
    Label(
        text = "Before", modifier = Modifier
            .padding(8.dp)
            .align(
                if (contentOrder == ContentOrder.BeforeAfter)
                    Alignment.TopStart else Alignment.TopEnd
            )
    )
}

@Composable
fun BoxScope.AfterLabel(contentOrder: ContentOrder) {
    Label(
        text = "After", modifier = Modifier
            .padding(8.dp)
            .align(
                if (contentOrder == ContentOrder.BeforeAfter)
                    Alignment.TopEnd else Alignment.TopStart
            )
    )
}
