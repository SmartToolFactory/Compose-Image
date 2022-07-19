# Compose Image on Steroids

[![](https://jitpack.io/v/SmartToolFactory/Compose-Image.svg)](https://jitpack.io/#SmartToolFactory/Compose-Image)


Collection of Images, Modifiers, utility functions for Jetpack Compose to expand
and enrich displaying, manipulating, scaling, resizing, zooming, and
getting cropped `ImageBitmap` based on selection area, before/after image to with handle to
show partial of both images and more is cooking up

https://user-images.githubusercontent.com/35650605/177950258-b9c122a9-b6df-422f-b03b-dcfe9a294b18.mp4

## Gradle Setup

To get a Git project into your build:

* Step 1. Add the JitPack repository to your build file Add it in your root build.gradle at the end
  of repositories:

```
allprojects {
  repositories {
      ...
      maven { url 'https://jitpack.io' }
  }
}
```

* Step 2. Add the dependency

```
dependencies {
    implementation 'com.github.SmartToolFactory:Compose-Image:<version>'
}
```


## ImageWithConstraints

A composable that lays out and draws a given `ImageBitmap`. This will attempt to  
size the composable according to the `ImageBitmap`'s given width and height.

`ImageScope` returns constraints, width and height of the drawing area based on `contentScale`
and rectangle of `imageBitmap` drawn. When a bitmap is displayed scaled to fit area of Composable
space used for drawing image is represented with `ImageScope.imageWidth` and
`ImageScope.imageHeight`. When we display a bitmap 1000x1000px with `ContentScale.Crop` if it's
cropped to 500x500px `ImageScope.rect` returns `IntRect(250,250,750,750)`.

This composable enables building other `Image` based Composables that require you to know
spaces around `ImageBitmap` based on `ContentScale ` or which section of Bitmap is drawn to `Canvas`

```kotlin
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
    imageScope: ImageScope->

}
```

returns `ImageScope` which is

```
@Stable
interface ImageScope {
    /**
     * The constraints given by the parent layout in pixels.
     *
     * Use [minWidth], [maxWidth], [minHeight] or [maxHeight] if you need value in [Dp].
     */
    val constraints: Constraints

    /**
     * The minimum width in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val minWidth: Dp

    /**
     * The maximum width in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val maxWidth: Dp

    /**
     * The minimum height in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val minHeight: Dp

    /**
     * The maximum height in [Dp].
     *
     * @see constraints for the values in pixels.
     */
    val maxHeight: Dp

    /**
     * Width of area inside BoxWithConstraints that is scaled based on [ContentScale]
     * This is width of the [Canvas] draws [ImageBitmap]
     */
    val imageWidth: Dp

    /**
     * Height of area inside BoxWithConstraints that is scaled based on [ContentScale]
     * This is height of the [Canvas] draws [ImageBitmap]
     */
    val imageHeight: Dp

    /**
     * [IntRect] that covers boundaries of [ImageBitmap]
     */
    val rect: IntRect
}
```

* drawImage param is to set whether this Composable should draw on Canvas. `ImageWithConstraints`
  can be used not only for drawing but providing required info for its `content` or child
  Composables so child can draw `ImageBitmap` as required by developer.

## ImageWithThumbnail

`ImageWithThumbnail` displays thumbnail of bitmap it draws in corner specified
by `ThumbnailState.position`. When touch position is close to thumbnail position
if `ThumbnailState.dynamicPosition` is set to true moves thumbnail
to corner specified by `ThumbnailState.moveTo`

```kotlin
@Composable
fun ImageWithThumbnail(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentDescription: String?,
    thumbnailState: ThumbnailState = rememberThumbnailState(),
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    drawOriginalImage: Boolean = true,
    onDown: ((Offset) -> Unit)? = null,
    onMove: ((Offset) -> Unit)? = null,
    onUp: (() -> Unit)? = null,
    onThumbnailCenterChange: ((Offset) -> Unit)? = null,
    content: @Composable ImageScope.() -> Unit = {}
) {

}
```

## TransformLayout

Composable that changes scale of its content with handles, translates its position
when dragged inside bounds.

```kotlin
@Composable
fun TransformLayout(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    handlePlacement: HandlePlacement = HandlePlacement.Corner,
    onDown: (Transform) -> Unit = {},
    onMove: (Transform) -> Unit = {},
    onUp: (Transform) -> Unit = {},
    content: @Composable () -> Unit
) {

}
```

## MorphLayout

Composable that changes dimensions of its content with handles, translates its position
when dragged inside bounds.

⚠️ Be careful about maximum dimension can be assigned to this Composable with handles
because maximum width and height depends on how a Composable,
Column for instance, lays out its children. It can be expanded up to remaining space if other
siblings occupy rest of the parent's available space set with parent `Layout`

```kotlin
@Composable
fun MorphLayout(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    handleRadius: Dp = 15.dp,
    handlePlacement: HandlePlacement = HandlePlacement.Corner,
    updatePhysicalSize: Boolean = false,
    onDown: () -> Unit = {},
    onMove: (DpSize) -> Unit = {},
    onUp: () -> Unit = {},
    content: @Composable () -> Unit
) {
}
```

## ZoomableImage

Zoomable image that zooms in and out in [ [minZoom], [maxZoom] ] interval and translates
zoomed image based on pointer position.
Double tap gestures reset image translation and zoom to default values with animation.
Callbacks notify user that gesture has started, going on finished with [ZoomData] that
contains current transformation information

```kotlin
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotationEnabled: Boolean = false,
    clipTransformToContentScale: Boolean = false,
    consume: Boolean = true,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {}
) {
}
```

## Modifier.zoom

Modifier that zooms, pans, and rotates any Composable it set to. when [clip] is true
`Modifier.clipToBounds()` is used to limit content inside Composable bounds
`consume` param is for `Modifier.pointerInput` to consume current events to prevent other
gestures like scroll, drag or transform to initiate.
Callbacks notify user that gesture has started, going on finished with [ZoomData] that
contains current transformation information

```kotlin
fun Modifier.zoom(
    key: Any?,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {},
) 
```
