# Compose Image on Steroids

Collection of Images, Modifiers, utility functions for Jetpack Compose to expand
and enrich displaying, manipulating, scaling, resizing, zooming, and
getting cropped `ImageBitmap` based on selection area

https://user-images.githubusercontent.com/35650605/177825237-6b509ff5-5027-44aa-b9db-84b93f21fdfa.mp4

## ImageWithConstraints
A composable that lays out and draws a given `ImageBitmap`. This will attempt to  
size the composable according to the `ImageBitmap`'s given width and height.

 `ImageScope` returns constraints, width and height of the drawing area based on `contentScale`
* and rectangle of `imageBitmap` drawn. When a bitmap is displayed scaled to fit area of Composable
* space used for drawing image is represented with `ImageScope.imageWidth` and
* `ImageScope.imageHeight`.
*
* When we display a bitmap 1000x1000px with `ContentScale.Crop` if it's cropped to 500x500px
* `ImageScope.rect` returns `IntRect(250,250,750,750)`.

This composable enables building other `Image` based Composables that require you to know
spaces around `ImageBitmap` with `ContentScale ` or which section of Bitmap is drawn to `Canvas`

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
Composable that changes scale of its content from handles, translates its position 
when dragged inside bounds

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
Composable that changes dimensions of its content from handles, translates its position 
when dragged inside bounds. When using be mindful about the parent composable that contains this
Composable since maximum width and height this Composable depends on how a Composable, 
Column for instance,  lays out its children. It can be expanded upto remaining space if other
sibling occupy rest of the parent's available space

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

```kotlin
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    clipTransformToContentScale: Boolean = false,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
}
```
