package com.bazemotion

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                BazeMotionApp()
            }
        }
    }
}

@Composable
fun BazeMotionApp() {

    val context = LocalContext.current

    var selectedVideoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var videoName by remember {
        mutableStateOf<String?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        selectedVideoUri = uri

        uri?.let {
            val cursor = context.contentResolver.query(
                it,
                null,
                null,
                null,
                null
            )

            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val index =
                        c.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    if (index >= 0) {
                        videoName = c.getString(index)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🎬 Baze Motion",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Видео редактор (альфа)"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                launcher.launch("video/*")
            }
        ) {
            Text("📁 Выбрать видео")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (videoName != null) {

            Text(
                text = "Выбрано:"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = videoName!!
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { }
            ) {
                Text("➡ Далее")
            }
        }
    }
}
