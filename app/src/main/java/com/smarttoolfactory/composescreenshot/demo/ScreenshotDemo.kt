package com.smarttoolfactory.composescreenshot.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.ScreenshotState
import com.smarttoolfactory.screenshot.rememberScreenshotState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotDemo() {

    val screenshotState = rememberScreenshotState()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                screenshotState.capture()
            }) {
                Text(text = "Capture")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues: PaddingValues ->
            ScreenshotSample(screenshotState, paddingValues)
        }
    )
}

@Composable
private fun ScreenshotSample(screenshotState: ScreenshotState, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
    ) {

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "This is a sample to capture, image from the screen\n" +
                    "using ScreenshotBox and other"
        )
        ScreenshotBox(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .fillMaxWidth()
                .fillMaxHeight(.5f),
            screenshotState = screenshotState
        ) {
            LazyVerticalGrid(
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xffECEFF1)),
                columns = GridCells.Fixed(3),
                content = {
                    items(snacks) { snack: Snack ->
                        GridSnackCard(snack = snack)
                    }
                }
            )
        }

        screenshotState.imageBitmap?.let { imageBitmap: ImageBitmap ->
            Image(bitmap = imageBitmap, contentDescription = null)
        }
    }
}
