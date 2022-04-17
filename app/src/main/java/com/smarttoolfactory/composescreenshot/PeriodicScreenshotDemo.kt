package com.smarttoolfactory.composescreenshot

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun PeriodicScreenshotDemo() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        val screenshotState = rememberScreenshotState()

        var progress1 by remember { mutableStateOf(0f) }
        var progress2 by remember { mutableStateOf(0f) }
        var progress3 by remember { mutableStateOf(0f) }

        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

        var counter by remember {
            mutableStateOf(0)
        }

        LaunchedEffect(Unit) {
            screenshotState.liveScreenshotFlow.onEach {
                imageBitmap = it.asImageBitmap()
            }.launchIn(this)
        }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                counter++
            }
        }

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

                Slider(value = progress1, onValueChange = { progress1 = it })
                Slider(value = progress2, onValueChange = { progress2 = it })
                Slider(value = progress3, onValueChange = { progress3 = it })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Live Screenshot",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp)
        )

        imageBitmap?.let { bmp ->
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp),
                bitmap = bmp,
                contentDescription = null
            )
        }
    }
}