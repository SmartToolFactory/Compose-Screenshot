package com.smarttoolfactory.composescreenshot.demo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composescreenshot.R
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlinx.coroutines.delay

@Composable
fun ScreenshotDemo2() {

    var counter by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            counter++
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {

        ScreenshotSample1(counter = counter)
        Spacer(modifier = Modifier.width(10.dp))
        ScreenshotSample2()
    }

}

@Composable
private fun ScreenshotSample1(counter: Int) {

    val screenshotState = rememberScreenshotState()

    var progress by remember { mutableStateOf(0f) }

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

    Spacer(modifier = Modifier.width(10.dp))

    ElevatedButton(
        onClick = {
            screenshotState.capture()
        }
    ) {
        Text(text = "Take Screenshot")
    }

    Spacer(modifier = Modifier.width(10.dp))

    Text(
        text = "Screenshot of State1",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(8.dp)
    )

    screenshotState.imageBitmap?.let {
        Image(
            modifier = Modifier
                .width(200.dp)
                .height(150.dp),
            bitmap = it,
            contentDescription = null
        )
    }

}

@Composable
private fun ScreenshotSample2() {

    val screenshotState = rememberScreenshotState()

    Text("ScreenshotBox at the right edge")
    Spacer(modifier = Modifier.width(10.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        ElevatedButton(
            onClick = {
                screenshotState.capture()
            }
        ) {
            Text(text = "Take Screenshot")
        }

        Spacer(modifier = Modifier.width(4.dp))
        ScreenshotBox(screenshotState = screenshotState) {
            Column(
                modifier = Modifier
                    .border(2.dp, Color.Red)
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
            }
        }
    }

    Text(
        text = "Screenshot of State2",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(8.dp)
    )


    println("DEMO image: ${screenshotState.imageBitmap}")

    screenshotState.imageBitmap?.let {
        Image(
            modifier = Modifier
                .background(Color.LightGray)
                .width(200.dp)
                .height(150.dp),
            bitmap = it,
            contentDescription = null
        )
    }
}
