# Compose ScreenshotBox

[![](https://jitpack.io/v/SmartToolFactory/Compose-Screenshot.svg)](https://jitpack.io/#SmartToolFactory/Compose-Screenshot)

Screenshot Composables and convert to Bitmap on user action or periodically.

| Screenshot with State| Single Screenshot | Periodic Screenshot |
| ----------|-----------| -----------|
| <img src="./art/screenshot.gif"/> | <img src="./art/screenshot2.gif"/> | <img src="./art/periodic_screenshot.gif"/> |

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
    implementation 'com.github.SmartToolFactory:Compose-Screenshot:Tag'
}
```

## Implementation

### Single Shot

Create a `ScreenshotBox` which covers your Composables you want to take screenshot of

```kotlin
ScreenshotBox(screenshotState = screenshotState) {
    Column(
        modifier = Modifier
            .border(2.dp, Color.Green)
            .padding(5.dp)
    ) {

        Image(
            bitmap = ImageBitmap.imageResource(
                LocalContext.current.resources,
                R.drawable.landscape
            ),
            contentDescription = null,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                // This is for displaying different ratio, optional
                .aspectRatio(4f / 3),
            contentScale = ContentScale.Crop
        )

        Text(text = "Counter: $counter")
        Slider(value = progress, onValueChange = { progress = it })
    }
}
```

Provide a `ScreenshotState` which stores Bitmap

```
val screenshotState = rememberScreenshotState()
```

Take screenshot by calling `screenshotState.capture()`

```kotlin
Button(
    onClick = {
        screenshotState.capture()
    }
) {
    Text(text = "Take Screenshot")
}
```

Get `Bitmap` or `ImageBitmap` as

```kotlin
screenshotState.imageBitmap?.let {
    Image(
        modifier = Modifier
            .width(200.dp)
            .height(150.dp),
        bitmap = it,
        contentDescription = null
    )
}
```

initially `Bitmap` is null because `onGloballyPositioned` might not return correct coordinates
initially, experienced this with `Pager` first few calls return incorrect position then actual
position is returned, or sometimes width or height is returned zero, nullable makes sure that you
get the latest one after calling `screenshotState.capture()` from a Composable that is laid out.

### Success or Error State

ImageResult Sealed class return data as Bitmap or Exception if you are interested in displaying
error result if any has occurred

```kotlin
sealed class ImageResult {
    object Initial : ImageResult()
    data class Error(val exception: Exception) : ImageResult()
    data class Success(val data: Bitmap) : ImageResult()
}
```

ImageState of `ScreenshotState` has
`val imageState = mutableStateOf<ImageResult>(ImageResult.Initial)` that can be observed as

```kotlin
when (imageResult) {
    is ImageResult.Success -> {
        Image(bitmap = imageResult.data.asImageBitmap(), contentDescription = null)
    }
    is ImageResult.Error -> {
        Text(text = "Error: ${imageResult.exception.message}")
    }
    else -> {}
}
```

### Periodic Screenshot

Collect `screenshotState.liveScreenshotFlow` to get periodic screenshots of your composables with

```kotlin
LaunchedEffect(Unit) {
    screenshotState.liveScreenshotFlow
        .onEach { bitmap: ImageBitmap ->
            imageBitmap = bitmap
        }
        .launchIn(this)
}
```

## ScreenshotState

Set a delay after each shot by setting `delayInMillis`

```kotlin
/**
 * Create a State of screenshot of composable that is used with that is kept on each recomposition.
 * @param delayInMillis delay before each screenshot if [liveScreenshotFlow] is collected.
 */
@Composable
fun rememberScreenshotState(delayInMillis: Long = 20) = remember {
        ScreenshotState(delayInMillis)
    }

/**
 * State of screenshot of composable that is used with.
 * @param timeInMillis delay before each screenshot if [liveScreenshotFlow] is collected.
 */
class ScreenshotState internal constructor(
    private val timeInMillis: Long = 20,
) {
    val imageState = mutableStateOf<ImageResult>(ImageResult.Initial)

    internal var callback: (() -> Unit)? = null

    /**
     * Captures current state of Composables inside [ScreenshotBox]
     */
    fun capture() {
        callback?.invoke()
    }

    val liveScreenshotFlow = flow {
        while (true) {
            callback?.invoke()
            delay(timeInMillis)
            bitmapState.value?.let {
                emit(it)
            }
        }
    }
        .map {
            it.asImageBitmap()
        }
        .flowOn(Dispatchers.Default)

    internal val bitmapState = mutableStateOf<Bitmap?>(null)

    val bitmap: Bitmap?
        get() = bitmapState.value

    val imageBitmap: ImageBitmap?
        get() = bitmap?.asImageBitmap()
}

```

### Standalone Functions
If you wish to use function instead of `ScreenshotBox` you can use it as

```
val view: View = LocalView.current

val imageResult:ImageResult = view.screenshot(bounds)
```

bounds is Compose rectangle that covers bounds of view that is needed to be screenshow
which should be retrieved using `Modifier.onGloballyPositioned()`

```
Modifier.onGloballyPositioned {
    composableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        it.boundsInWindow()
    } else {
        it.boundsInRoot()
    }
}
```

```kotlin
fun View.screenshot(
    bounds: Rect
): ImageResult {

    try {

        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Above Android O not using PixelCopy throws exception
            // https://stackoverflow.com/questions/58314397/java-lang-illegalstateexception-software-rendering-doesnt-support-hardware-bit
            PixelCopy.request(
                (this.context as Activity).window,
                bounds.toAndroidRect(),
                bitmap,
                {},
                Handler(Looper.getMainLooper())
            )
        } else {
            val canvas = Canvas(bitmap)
                .apply {
                    translate(-bounds.left, -bounds.top)
                }
            this.draw(canvas)
            canvas.setBitmap(null)
        }
        return ImageResult.Success(bitmap)
    } catch (e: Exception) {
        return ImageResult.Error(e)
    }
}
```