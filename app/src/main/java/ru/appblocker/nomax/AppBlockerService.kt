package ru.appblocker.nomax

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AppBlockerService : AccessibilityService() {

    companion object {
        const val TAG = "AppBlocker"
    }

    private val blockedPackage = "ru.oneme.app"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Log.d(TAG, "Событие: ${event.eventType}, Пакет: ${event.packageName}")

            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val packageName = it.packageName?.toString()
                Log.d(TAG, "Проверка пакета: $packageName")

                if (packageName == blockedPackage) {
                    Log.d(TAG, "🚫 БЛОКИРОВКА: Обнаружен $blockedPackage")

                    // Немедленная блокировка
                    performGlobalAction(GLOBAL_ACTION_BACK)

                    // Дополнительные действия для надежности
                    Thread.sleep(100)
                    performGlobalAction(GLOBAL_ACTION_HOME)

                    // Показываем экран блокировки
                    showBlockScreen()
                }
            }
        }
    }

    private fun showBlockScreen() {
        try {
            val intent = Intent(this, BlockActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d(TAG, "✅ Экран блокировки показан")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка показа экрана блокировки: ${e.message}")
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Служба прервана")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ Служба подключена!")

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }

        this.serviceInfo = info
        Log.d(TAG, "Конфигурация службы установлена")
    }
}