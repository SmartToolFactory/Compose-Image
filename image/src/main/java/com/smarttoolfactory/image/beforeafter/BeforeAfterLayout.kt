package com.smarttoolfactory.image.beforeafter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.gesture.pointerMotionEvents

@Composable
fun BeforeAfterLayout(
    modifier: Modifier = Modifier,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    beforeLabel: @Composable () -> Unit = {},
    afterLabel: @Composable () -> Unit = {},
    overlay: @Composable (DpSize, Offset) -> Unit = { size, offset -> }
) {

    var parentSize by remember { mutableStateOf(Size.Zero) }
    var handlePosition by remember { mutableStateOf(100f) }

    var isHandleTouched by remember { mutableStateOf(false) }

    var zoom by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    val shapeAfter by remember(handlePosition, zoom, pan) {
        mutableStateOf(
            GenericShape { size: Size, layoutDirection: LayoutDirection ->

                val maxX = (parentSize.width * (zoom - 1) / 2f)
                val panPos = (maxX - pan.x) / zoom

//                val shapeOffset = handlePosition / zoom + panPos
                val shapeOffset = handlePosition

                moveTo(0f, 0f)
                lineTo(shapeOffset, 0f)
                lineTo(shapeOffset, size.height)
                lineTo(0f, size.height)
                close()
            }
        )
    }

//    val shapeBefore by remember(handlePosition, zoom, pan) {
//        mutableStateOf(
//            GenericShape { size: Size, layoutDirection: LayoutDirection ->
//
//                val maxX = (parentSize.width * (zoom - 1) / 2f)
//                val panPos = (maxX - pan.x) / zoom
//
//                val shapeOffset = handlePosition / zoom + panPos
//
//                moveTo(0f, 0f)
//                lineTo(shapeOffset, 0f)
//                lineTo(shapeOffset, size.height)
//                lineTo(0f, size.height)
//                close()
//            }
//        )
//    }

    val gestureModifier = Modifier
        .clipToBounds()
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { _: Offset, panChange: Offset, zoomChange: Float, _, _, _ ->

                    zoom = (zoom * zoomChange).coerceIn(1f, 5f)

                    val maxX = (size.width * (zoom - 1) / 2f)
                    val maxY = (size.height * (zoom - 1) / 2f)

                    val newPan = pan + panChange.times(zoom)
                    pan = Offset(
                        newPan.x.coerceIn(-maxX, maxX),
                        newPan.y.coerceIn(-maxY, maxY)
                    )
                }
            )
        }
        .pointerMotionEvents(
            onDown = {
                val position = it.position
                val xPos = position.x
                isHandleTouched = ((handlePosition - xPos) * (handlePosition - xPos) < 10000)
            },
            onMove = {
                if (isHandleTouched) {
                    handlePosition = it.position.x
                    it.consume()
                }
            },
            onUp = {
                isHandleTouched = false
            }
        )


    val parentModifier = modifier
        .then(gestureModifier)
        .onSizeChanged {
            parentSize = Size(it.width.toFloat(), it.height.toFloat())
        }

    val afterModifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            this.clip = true
            this.shape = shapeAfter
        }

    BoxWithConstraints {
        Box(modifier = parentModifier) {
            Box {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        this.scaleX = zoom
                        this.scaleY = zoom
                        this.translationX = pan.x
                        this.translationY = pan.y
                    }
                ) {
                    beforeContent()
                }

                Label(
                    text = "Before", modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                )
            }

            Box(afterModifier) {

                Box(modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        this.scaleX = zoom
                        this.scaleY = zoom
                        this.translationX = pan.x
                        this.translationY = pan.y
                    }
                ) {
                    afterContent()
                }

                Label(
                    text = "After",
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                )
            }
        }

//        Canvas(modifier = Modifier.matchParentSize()) {
//
//            val canvasWidth = size.width
//
//            val imagePosition = handlePosition.coerceIn(0f, canvasWidth)
//
//            drawLine(
//                Color.White,
//                strokeWidth = 2.dp.toPx(),
//                start = Offset(imagePosition, 0f),
//                end = Offset(imagePosition, size.height)
//            )
//            drawCircle(
//                color = Color.Red,
//                center = Offset(imagePosition, size.height / 2),
//                radius = 30f
//            )
//        }

    }
}
