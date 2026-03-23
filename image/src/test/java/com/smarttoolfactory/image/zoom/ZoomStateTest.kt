package com.smarttoolfactory.image.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.image.util.rotateBy
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomStateTest {

    @Test
    fun `zoom in keeps off center centroid stable`() = runBlocking {
        val state = ZoomState(
            maxZoom = 5f,
            rotatable = false
        )
        state.size = IntSize(200, 100)

        val centroid = Offset(150f, 30f)
        val contentPoint = contentPointForCentroid(
            size = state.size,
            zoom = state.zoom,
            pan = state.pan,
            rotation = state.rotation,
            centroid = centroid
        )

        state.updateZoomState(
            centroid = centroid,
            panChange = Offset(12f, -7f),
            zoomChange = 1.8f,
            rotationChange = 0f
        )

        val centroidAfter = screenPointForContent(
            size = state.size,
            zoom = state.zoom,
            pan = state.pan,
            rotation = state.rotation,
            contentPoint = contentPoint
        )

        assertOffsetEquals(centroid, centroidAfter)
    }

    @Test
    fun `zoom out keeps off center centroid stable`() = runBlocking {
        val state = ZoomState(
            initialZoom = 2f,
            maxZoom = 5f,
            rotatable = false
        )
        state.size = IntSize(240, 160)
        state.snapPanXto(-30f)
        state.snapPanYto(18f)

        val centroid = Offset(182f, 54f)
        val contentPoint = contentPointForCentroid(
            size = state.size,
            zoom = state.zoom,
            pan = state.pan,
            rotation = state.rotation,
            centroid = centroid
        )

        state.updateZoomState(
            centroid = centroid,
            panChange = Offset(9f, 6f),
            zoomChange = 0.6f,
            rotationChange = 0f
        )

        val centroidAfter = screenPointForContent(
            size = state.size,
            zoom = state.zoom,
            pan = state.pan,
            rotation = state.rotation,
            contentPoint = contentPoint
        )

        assertOffsetEquals(centroid, centroidAfter)
    }

    @Test
    fun `pan change is ignored while zooming`() = runBlocking {
        val centroid = Offset(145f, 42f)
        val size = IntSize(220, 120)

        val stateWithPan = ZoomState(
            maxZoom = 5f,
            rotatable = false
        ).also { it.size = size }

        val stateWithoutPan = ZoomState(
            maxZoom = 5f,
            rotatable = false
        ).also { it.size = size }

        stateWithPan.updateZoomState(
            centroid = centroid,
            panChange = Offset(15f, -11f),
            zoomChange = 1.6f,
            rotationChange = 0f
        )

        stateWithoutPan.updateZoomState(
            centroid = centroid,
            panChange = Offset.Zero,
            zoomChange = 1.6f,
            rotationChange = 0f
        )

        assertEquals(stateWithoutPan.zoom, stateWithPan.zoom, TOLERANCE)
        assertOffsetEquals(stateWithoutPan.pan, stateWithPan.pan)
    }

    @Test
    fun `pure pan still uses zoom scaled translation`() = runBlocking {
        val state = ZoomState(
            initialZoom = 2f,
            maxZoom = 5f,
            rotatable = false
        )
        state.size = IntSize(200, 120)

        state.updateZoomState(
            centroid = Offset(100f, 60f),
            panChange = Offset(5f, -3f),
            zoomChange = 1f,
            rotationChange = 0f
        )

        assertOffsetEquals(Offset(10f, -6f), state.pan)
    }

    @Test
    fun `animated zoom clamps pan against updated bounds`() = runBlocking {
        val state = AnimatedZoomState(
            contentSize = IntSize(300, 100),
            initialZoom = 4f,
            minZoom = 1f,
            maxZoom = 5f,
            limitPan = true,
            rotatable = false
        )
        state.size = IntSize(100, 100)
        state.snapPanXto(550f)
        state.snapPanYto(0f)

        state.updateZoomState(
            centroid = Offset(0f, 50f),
            panChange = Offset(40f, 0f),
            zoomChange = 0.25f,
            rotationChange = 0f
        )

        assertEquals(1f, state.zoom, TOLERANCE)
        assertOffsetEquals(Offset(100f, 0f), state.pan)
    }

    @Test
    fun `rotation keeps centroid stable without pan drift`() = runBlocking {
        val centroid = Offset(166f, 78f)
        val size = IntSize(240, 180)

        val stateWithPan = ZoomState(
            initialZoom = 2f,
            initialRotation = 15f,
            maxZoom = 5f,
            rotatable = true
        ).also {
            it.size = size
            it.snapPanXto(20f)
            it.snapPanYto(-10f)
        }

        val stateWithoutPan = ZoomState(
            initialZoom = 2f,
            initialRotation = 15f,
            maxZoom = 5f,
            rotatable = true
        ).also {
            it.size = size
            it.snapPanXto(20f)
            it.snapPanYto(-10f)
        }

        val contentPoint = contentPointForCentroid(
            size = stateWithPan.size,
            zoom = stateWithPan.zoom,
            pan = stateWithPan.pan,
            rotation = stateWithPan.rotation,
            centroid = centroid
        )

        stateWithPan.updateZoomState(
            centroid = centroid,
            panChange = Offset(14f, 9f),
            zoomChange = 1f,
            rotationChange = 30f
        )

        stateWithoutPan.updateZoomState(
            centroid = centroid,
            panChange = Offset.Zero,
            zoomChange = 1f,
            rotationChange = 30f
        )

        val centroidAfter = screenPointForContent(
            size = stateWithPan.size,
            zoom = stateWithPan.zoom,
            pan = stateWithPan.pan,
            rotation = stateWithPan.rotation,
            contentPoint = contentPoint
        )

        assertOffsetEquals(centroid, centroidAfter)
        assertOffsetEquals(stateWithoutPan.pan, stateWithPan.pan)
    }

    private fun contentPointForCentroid(
        size: IntSize,
        zoom: Float,
        pan: Offset,
        rotation: Float,
        centroid: Offset
    ): Offset {
        val center = layoutCenter(size)
        val transformedVector = centroid - center - pan
        return center + transformedVector.rotateBy(-rotation).times(1f / zoom)
    }

    private fun screenPointForContent(
        size: IntSize,
        zoom: Float,
        pan: Offset,
        rotation: Float,
        contentPoint: Offset
    ): Offset {
        val center = layoutCenter(size)
        val contentVector = contentPoint - center
        return center + contentVector.rotateBy(rotation).times(zoom) + pan
    }

    private fun layoutCenter(size: IntSize): Offset {
        return Offset(size.width / 2f, size.height / 2f)
    }

    private fun assertOffsetEquals(expected: Offset, actual: Offset) {
        assertEquals(expected.x, actual.x, TOLERANCE)
        assertEquals(expected.y, actual.y, TOLERANCE)
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
