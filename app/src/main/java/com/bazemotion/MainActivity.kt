package com.bazemotion

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

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
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    
    val tabs = listOf(
        TabItem("Редактор", Icons.Filled.Edit),
        TabItem("Проекты", Icons.Filled.Folder),
        TabItem("Настройки", Icons.Filled.Settings)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> EditorScreen()
                1 -> ProjectsScreen()
                2 -> SettingsScreen()
            }
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun EditorScreen() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text("🎬 Baze Motion", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Видео редактор\n(альфа версия)", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            Toast.makeText(context, "Выбор видео (будет позже)", Toast.LENGTH_SHORT).show()
        }) {
            Text("Импортировать видео")
        }
    }
}

@Composable
fun ProjectsScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text("📁 Мои проекты", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Здесь будут сохранённые проекты", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SettingsScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text("⚙️ Настройки", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Офлайн режим: включен\nНикаких Telegram-блокировок", style = MaterialTheme.typography.bodyLarge)
    }
}
