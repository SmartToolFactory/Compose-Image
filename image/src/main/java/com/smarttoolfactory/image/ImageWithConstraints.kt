package com.smarttoolfactory.image

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.*


/**
 * A composable that lays out and draws a given [ImageBitmap]. This will attempt to
 * size the composable according to the [ImageBitmap]'s given width and height.
 *
 * [ImageScope] contains [Constraints] since [ImageWithConstraints] uses [BoxWithConstraints]
 * also it contains information about canvas width, height and top left position relative
 * to parent [BoxWithConstraints].
 *
 * @param alignment determines where image will be aligned inside [BoxWithConstraints]
 * This is observable when bitmap image/width ratio differs from [Canvas] that draws [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take. This text should be
 * localized, such as by using [androidx.compose.ui.res.stringResource] or similar
 * @param contentScale how image should be scaled inside Canvas to match parent dimensions.
 * [ContentScale.Fit] for instance maintains src ratio and scales image to fit inside the parent.
 * @param alpha Opacity to be applied to [imageBitmap] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [imageBitmap] when drawn into the destination
 * @param filterQuality Sampling algorithm applied to the [imageBitmap] when it is scaled and drawn
 * into the destination. The default is [FilterQuality.Low] which scales using a bilinear
 * sampling algorithm
 * @param content is a Composable that can be matched at exact position where [imageBitmap] is drawn.
 * This is useful for drawing thumbs, cropping or another layout that should match position
 * with the image that is scaled is drawn
 * @param drawImage flag to draw image on canvas. Some Composables might only require
 * the calculation and rectangle bounds of image after scaling but not drawing.
 * Composables like image cropper that scales or
 * rotates image. Drawing here again have 2 drawings overlap each other.
 */
@Composable
fun ImageWithConstraints(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    drawImage: Boolean = true,
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

        val bitmapWidth = imageBitmap.width
        val bitmapHeight = imageBitmap.height

        val (boxWidth: Int, boxHeight: Int) = getParentSize(bitmapWidth, bitmapHeight)

        // Src is Bitmap, Dst is the container(Image) that Bitmap will be displayed
        val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val dstSize = Size(boxWidth.toFloat(), boxHeight.toFloat())

        val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

        // Image is the container for bitmap that is located inside Box
        // image bounds can be smaller or bigger than its parent based on how it's scaled
        val imageWidth = bitmapWidth * scaleFactor.scaleX
        val imageHeight = bitmapHeight * scaleFactor.scaleY

        val bitmapRect = getScaledBitmapRect(
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            bitmapWidth = bitmapWidth,
            bitmapHeight = bitmapHeight
        )

        ImageLayout(
            constraints = constraints,
            imageBitmap = imageBitmap,
            bitmapRect = bitmapRect,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            drawImage = drawImage,
            content = content
        )
    }
}

/**
 * Get Rectangle of [ImageBitmap] with [bitmapWidth] and [bitmapHeight] that is drawn inside
 * Canvas with [imageWidth] and [imageHeight]. [boxWidth] and [boxHeight] belong
 * to [BoxWithConstraints] that contains Canvas.
 *  @param boxWidth width of the parent container
 *  @param boxHeight height of the parent container
 *  @param imageWidth width of the [Canvas] that draw [ImageBitmap]
 *  @param imageHeight height of the [Canvas] that draw [ImageBitmap]
 *  @param bitmapWidth intrinsic width of the [ImageBitmap]
 *  @param bitmapHeight intrinsic height of the [ImageBitmap]
 *  @return [IntRect] that covers [ImageBitmap] bounds. When image [ContentScale] is crop
 *  this rectangle might return smaller rectangle than actual [ImageBitmap] and left or top
 *  of the rectangle might be bigger than zero.
 */
private fun getScaledBitmapRect(
    boxWidth: Int,
    boxHeight: Int,
    imageWidth: Float,
    imageHeight: Float,
    bitmapWidth: Int,
    bitmapHeight: Int
): IntRect {
    // Get scale of box to width of the image
    // We need a rect that contains Bitmap bounds to pass if any child requires it
    // For a image with 100x100 px with 300x400 px container and image with crop 400x400px
    // So we need to pass top left as 0,50 and size
    val scaledBitmapX = boxWidth / imageWidth
    val scaledBitmapY = boxHeight / imageHeight

    val topLeft = IntOffset(
        x = (bitmapWidth * (imageWidth - boxWidth) / imageWidth / 2)
            .coerceAtLeast(0f).toInt(),
        y = (bitmapHeight * (imageHeight - boxHeight) / imageHeight / 2)
            .coerceAtLeast(0f).toInt()
    )

    val size = IntSize(
        width = (bitmapWidth * scaledBitmapX).toInt().coerceAtMost(bitmapWidth),
        height = (bitmapHeight * scaledBitmapY).toInt().coerceAtMost(bitmapHeight)
    )

    return IntRect(offset = topLeft, size = size)
}

