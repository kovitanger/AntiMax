package ru.appblocker.nomax

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdaptiveAppBlockerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var serviceStatus by remember { mutableStateOf("Проверка...") }
    var statusColor by remember { mutableStateOf(Color.Gray) }
    var isLoading by remember { mutableStateOf(true) }

    // Рассчитываем адаптивные размеры на основе плотности и размера экрана
    val adaptivePadding = calculateAdaptivePadding(configuration)
    val adaptiveSpacing = calculateAdaptiveSpacing(configuration)
    val buttonHeight = calculateButtonHeight(configuration)

    // Автоматическая проверка статуса службы
    LaunchedEffect(Unit) {
        while (true) {
            val isEnabled = isAccessibilityServiceEnabled(context)
            serviceStatus = if (isEnabled) {
                statusColor = Color(0xFF4CAF50)
                "✅ Служба активна"
            } else {
                statusColor = Color(0xFFF44336)
                "❌ Служба отключена"
            }
            isLoading = false
            delay(2000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(adaptivePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = "AntiMax",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(adaptiveSpacing.large))

        // Статус службы
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(adaptiveSpacing.medium))
                Text(
                    "Проверка статуса...",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = serviceStatus,
                style = MaterialTheme.typography.headlineSmall,
                color = statusColor,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(adaptiveSpacing.large))

        // Информация о блокировке
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(adaptiveSpacing.medium)
            ) {
                Text(
                    text = "Информация о блокировке",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(adaptiveSpacing.small))
                Text("• Блокируемое приложение: ru.oneme.app")
                Text("• Служба: Accessibility Service")
                Text("• Статус: ${if (serviceStatus.contains("✅")) "Активна" else "Требуется настройка"}")
            }
        }

        Spacer(modifier = Modifier.height(adaptiveSpacing.large))

        // Кнопка включения службы
        Button(
            onClick = {
                try {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    Log.d("MainActivity", "Открыты настройки доступности")
                } catch (e: Exception) {
                    Log.e("MainActivity", "Ошибка открытия настроек: ${e.message}")
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    context.startActivity(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Включить службу доступности",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(adaptiveSpacing.medium))

        // Инструкция
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(adaptiveSpacing.medium)
            ) {
                Text(
                    text = "Инструкция по настройке:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(adaptiveSpacing.small))
                Text(
                    text = "1. Нажмите кнопку 'Включить службу доступности'\n" +
                            "2. В настройках найдите 'AntiMax'\n" +
                            "3. Включите переключатель напротив 'AntiMax'\n" +
                            "4. Вернитесь в это приложение\n" +
                            "5. Статус должен измениться на '✅ Служба активна'\n" +
                            "6. Теперь при запуске ru.oneme.app оно будет блокироваться",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.height(adaptiveSpacing.medium))

        // Кнопка обновления статуса
        Button(
            onClick = {
                val isEnabled = isAccessibilityServiceEnabled(context)
                serviceStatus = if (isEnabled) {
                    statusColor = Color(0xFF4CAF50)
                    "✅ Служба активна"
                } else {
                    statusColor = Color(0xFFF44336)
                    "❌ Служба отключена"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Обновить статус")
        }

        Spacer(modifier = Modifier.height(adaptiveSpacing.medium))

        Text(
            text = "Для отладки откройте Logcat в Android Studio и отфильтруйте по 'AppBlocker'",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Класс для хранения адаптивных отступов
data class AdaptiveSpacing(
    val small: Dp,
    val medium: Dp,
    val large: Dp
)

// Функция для расчета адаптивных отступов на основе плотности и размера экрана
@Composable
fun calculateAdaptivePadding(configuration: android.content.res.Configuration): Dp {
    val screenWidth = configuration.screenWidthDp
    val density = configuration.densityDpi

    return when {
        screenWidth < 360 -> 12.dp // Маленькие экраны
        screenWidth < 600 -> 16.dp // Средние экраны
        else -> 24.dp // Большие экраны и планшеты
    }
}

// Функция для расчета адаптивных промежутков
@Composable
fun calculateAdaptiveSpacing(configuration: android.content.res.Configuration): AdaptiveSpacing {
    val screenWidth = configuration.screenWidthDp
    val density = configuration.densityDpi

    return when {
        screenWidth < 360 -> AdaptiveSpacing(
            small = 8.dp,
            medium = 12.dp,
            large = 16.dp
        )
        screenWidth < 600 -> AdaptiveSpacing(
            small = 12.dp,
            medium = 16.dp,
            large = 24.dp
        )
        else -> AdaptiveSpacing(
            small = 16.dp,
            medium = 24.dp,
            large = 32.dp
        )
    }
}

// Функция для расчета высоты кнопок
@Composable
fun calculateButtonHeight(configuration: android.content.res.Configuration): Dp {
    val screenHeight = configuration.screenHeightDp
    val density = configuration.densityDpi

    return when {
        screenHeight < 600 -> 48.dp // Маленькие экраны
        screenHeight < 800 -> 52.dp // Средние экраны
        else -> 56.dp // Большие экраны
    }
}

// Функция проверки статуса службы доступности
private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    return try {
        val accessibilityEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        ) == 1

        if (!accessibilityEnabled) return false

        val serviceName = "ru.appblocker.nomax/ru.appblocker.nomax.AppBlockerService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""

        Log.d("MainActivity", "Доступные службы: $enabledServices")
        enabledServices.contains(serviceName)
    } catch (e: Exception) {
        Log.e("MainActivity", "Ошибка проверки службы: ${e.message}")
        false
    }
}

@Composable
fun AdaptiveAppBlockerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            secondary = Color(0xFF625B71),
            tertiary = Color(0xFF7D5260),
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F),
        ),
        typography = MaterialTheme.typography.copy(
            bodySmall = MaterialTheme.typography.bodySmall.copy(
                lineHeight = androidx.compose.ui.unit.TextUnit(18f, androidx.compose.ui.unit.TextUnitType.Sp)
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp)
            )
        ),
        content = content
    )
}

// Preview для разных размеров экрана
@Preview(showBackground = true, widthDp = 360, heightDp = 640) // Маленький экран
@Composable
fun MainScreenSmallPreview() {
    AdaptiveAppBlockerTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, widthDp = 411, heightDp = 731) // Средний экран
@Composable
fun MainScreenMediumPreview() {
    AdaptiveAppBlockerTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 1024) // Большой экран/планшет
@Composable
fun MainScreenLargePreview() {
    AdaptiveAppBlockerTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, widthDp = 834, heightDp = 1194) // Планшет
@Composable
fun MainScreenTabletPreview() {
    AdaptiveAppBlockerTheme {
        MainScreen()
    }
}