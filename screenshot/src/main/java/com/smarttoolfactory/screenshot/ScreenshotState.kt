package com.smarttoolfactory.screenshot

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

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
    private val timeInMillis: Long = 20
) {
    internal var callback: (() -> Bitmap?)? = null

    private val bitmapState = mutableStateOf(callback?.invoke())

    /**
     * Captures current state of Composables inside [ScreenshotBox]
     */
    fun capture() {
        bitmapState.value = callback?.invoke()
    }

    val liveScreenshotFlow = flow {
        while (true) {
            val bmp = callback?.invoke()
            bmp?.let {
                emit(it)
            }
            delay(timeInMillis)
        }
    }

    val bitmap: Bitmap?
        get() = bitmapState.value

    val imageBitmap: ImageBitmap?
        get() = bitmap?.asImageBitmap()
}
