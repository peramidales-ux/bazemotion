package com.bazemotion

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    
    private var inputVideoUri: Uri? = null
    private var outputVideoPath: String? = null
    private var isProcessing by mutableStateOf(false)
    private var processStatus by mutableStateOf("")
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_VIDEO, false) ||
            permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                pickVideo()
            }
            else -> {
                Toast.makeText(this, "Нужно разрешение на чтение видео", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private val pickVideoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            inputVideoUri = it
            processStatus = "Видео выбрано: ${it.lastPathSegment}"
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        setContent {
            MaterialTheme {
                BazeMotionApp(
                    onPickVideo = { pickVideo() },
                    onTrimVideo = { startTrim() },
                    isProcessing = isProcessing,
                    status = processStatus,
                    hasVideo = inputVideoUri != null
                )
            }
        }
    }
    
    private fun checkPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        val needRequest = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (needRequest) {
            requestPermissionLauncher.launch(permissions)
        }
    }
    
    private fun pickVideo() {
        pickVideoLauncher.launch("video/*")
    }
    
    private fun startTrim() {
        val uri = inputVideoUri
        if (uri == null) {
            Toast.makeText(this, "Сначала выберите видео", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Обрезаем первые 3 секунды для примера
        trimVideo(uri, startTime = 3, duration = 5)
    }
    
    private fun trimVideo(uri: Uri, startTime: Int, duration: Int) {
        isProcessing = true
        processStatus = "Обработка видео..."
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Получаем реальный путь к файлу
                val inputPath = getRealPathFromUri(uri)
                if (inputPath == null) {
                    withContext(Dispatchers.Main) {
                        processStatus = "Ошибка: не могу найти файл"
                        isProcessing = false
                    }
                    return@launch
                }
                
                // Создаём выходной файл
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val outputFile = File(cacheDir, "trimmed_$timeStamp.mp4")
                outputVideoPath = outputFile.absolutePath
                
                // FFmpeg команда для обрезки
                val command = arrayOf(
                    "-i", inputPath,
                    "-ss", startTime.toString(),
                    "-t", duration.toString(),
                    "-c", "copy",
                    outputFile.absolutePath
                ).joinToString(" ")
                
                val session = FFmpegKit.execute(command)
                
                withContext(Dispatchers.Main) {
                    if (ReturnCode.isSuccess(session.returnCode)) {
                        processStatus = "✅ Обрезка завершена! Видео сохранено"
                        Toast.makeText(this@MainActivity, "Видео обрезано!", Toast.LENGTH_LONG).show()
                        saveToGallery(outputFile)
                    } else {
                        processStatus = "❌ Ошибка: ${session.failStackTrace}"
                    }
                    isProcessing = false
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    processStatus = "Ошибка: ${e.message}"
                    isProcessing = false
                }
            }
        }
    }
    
    private fun getRealPathFromUri(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val tempFile = File(cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun saveToGallery(file: File) {
        try {
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/BazeMotion")
                }
            }
            
            val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Toast.makeText(this, "Сохранено в галерею", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun BazeMotionApp(
    onPickVideo: () -> Unit,
    onTrimVideo: () -> Unit,
    isProcessing: Boolean,
    status: String,
    hasVideo: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎬 Baze Motion", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Видео редактор на FFmpeg", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onPickVideo,
            enabled = !isProcessing
        ) {
            Text("📁 Выбрать видео")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onTrimVideo,
            enabled = hasVideo && !isProcessing,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("✂️ Обрезать (3-8 сек)")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isProcessing) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (status.isNotEmpty()) {
            Card(
                modifier = Modifier.padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
