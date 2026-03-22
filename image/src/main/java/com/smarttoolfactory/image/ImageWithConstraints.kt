package com.smarttoolfactory.image

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import com.smarttoolfactory.image.util.getParentSize
import com.smarttoolfactory.image.util.getScaledBitmapRect


/**
 * A composable that lays out and draws a given [ImageBitmap]. This will attempt to
 * size the composable according to the [ImageBitmap]'s given width and height. However, an
 * optional [Modifier] parameter can be provided to adjust sizing or draw additional content (ex.
 * background). Any unspecified dimension will leverage the [ImageBitmap]'s size as a minimum
 * constraint.
 *
 * [ImageScope] returns constraints, width and height of the drawing area based on [contentScale]
 * and rectangle of [imageBitmap] drawn. When a bitmap is displayed scaled to fit area of Composable
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

    val bitmapPainter =
        remember(imageBitmap) { BitmapPainter(imageBitmap, filterQuality = filterQuality) }

    ImageWithConstraints(
        modifier = modifier,
        alignment = alignment,
        painter = bitmapPainter,
        contentScale = contentScale,
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = colorFilter,
        drawImage = drawImage,
        content = content
    )
}

@Composable
fun ImageWithConstraints(
    modifier: Modifier,
    alignment: Alignment,
    painter: Painter,
    contentScale: ContentScale,
    alpha: Float,
    colorFilter: ColorFilter?,
    drawImage: Boolean,
    contentDescription: String?,
    content: @Composable ImageScope.() -> Unit
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

        val bitmapWidth = painter.intrinsicSize.width.toInt()
        val bitmapHeight = painter.intrinsicSize.height.toInt()

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
            painter = painter,
            bitmapRect = bitmapRect,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            alpha = alpha,
            colorFilter = colorFilter,
            drawImage = drawImage,
            content = content
        )
    }
}

@Composable
private fun ImageLayout(
    constraints: Constraints,
    painter: Painter,
    bitmapRect: IntRect,
    imageWidth: Float,
    imageHeight: Float,
    boxWidth: Int,
    boxHeight: Int,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
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

    // Send rectangle of Bitmap drawn to Canvas as bitmapRect, content scale modes like
    // ContentScale.Crop might crop image so Rect can be such as IntRect(250,250,500,500)

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
    if (drawImage) {
        ImageImpl(
            modifier = Modifier.size(canvasWidthInDp, canvasHeightInDp),
            painter = painter,
            alpha = alpha,
            width = imageWidth.toInt(),
            height = imageHeight.toInt(),
            colorFilter = colorFilter,
        )
    }

    imageScopeImpl.content()
}

@Composable
private fun ImageImpl(
    modifier: Modifier,
    painter: Painter,
    width: Int,
    height: Int,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {
    val bitmapWidth = painter.intrinsicSize.width
    val bitmapHeight = painter.intrinsicSize.height

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

            with(painter) {
                draw(
                    size = Size(width.toFloat(), height.toFloat()),
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }
//            drawImage(
//                painter,
//                srcSize = IntSize(bitmapWidth, bitmapHeight),
//                dstSize = IntSize(width, height),
//                alpha = alpha,
//                colorFilter = colorFilter,
//                filterQuality = filterQuality
//            )
        }
    }
}