/**
 * Get [IntSize] of the parent or container that contains [Canvas] that draws [ImageBitmap]
 *  @param bitmapWidth intrinsic width of the [ImageBitmap]
 *  @param bitmapHeight intrinsic height of the [ImageBitmap]
 *  @return size of parent Composable. When Modifier is assigned with fixed or finite size
 *  they are used, but when any dimension is set to infinity intrinsic dimensions of
 *  [ImageBitmap] are returned
 */
private fun BoxWithConstraintsScope.getParentSize(
    bitmapWidth: Int,
    bitmapHeight: Int
): IntSize {
    // Check if Composable has fixed size dimensions
    val hasBoundedDimens = constraints.hasBoundedWidth && constraints.hasBoundedHeight
    // Check if Composable has infinite dimensions
    val hasFixedDimens = constraints.hasFixedWidth && constraints.hasFixedHeight

    // Box is the parent(BoxWithConstraints) that contains Canvas under the hood
    // Canvas aspect ratio or size might not match parent but it's upper bounds are
    // what are passed from parent. Canvas cannot be bigger or taller than BoxWithConstraints
    val boxWidth: Int = if (hasBoundedDimens || hasFixedDimens) {
        constraints.maxWidth
    } else {
        constraints.minWidth.coerceAtLeast(bitmapWidth)
    }
    val boxHeight: Int = if (hasBoundedDimens || hasFixedDimens) {
        constraints.maxHeight
    } else {
        constraints.minHeight.coerceAtLeast(bitmapHeight)
    }
    return IntSize(boxWidth, boxHeight)
}

@Composable
private fun ImageLayout(
    constraints: Constraints,
    imageBitmap: ImageBitmap,
    bitmapRect: IntRect,
    imageWidth: Float,
    imageHeight: Float,
    boxWidth: Int,
    boxHeight: Int,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    drawImage: Boolean = true,
    content: @Composable ImageScope.() -> Unit
) {
    val density = LocalDensity.current

    // Dimensions of canvas that will draw this Bitmap
    val canvasWidthInDp: Dp
    val canvasHeightInDp: Dp

    with(density) {
        canvasWidthInDp = imageWidth.coerceAtMost(boxWidth.toFloat()).toDp()
        canvasHeightInDp = imageHeight.coerceAtMost(boxHeight.toFloat()).toDp()
    }

    // Send the not scaled ImageBitmap dimensions which can be larger than Canvas size
    // but the one constraint with Canvas size
    // because modes like ContentScale.Crop
    // which displays center section of the ImageBitmap if it's scaled
    // to be bigger than Canvas.
    // What user see on screen cannot be bigger than Canvas dimensions
    val imageScopeImpl = ImageScopeImpl(
        density = density,
        constraints = constraints,
        imageWidth = canvasWidthInDp,
        imageHeight = canvasHeightInDp,
        rect = bitmapRect
    )

    // width and height params for translating draw position if scaled Image dimensions are
    // bigger than Canvas dimensions
    if (drawImage) {
        ImageImpl(
            modifier = Modifier.size(canvasWidthInDp, canvasHeightInDp),
            imageBitmap = imageBitmap,
            alpha = alpha,
            width = imageWidth.toInt(),
            height = imageHeight.toInt(),
            colorFilter = colorFilter,
            filterQuality = filterQuality
        )
    }

    imageScopeImpl.content()
}

@Composable
private fun ImageImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    width: Int,
    height: Int,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    Canvas(modifier = modifier.clipToBounds()) {

        val canvasWidth = size.width.toInt()
        val canvasHeight = size.height.toInt()

        // Translate to left or down when Image size is bigger than this canvas.
        // ImageSize is bigger when scale modes like Crop is used which enlarges image
        // For instance 1000x1000 image can be 1000x2000 for a Canvas with 1000x1000
        // so top is translated -500 to draw center of ImageBitmap
        translate(
            top = (-height + canvasHeight) / 2f,
            left = (-width + canvasWidth) / 2f,

            ) {
            drawImage(
                imageBitmap,
                srcSize = IntSize(bitmapWidth, bitmapHeight),
                dstSize = IntSize(width, height),
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality
            )
        }
    }
}
