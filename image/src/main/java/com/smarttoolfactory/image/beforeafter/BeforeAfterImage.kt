package com.smarttoolfactory.image.beforeafter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.*
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.ImageScope
import com.smarttoolfactory.image.ImageScopeImpl
import com.smarttoolfactory.image.getParentSize
import com.smarttoolfactory.image.getScaledBitmapRect


/**
 * A composable that lays out and draws a given [ImageBitmap]. This will attempt to
 * size the composable according to the [ImageBitmap]'s given width and height. However, an
 * optional [Modifier] parameter can be provided to adjust sizing or draw additional content (ex.
 * background). Any unspecified dimension will leverage the [ImageBitmap]'s size as a minimum
 * constraint.
 *
 * [ImageScope] returns constraints, width and height of the drawing area based on [contentScale]
 * and rectangle of [beforeImage] drawn. When a bitmap is displayed scaled to fit area of Composable
 * space used for drawing image is represented with [ImageScope.imageWidth] and
 * [ImageScope.imageHeight].
 *
 * When we display a bitmap 1000x1000px with [ContentScale.Crop] if it's cropped to 500x500px
 * [ImageScope.rect] returns `IntRect(250,250,750,750)`.
 *
 * @param alignment determines where image will be aligned inside [BoxWithConstraints]
 * This is observable when bitmap image/width ratio differs from [Canvas] that draws [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param contentScale how image should be scaled inside Canvas to match parent dimensions.
 * [ContentScale.Fit] for instance maintains src ratio and scales image to fit inside the parent.
 * @param alpha Opacity to be applied to [beforeImage] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [beforeImage] when drawn into the destination
 * @param filterQuality Sampling algorithm applied to the [beforeImage] when it is scaled and drawn
 * into the destination. The default is [FilterQuality.Low] which scales using a bilinear
 * sampling algorithm
 * @param content is a Composable that can be matched at exact position where [beforeImage] is drawn.
 * This is useful for drawing thumbs, cropping or another layout that should match position
 * with the image that is scaled is drawn
 */
@Composable
fun BeforeAfterImage(
    modifier: Modifier = Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    content: @Composable ImageScope.() -> Unit = {}
) {

    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier
            .then(semantics),
        contentAlignment = alignment,
    ) {

        val bitmapWidth = beforeImage.width
        val bitmapHeight = beforeImage.height


        val (boxWidth: Int, boxHeight: Int) = getParentSize(bitmapWidth, bitmapHeight)

        // Src is Bitmap, Dst is the container(Image) that Bitmap will be displayed
        val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val dstSize = Size(boxWidth.toFloat(), boxHeight.toFloat())

        val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

        // Image is the container for bitmap that is located inside Box
        // image bounds can be smaller or bigger than its parent based on how it's scaled
        val imageWidth = bitmapWidth * scaleFactor.scaleX
        val imageHeight = bitmapHeight * scaleFactor.scaleY

        var handlePosition by remember { mutableStateOf(imageWidth.coerceAtMost(boxWidth.toFloat()) / 2f) }

        var isHandleTouched by remember { mutableStateOf(false) }

        var zoom by remember { mutableStateOf(1f) }
        var pan by remember { mutableStateOf(Offset.Zero) }


        val imageModifier = Modifier
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
            .graphicsLayer {
                this.scaleX = zoom
                this.scaleY = zoom
                this.translationX = pan.x
                this.translationY = pan.y
            }

        val bitmapRect = getScaledBitmapRect(
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            bitmapWidth = bitmapWidth,
            bitmapHeight = bitmapHeight
        )

        val density = LocalDensity.current

        // Dimensions of canvas that will draw this Bitmap
        val canvasWidthInDp: Dp
        val canvasHeightInDp: Dp

        with(density) {
            canvasWidthInDp = imageWidth.coerceAtMost(boxWidth.toFloat()).toDp()
            canvasHeightInDp = imageHeight.coerceAtMost(boxHeight.toFloat()).toDp()
        }

        ImageLayout(
            modifier = imageModifier,
            constraints = constraints,
            beforeImage = beforeImage,
            afterImage = afterImage,
            handlePosition = handlePosition,
            translateX = pan.x,
            zoom = zoom,
            bitmapRect = bitmapRect,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            canvasWidthInDp = canvasWidthInDp,
            canvasHeightInDp = canvasHeightInDp,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            content = content
        )
    }
}

