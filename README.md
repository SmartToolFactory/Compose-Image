# Compose Image on Steroids

[![](https://jitpack.io/v/SmartToolFactory/Compose-Image.svg)](https://jitpack.io/#SmartToolFactory/Compose-Image)



Collection of Images, Modifiers, utility functions for Jetpack Compose to expand
and enrich displaying, manipulating, scaling, resizing, zooming, and
getting cropped `ImageBitmap` based on selection area, before/after image to with handle to
show partial of both images and more is cooking up

https://user-images.githubusercontent.com/35650605/179715223-ba681886-6032-461f-806a-ea6a535d0627.mp4

https://user-images.githubusercontent.com/35650605/185785993-58aee8fb-8f27-4664-9ebe-5cde04b5a04e.mp4


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

## Image

### ImageWithConstraints

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

### ImageWithThumbnail

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

## Transform

These Composables and Modifiers are for scaling or resizing Image or Composable and move
from current position using handles.

### TransformLayout

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

### MorphLayout

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

## Zoom





Zoom feature provides zooming in or out Image, or Composables with animations and
getting data about current transform or image Using Callbacks.

### Modifier.zoom()

Modifier that zooms, pans, and rotates any Composable it set to. when [clip] is true
`Modifier.clipToBounds()` is used to limit content inside Composable bounds
`consume` param is for `Modifier.pointerInput` to consume current events to prevent other
gestures like scroll, drag or transform to initiate.
Callbacks notify user that gesture has started, going on finished with [ZoomData] that
contains current transformation information

```kotlin
fun Modifier.zoom(
    key: Any? = Unit,
    consume: Boolean = true,
    clip: Boolean = true,
    zoomState: ZoomState,
    onGestureStart: ((ZoomData) -> Unit)? = null,
    onGesture: ((ZoomData) -> Unit)? = null,
    onGestureEnd: ((ZoomData) -> Unit)? = null
)
```

#### Parameters

* **key/key1-key2/keys** are used for restarting `Modifier.pointerInput(*keys)` and remember
  for getting `ZoomState`
* **consume** flag to prevent other gestures such as scroll, drag or transform to get
* **clip** when set to true clips to parent bounds. Anything outside parent bounds is not drawn
  empty space on sides or edges of parent.
* **zoomState** State of the zoom that contains option to set initial, min, max zoom, enabling
  rotation, pan or zoom and contains current [ZoomData]event propagations
* **onGestureStart** callback to to notify gesture has started and return current ZoomData of this
  modifier
* **onGesture** callback to notify about ongoing gesture and return current ZoomData of this
  modifier
* **onGestureEnd** callback to notify that gesture finished and return current ZoomData of this
  modifier

### ZoomState

Create and [remember] the [ZoomState] based on the currently appropriate transform configuration to
allow changing pan, zoom, and rotation.

```kotlin
@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any? = Unit
): ZoomState {
    return remember(key1) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}
```

#### Parameters

* **initialZoom** zoom set initially
* **initialRotation** rotation set initially
* **minZoom** minimum zoom value
* **maxZoom** maximum zoom value
* **limitPan** limits pan to bounds of parent Composable. Using this flag prevents creating empty
  space on sides or edges of parent
* **zoomable** when set to true zoom is enabled
* **pannable** when set to true pan is enabled
* **rotatable** when set to true rotation is enabled

### ZoomableImage

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
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    clip: Boolean = true,
    clipTransformToContentScale: Boolean = false,
    consume: Boolean = true,
    onGestureStart: (ZoomData) -> Unit = {},
    onGesture: (ZoomData) -> Unit = {},
    onGestureEnd: (ZoomData) -> Unit = {}
)
```

#### Parameters

* **initialZoom** zoom set initially
* **minZoom** minimum zoom value this Composable can possess
* **maxZoom** maximum zoom value this Composable can possess
* **clip** whether image should be clip to bounds of Image
* **clipTransformToContentScale** when set true zoomable image takes borders of image drawn while
  zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
* **limitPan** limits pan to bounds of parent Composable. Using this flag prevents creating empty
  space on sides or edges of parent.
* **consume** flag to prevent other gestures such as scroll, drag or transform to get event
  propagations
* **zoomable** when set to true zoom is enabled
* **pannable** when set to true pan is enabled
* **rotatable** when set to true rotation is enabled
* **onGestureStart** callback to to notify gesture has started and return current ZoomData of this
  modifier
* **onGesture** callback to notify about ongoing gesture and return current ZoomData of this
  modifier
* **onGestureEnd** callback to notify that gesture finished and return current ZoomData of this
  modifier

### Modifier.enhancedZoom()

Modifier that zooms in or out of Composable set to. This zoom modifier has option to move back to
bounds with an animation or option to have fling gesture when user removes from screen while
velocity is higher than threshold to have smooth touch effect.

```kotlin
fun Modifier.enhancedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
)
```

#### Parameters

* **key** is used for [Modifier.pointerInput] to restart closure when any keys assigned change
* **clip** when set to true clips to parent bounds. Anything outside parent bounds is not drawn
  empty space on sides or edges of parent.
* **enhancedZoomState** State of the zoom that contains option to set initial, min, max zoom,
  enabling rotation, pan or zoom and contains current [EnhancedZoomData]event propagations. Also
  contains [Rect] of visible area based on pan, zoom and rotation
* **zoomOnDoubleTap** lambda that returns current [ZoomLevel] and based on current level enables
  developer to define zoom on double tap gesture
* **enabled** lambda can be used selectively enable or disable pan and intercepting with scroll,
  drag or lists or pagers using current zoom, pan or rotation values
* **onGestureStart callback to to notify gesture has started and return current [EnhancedZoomData]
  of this modifier
* **onGesture** callback to notify about ongoing gesture and return current [EnhancedZoomData]  of
  this modifier
* **onGestureEnd** callback to notify that gesture finished return current [EnhancedZoomData]  of
  this modifier

### EnhancedZoomState

Create and [remember] the [EnhancedZoomState] based on the currently appropriate transform
configuration to allow changing pan, zoom, and rotation.
Allows to change zoom, pan, translate, or get current state by calling methods on this object. To be
hosted and passed to [Modifier.enhancedZoom].
Also contains [EnhancedZoomData] about current transformation area of Composable and visible are of
image being zoomed, rotated, or panned. If any animation is going on
current [EnhancedZoomState.isAnimationRunning] is true and [EnhancedZoomData] returns rectangle that
belongs to end of animation.

```kotlin
fun Modifier.enhancedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
)
```

#### Parameters

* **initialZoom** zoom set initially
* **minZoom** minimum zoom value
* **maxZoom maximum zoom value
* **fling** when set to true dragging pointer builds up velocity. When last pointer leaves
  Composable a movement invoked against friction till velocity drops below to threshold
* **moveToBounds** when set to true if image zoom is lower than initial zoom or panned out of image
  boundaries moves back to bounds with animation.
* **zoomable** when set to true zoom is enabled
* **pannable** when set to true pan is enabled
* **rotatable** when set to true rotation is enabled
* **limitPan** limits pan to bounds of parent Composable. Using this flag prevents creating empty
  space on sides or edges of parent

### EnhancedZoomableImage

Zoomable image that zooms in and out in [ [minZoom], [maxZoom] ] interval and pans zoomed image
based on pointer position. Double tap gestures reset image translation and zoom to default values
with animation. Difference between `ZoomaableImage` and `EnhancedZoomableImage` is this version
can animate back to bounds and have fling gesture that doesn't stop movement when last pointer
is up but continues motion agains friction.

`moveToBound` is true image moves to bounds when moved out of bounds. When
`fling` is set to true image moves until velocity drops below threshold.

```
@Composable
fun EnhancedZoomableImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    limitPan: Boolean = true,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    clip: Boolean = true,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = DefaultOnDoubleTap,
    clipTransformToContentScale: Boolean = false,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null
)
```

#### Parameters

* **initialZoom** zoom set initially
* **minZoom** minimum zoom value this Composable can possess
* **maxZoom** maximum zoom value this Composable can possess
* **limitPan** limits pan to bounds of parent Composable. Using this flag prevents creating empty
  space on sides or edges of parent.
* **fling** when set to true dragging pointer builds up velocity. When last pointer leaves
  Composable a movement invoked against friction till velocity drops down to threshold
* **moveToBounds** when set to true if image zoom is lower than initial zoom or panned out of image
  boundaries moves back to bounds with animation.
* **zoomable** when set to true zoom is enabled
* **pannable** when set to true pan is enabled
* **rotatable** when set to true rotation is enabled
* **clip** when set to true clips to parent bounds. Anything outside parent bounds is not drawn
* **clipTransformToContentScale** when set true zoomable image takes borders of image drawn while
  zooming in. [contentScale] determines whether will be empty spaces on edges of Composable
* **zoomOnDoubleTap** lambda that returns current [ZoomLevel] and based on current level enables
  developer to define zoom on double tap gesture
* **enabled** lambda can be used selectively enable or disable pan and intercepting with scroll,
  drag or lists or pagers using current zoom, pan or rotation values
* **onGestureStart** callback to to notify gesture has started and return current ZoomData of this
  modifier
* **onGesture** callback to notify about ongoing gesture and return current ZoomData of this
  modifier
* **onGestureEnd** callback to notify that gesture finished and return current ZoomData of this
  modifier

### Modifier.animatedZoom()

Modifier that zooms in or out of Composable set to. This zoom modifier has option to move back to
bounds with an animation or option to have fling gesture when user removes from screen while
velocity is higher than threshold to have smooth touch effect.

Difference between `Modifier.enhancedZoom()` and `Modifier.animatedZoom()` is enhanced zoom
uses Bitmap dimensions and returns a callback that returns [EnhandedZoomData] that contains
visible image area which is suitable for crop while `Modifier.animatedZoom()` requires
dimensions of Composable to have valid pan limiting behavior. More suitable for zooming
Composables while enhanced zoom is more suitable for iamge zooming.

```kotlin
fun Modifier.animatedZoom(
    vararg keys: Any?,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
)
```

#### Parameters

* **keys** are used for [Modifier.pointerInput] to restart closure when any keys assigned change
* **clip** when set to true clips to parent bounds. Anything outside parent bounds is not drawn
* **animatedZoomState** State of the zoom that contains option to set initial, min, max zoom,
  enabling rotation, pan or zoom
* **zoomOnDoubleTap** lambda that returns current [ZoomLevel] and based on current level enables
  developer to define zoom on double tap gesture
* **enabled** lambda can be used selectively enable or disable pan and intercepting with scroll,
  drag or lists or pagers using current zoom, pan or rotation values

### AnimatedZoomState

Create and [remember] the [AnimatedZoomState] based on the currently appropriate transform
configuration to allow changing pan, zoom, and rotation.

Allows to change zoom, pan, translate, or get current state by calling methods on this object. To be
hosted and passed to [Modifier.animatedZoom].

#### Parameters

* **contentSize** when the content that will be zoomed is not parent pass child size to bound
  content correctly inside parent. If parent doesn't have any content this parameter is not required
* **initialZoom** zoom set initially
* **minZoom** minimum zoom value
* **maxZoom** maximum zoom value
* **fling** when set to true dragging pointer builds up velocity. When last
* pointer leaves Composable a movement invoked against friction till velocity drops below to
  threshold
* **moveToBounds** when set to true if image zoom is lower than initial zoom or panned out of image
  boundaries moves back to bounds with animation.
* **zoomable** when set to true zoom is enabled
* **pannable** when set to true pan is enabled
* **rotatable** when set to true rotation is enabled
* **limitPan** limits pan to bounds of parent Composable. Using this flag prevents creating empty
  space on sides or edges of parent
