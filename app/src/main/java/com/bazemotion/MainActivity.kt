package com.bazemotion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class ProjectItem(
    val name: String,
    val resolution: String,
    val fps: Int
)

@Composable
fun BazeMotionApp() {

    val projects = remember {
        mutableStateListOf(
            ProjectItem("Demo Project", "1920x1080", 30)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🎬 Baze Motion",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Video Editor Alpha"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Button(
                onClick = {
                    projects.add(
                        ProjectItem(
                            "New Project ${projects.size + 1}",
                            "1920x1080",
                            30
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("➕ Новый проект")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Мои проекты",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(projects) { project ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Разрешение: ${project.resolution}"
                        )

                        Text(
                            text = "FPS: ${project.fps}"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                            }
                        ) {
                            Text("Открыть")
                        }
                    }
                }
            }
        }

        NavigationBar {

            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = { Text("🏠") },
                label = { Text("Проекты") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Text("🎞") },
                label = { Text("Редактор") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Text("📤") },
                label = { Text("Экспорт") }
            )
        }
    }
}