@Composable
private fun ImageLayout(
    modifier: Modifier,
    constraints: Constraints,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    handlePosition: Float,
    translateX: Float,
    zoom: Float,
    bitmapRect: IntRect,
    imageWidth: Float,
    imageHeight: Float,
    canvasWidthInDp: Dp,
    canvasHeightInDp: Dp,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    content: @Composable ImageScope.() -> Unit
) {

    val density = LocalDensity.current

    // Send rectangle of Bitmap drawn to Canvas as bitmapRect, content scale modes like
    // crop might crop image from center so Rect can be such as IntRect(250,250,500,500)

    // canvasWidthInDp, and  canvasHeightInDp are Canvas dimensions coerced to Box size
    // that covers Canvas
    val imageScopeImpl = ImageScopeImpl(
        density = density,
        constraints = constraints,
        imageWidth = canvasWidthInDp,
        imageHeight = canvasHeightInDp,
        rect = bitmapRect
    )

    // width and height params for translating draw position if scaled Image dimensions are
    // bigger than Canvas dimensions
    ImageImpl(
        modifier = modifier.size(canvasWidthInDp, canvasHeightInDp),
        beforeImage = beforeImage,
        afterImage = afterImage,
        handlePosition = handlePosition,
        translateX = translateX,
        zoom = zoom,
        alpha = alpha,
        width = imageWidth.toInt(),
        height = imageHeight.toInt(),
        canvasWidthInDp = canvasWidthInDp,
        canvasHeightInDp = canvasHeightInDp,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )

    imageScopeImpl.content()
}

@Composable
private fun ImageImpl(
    modifier: Modifier,
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    handlePosition: Float,
    translateX: Float,
    zoom: Float,
    width: Int,
    height: Int,
    canvasWidthInDp: Dp,
    canvasHeightInDp: Dp,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    val bitmapWidth = beforeImage.width
    val bitmapHeight = beforeImage.height

    Box {
        Canvas(modifier = modifier) {

            val canvasWidth = size.width
            val canvasHeight = size.height

            val touchPosition =
                (+width - canvasWidth) / 2f + (handlePosition / zoom).coerceIn(0f, canvasWidth)
                    .toInt()

            // Translate to left or down when Image size is bigger than this canvas.
            // ImageSize is bigger when scale modes like Crop is used which enlarges image
            // For instance 1000x1000 image can be 1000x2000 for a Canvas with 1000x1000
            // so top is translated -500 to draw center of ImageBitmap
            translate(
                top = (-height + canvasHeight) / 2f,
                left = (-width + canvasWidth) / 2f,
            ) {

                val maxX = (size.width * (zoom - 1) / 2f)
                val pan = (maxX - translateX) / zoom

                println(
                    "🔥 canvasWidth: $canvasWidth, bitmapWidth: $bitmapWidth, maxX: $maxX\n" +
                            "touchPosition: $touchPosition, translateX: $translateX, pan: $pan, zoom: $zoom"
                )


                val srcOffsetX = ((pan + touchPosition) * bitmapWidth / width).toInt()
                val dstOffsetX = (pan + touchPosition).toInt()

                println("🍏 srcOffsetX: $srcOffsetX, dstOffsetX: $dstOffsetX")

                drawImage(
                    afterImage,
                    srcSize = IntSize(bitmapWidth, bitmapHeight),
                    dstSize = IntSize(width, height),
                    alpha = alpha,
                    colorFilter = colorFilter,
                    filterQuality = filterQuality
                )
                drawImage(
                    beforeImage,
                    srcSize = IntSize(bitmapWidth, bitmapHeight),
                    srcOffset = IntOffset(srcOffsetX, 0),
                    dstSize = IntSize(width, height),
                    dstOffset = IntOffset(dstOffsetX, 0),
                    alpha = alpha,
                    colorFilter = colorFilter,
                    filterQuality = filterQuality
                )
            }
        }

        Canvas(modifier = Modifier.size(canvasWidthInDp, canvasHeightInDp)) {

            val canvasWidth = size.width

            val imagePosition = handlePosition.coerceIn(0f, canvasWidth)

            drawLine(
                Color.White,
                strokeWidth = 2.dp.toPx(),
                start = Offset(imagePosition, 0f),
                end = Offset(imagePosition, size.height)
            )
            drawCircle(
                color = Color.Red,
                center = Offset(imagePosition, size.height / 2),
                radius = 30f
            )
        }
    }
}
