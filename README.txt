========== BAZE MOTION ==========
Офлайн видео-редактор
Никаких Telegram-блокировок
Версия: 0.1-alpha

=== Как собрать APK (нужен Android SDK) ===
1. Установите Android Studio на ПК
2. Откройте папку BazeMotion как проект
3. Build → Build APK

=== Или используйте онлайн-сборку ===
1. Залейте папку BazeMotion на GitHub
2. Настройте GitHub Actions
3. Скачайте готовый APK

=== Структура проекта ===
BazeMotion/
├── app/
│   ├── src/main/
│   │   ├── java/com/bazemotion/MainActivity.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts

=== Следующие шаги ===
- Добавить импорт видео через MediaStore
- Реализовать FFmpeg команды
- Создать таймлайн
- Добавить эффекты
