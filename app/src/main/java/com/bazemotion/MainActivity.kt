package com.bazemotion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private var selectedVideoUri: Uri? = null

    private val pickVideoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            Toast.makeText(this, "Видео выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BazeMotionApp()
            }
        }
    }

    @Composable
    fun BazeMotionApp() {
        val context = LocalContext.current
        var videoName by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(selectedVideoUri) {
            selectedVideoUri?.let { uri ->
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        videoName = it.getString(nameIndex)
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
            Text("🎬 Baze Motion", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Видео редактор (альфа)", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                pickVideoLauncher.launch("video/*")
            }) {
                Text("📁 Выбрать видео")
            }
            Spacer(modifier = Modifier.height(16.dp))
            videoName?.let {
                Text("Выбрано: $it", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    Toast.makeText(context, "Дальше: работа с видео", Toast.LENGTH_SHORT).show()
                    // Следующий шаг: превью/обрезка
                }) {
                    Text("➡ Далее")
                }
            }
        }
    }
}
