package com.smarttoolfactory.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView

/**
 * A composable that gets screenshot of Composable that is in [content].
 * @param screenshotState state of screenshot that contains [Bitmap].
 * @param content Composable that will be captured to bitmap on action or periodically.
 */
@Composable
fun ScreenshotBox(
    modifier: Modifier = Modifier,
    screenshotState: ScreenshotState,
    content: @Composable () -> Unit,
) {
    val view: View = LocalView.current
    val window: Window = (view.context as Activity).window

    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    DisposableEffect(Unit) {

        var bitmap: Bitmap? = null

        screenshotState.callback = {
            composableBounds?.let { bounds ->

                if (bounds.width == 0f || bounds.height == 0f) return@let
                bitmap = captureView(view, window, bounds)
                captureView(view, window, bounds) {

                }
            }
            bitmap
        }

        onDispose {
            println("✊ ScreenshotBox onDispose(), bitmap: ${bitmap.hashCode()}")
            bitmap?.apply {
                if (!isRecycled) {
                    recycle()
                    bitmap = null
                }
            }
            screenshotState.callback = null
        }
    }

    Box(modifier = modifier
        .onGloballyPositioned {
            composableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.boundsInWindow()
            } else {
                it.boundsInRoot()
            }
        }
    ) {
        content()
    }
}

private fun captureView(
    view: View,
    window: Window,
    bounds: Rect,
): Bitmap {

    val bitmap = Bitmap.createBitmap(
        bounds.width.toInt(),
        bounds.height.toInt(),
        Bitmap.Config.ARGB_8888,
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Above Android O, use PixelCopy
        PixelCopy.request(
            window,
            bounds.toAndroidRect(),
            bitmap,
            {

            },
            Handler(Looper.getMainLooper())
        )
    } else {

        val canvas = Canvas(bitmap)
            .apply {
                translate(-bounds.left, -bounds.top)
            }
        view.draw(canvas)
        canvas.setBitmap(null)
    }

    return bitmap
}


private fun captureView(
    view: View,
    window: Window,
    bounds: Rect,
    bitmapCallback: (Result<Bitmap>) -> Unit
) {

    try {
        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Above Android O, use PixelCopy
            PixelCopy.request(
                window,
                bounds.toAndroidRect(),
                bitmap,
                {
                    if (it == PixelCopy.SUCCESS) {
                        bitmapCallback.invoke(Result.success(bitmap))
                    } else {
                        Result.failure<Bitmap>(Exception())
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } else {

            val canvas = Canvas(bitmap)
                .apply {
                    translate(-bounds.left, -bounds.top)
                }
            view.draw(canvas)
            canvas.setBitmap(null)
            bitmapCallback.invoke(Result.success(bitmap))
        }
    } catch (e: Exception) {
        bitmapCallback.invoke(Result.failure(e))
    }
}
