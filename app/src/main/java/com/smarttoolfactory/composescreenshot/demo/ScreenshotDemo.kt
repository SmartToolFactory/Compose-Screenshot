package com.smarttoolfactory.composescreenshot.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.ScreenshotState
import com.smarttoolfactory.screenshot.rememberScreenshotState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotDemo() {

    val screenshotState = rememberScreenshotState()
    var showDialog by remember { mutableStateOf(false) }
    val imageResult: ImageResult = screenshotState.imageState.value

    // Show dialog only when ImageResult is success or error
    LaunchedEffect(key1 = imageResult){
        if (imageResult is ImageResult.Success || imageResult is ImageResult.Error){
            showDialog = true
        }
    }

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

           if (showDialog) {
               ImageAlertDialog(imageResult = imageResult) {
                   showDialog = false
               }
           }
        }
    )
}

@Composable
private fun ScreenshotSample(screenshotState: ScreenshotState, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.background(Color(0xffECEFF1))) {

        Spacer(modifier = Modifier.height(30.dp))

        ScreenshotBox(
            modifier = Modifier.fillMaxSize(),
            screenshotState = screenshotState
        ) {
            LazyVerticalGrid(
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(3),
                content = {
                    items(snacks) { snack: Snack ->
                        GridSnackCard(snack = snack)
                    }
                }
            )
        }
    }
}

@Composable
private fun ImageAlertDialog(imageResult: ImageResult, onDismiss: () -> Unit) {
    androidx.compose.material.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(onClick = { onDismiss() }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            FilledTonalButton(onClick = { onDismiss() }) {
                Text(text = "Dismiss")
            }
        },
        text = {
            when (imageResult) {
                is ImageResult.Success -> {
                    Image(bitmap = imageResult.data.asImageBitmap(), contentDescription = null)
                }
                is ImageResult.Error -> {
                    Text(text = "Error: ${imageResult.exception.message}")
                }
                else -> {}
            }
        })
}
