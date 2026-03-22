package com.smarttoolfactory.image.subsampling

import android.net.Uri
import androidx.annotation.RawRes
import java.io.File as JavaFile

/**
 * Android-backed image sources for tiled subsampling.
 */
sealed interface SubsamplingImageSource {

    data class Resource(@param:RawRes val resId: Int) : SubsamplingImageSource

    data class Asset(val path: String) : SubsamplingImageSource

    data class File(val file: JavaFile) : SubsamplingImageSource

    data class ContentUri(val uri: Uri) : SubsamplingImageSource
}
