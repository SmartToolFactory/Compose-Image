package com.smarttoolfactory.image.subsampling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.util.LruCache
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import java.io.Closeable
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

internal data class SubsamplingDecodeRequest(
    val visibleSourceRect: IntRect,
    val decodeSourceRect: IntRect,
    val inSampleSize: Int
) {
    fun cacheKey(sourceKey: String): String {
        return buildString {
            append(sourceKey)
            append('|')
            append(decodeSourceRect.left)
            append(',')
            append(decodeSourceRect.top)
            append(',')
            append(decodeSourceRect.right)
            append(',')
            append(decodeSourceRect.bottom)
            append('|')
            append(inSampleSize)
        }
    }
}

@Immutable
internal data class DecodedBitmapResult(
    val sourceRect: IntRect,
    val inSampleSize: Int,
    val bitmap: Bitmap
)

internal object SubsamplingBitmapCache {
    private val cache = object : LruCache<String, DecodedBitmapResult>(cacheSizeInKb()) {
        override fun sizeOf(key: String, value: DecodedBitmapResult): Int {
            return value.bitmap.byteCount / 1024
        }
    }

    fun get(key: String): DecodedBitmapResult? = cache.get(key)

    fun put(key: String, value: DecodedBitmapResult) {
        cache.put(key, value)
    }

    private fun cacheSizeInKb(): Int {
        val maxMemoryKb = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        return min(maxMemoryKb / 16, 32 * 1024)
    }
}

