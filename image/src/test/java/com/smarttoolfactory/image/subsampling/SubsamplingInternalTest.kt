package com.smarttoolfactory.image.subsampling

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SubsamplingInternalTest {

    @Test
    fun `source key generation uses stable prefixes`() {
        assertEquals(
            "asset:images/large.jpg",
            buildSubsamplingSourceKey(
                source = SubsamplingImageSource.Asset("images/large.jpg"),
                packageName = "com.example.app"
            )
        )

        assertEquals(
            "resource:com.example.app:42",
            buildSubsamplingSourceKey(
                source = SubsamplingImageSource.Resource(42),
                packageName = "com.example.app"
            )
        )

        assertEquals(
            "file:/tmp/large.jpg",
            buildSubsamplingSourceKey(
                source = SubsamplingImageSource.File(File("/tmp/large.jpg")),
                packageName = "com.example.app"
            )
        )
    }

    @Test
    fun `expanded decode region stays inside source bounds`() {
        val expanded = expandDecodeRegion(
            region = IntRect(left = 930, top = 760, right = 1000, bottom = 800),
            imageSize = IntSize(1000, 800),
            overscanFraction = 0.5f,
            minOverscan = 64
        )

        assertTrue(expanded.left >= 0)
        assertTrue(expanded.top >= 0)
        assertTrue(expanded.right <= 1000)
        assertTrue(expanded.bottom <= 800)
        assertEquals(1000, expanded.right)
        assertEquals(800, expanded.bottom)
    }

    @Test
    fun `in sample size tracks zoom level`() {
        val sourceSize = IntSize(7680, 5120)
        val containerSize = IntSize(1920, 1280)

        assertEquals(4, calculateInSampleSize(sourceSize, containerSize, zoom = 1f))
        assertEquals(2, calculateInSampleSize(sourceSize, containerSize, zoom = 2f))
        assertEquals(1, calculateInSampleSize(sourceSize, containerSize, zoom = 4f))
    }
}