internal class BitmapRegionDecoderHolder private constructor(
    val key: String,
    private val decoder: BitmapRegionDecoder
) : Closeable {
    private val lock = Any()

    @Volatile
    private var isClosed = false

    val imageSize: IntSize = IntSize(decoder.width, decoder.height)

    fun decodeRegion(request: SubsamplingDecodeRequest): DecodedBitmapResult? {
        val options = BitmapFactory.Options().apply {
            inSampleSize = request.inSampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val bitmap = synchronized(lock) {
            if (isClosed) return null

            decoder.decodeRegion(request.decodeSourceRect.toAndroidRect(), options)
        } ?: return null

        return DecodedBitmapResult(
            sourceRect = request.decodeSourceRect,
            inSampleSize = request.inSampleSize,
            bitmap = bitmap
        )
    }

    override fun close() {
        synchronized(lock) {
            if (isClosed) return

            isClosed = true
            decoder.recycle()
        }
    }

    companion object {
        fun open(
            context: Context,
            source: SubsamplingImageSource
        ): BitmapRegionDecoderHolder {
            val key = buildSubsamplingSourceKey(source, context.packageName)
            val decoder = openSubsamplingInputStream(context, source).use(::createBitmapRegionDecoder)
            return BitmapRegionDecoderHolder(key = key, decoder = decoder)
        }
    }
}

internal fun buildSubsamplingSourceKey(
    source: SubsamplingImageSource,
    packageName: String
): String = when (source) {
    is SubsamplingImageSource.Asset -> "asset:${source.path}"
    is SubsamplingImageSource.ContentUri -> "content:${source.uri}"
    is SubsamplingImageSource.File -> "file:${source.file.absolutePath}"
    is SubsamplingImageSource.Resource -> "resource:$packageName:${source.resId}"
}

internal fun openSubsamplingInputStream(
    context: Context,
    source: SubsamplingImageSource
): InputStream = when (source) {
    is SubsamplingImageSource.Asset -> context.assets.open(source.path)
    is SubsamplingImageSource.ContentUri -> requireNotNull(
        context.contentResolver.openInputStream(source.uri)
    ) { "Unable to open content URI: ${source.uri}" }
    is SubsamplingImageSource.File -> source.file.inputStream()
    is SubsamplingImageSource.Resource -> context.resources.openRawResource(source.resId)
}

internal fun mapPreviewRectToSourceRect(
    previewRect: IntRect,
    previewSize: IntSize,
    sourceSize: IntSize
): IntRect {
    val left = floor(previewRect.left * sourceSize.width / previewSize.width.toFloat()).toInt()
    val top = floor(previewRect.top * sourceSize.height / previewSize.height.toFloat()).toInt()
    val right = ceil(previewRect.right * sourceSize.width / previewSize.width.toFloat()).toInt()
    val bottom = ceil(previewRect.bottom * sourceSize.height / previewSize.height.toFloat()).toInt()

    return IntRect(
        left = left.coerceIn(0, sourceSize.width - 1),
        top = top.coerceIn(0, sourceSize.height - 1),
        right = right.coerceIn(left + 1, sourceSize.width),
        bottom = bottom.coerceIn(top + 1, sourceSize.height)
    )
}

internal fun calculateSubsamplingStartZoom(
    previewRect: IntRect,
    containerSize: IntSize
): Float {
    if (containerSize.width == 0 || containerSize.height == 0) return 1f

    val widthThreshold = previewRect.width / containerSize.width.toFloat()
    val heightThreshold = previewRect.height / containerSize.height.toFloat()
    return min(widthThreshold, heightThreshold).coerceAtLeast(1f)
}

internal fun calculateDecodeRequest(
    baseSourceRect: IntRect,
    visibleRegion: IntRect,
    containerSize: IntSize,
    zoom: Float,
    overscanFraction: Float = 0.25f,
    minOverscan: Int = 64
): SubsamplingDecodeRequest {
    val expandedVisibleRegion = expandDecodeRegion(
        region = visibleRegion,
        imageSize = baseSourceRect.size,
        overscanFraction = overscanFraction,
        minOverscan = minOverscan
    )

    val fullVisibleRegion = visibleRegion.offsetBy(baseSourceRect.left, baseSourceRect.top)
    val fullDecodeRegion = expandedVisibleRegion.offsetBy(baseSourceRect.left, baseSourceRect.top)

    return SubsamplingDecodeRequest(
        visibleSourceRect = fullVisibleRegion,
        decodeSourceRect = fullDecodeRegion,
        inSampleSize = calculateInSampleSize(
            sourceSize = baseSourceRect.size,
            containerSize = containerSize,
            zoom = zoom
        )
    )
}

internal fun expandDecodeRegion(
    region: IntRect,
    imageSize: IntSize,
    overscanFraction: Float = 0.25f,
    minOverscan: Int = 64
): IntRect {
    val paddingX = max((region.width * overscanFraction).toInt(), minOverscan)
    val paddingY = max((region.height * overscanFraction).toInt(), minOverscan)

    return IntRect(
        left = (region.left - paddingX).coerceAtLeast(0),
        top = (region.top - paddingY).coerceAtLeast(0),
        right = (region.right + paddingX).coerceAtMost(imageSize.width),
        bottom = (region.bottom + paddingY).coerceAtMost(imageSize.height)
    )
}

internal fun calculateInSampleSize(
    sourceSize: IntSize,
    containerSize: IntSize,
    zoom: Float
): Int {
    if (containerSize.width == 0 || containerSize.height == 0) return 1

    val widthRatio = sourceSize.width / (containerSize.width * zoom)
    val heightRatio = sourceSize.height / (containerSize.height * zoom)
    val maxRatio = max(widthRatio, heightRatio)

    var sampleSize = 1
    while (sampleSize * 2 <= maxRatio) {
        sampleSize *= 2
    }
    return sampleSize.coerceAtLeast(1)
}

internal fun IntRect.contains(other: IntRect): Boolean {
    return left <= other.left &&
        top <= other.top &&
        right >= other.right &&
        bottom >= other.bottom
}

internal fun IntRect.offsetBy(dx: Int, dy: Int): IntRect {
    return IntRect(
        offset = IntOffset(left + dx, top + dy),
        size = size
    )
}

private fun IntRect.toAndroidRect(): android.graphics.Rect {
    return android.graphics.Rect(left, top, right, bottom)
}

@Suppress("DEPRECATION")
private fun createBitmapRegionDecoder(inputStream: InputStream): BitmapRegionDecoder {
    return requireNotNull(BitmapRegionDecoder.newInstance(inputStream, false)) {
        "Unable to create BitmapRegionDecoder"
    }
}
