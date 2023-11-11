package suyasuya.quicksettingsutil

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class SoundSettingWidget : AppWidgetProvider() {
    private var audioManager: AudioManager? = null  // ホントは lateinit var にしたいが適切な初期化位置が見当たらない

    companion object {
        val AUDIO_FLAGS = AudioManager.FLAG_PLAY_SOUND
//        val AUDIO_FLAGS = AudioManager.FLAG_PLAY_SOUND or AudioManager.FLAG_SHOW_UI
//        val AUDIO_STREAM = AudioManager.STREAM_SYSTEM
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        //Manifestに登録されたActionと発行したインテントが同じ場合に実行される
        if (audioManager == null) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
        val am: AudioManager = audioManager!!
//        val volume = am.getStreamVolume(AUDIO_STREAM)
//        val maxVolume = am.getStreamMaxVolume(AUDIO_STREAM)
        when (intent.action) {
            "volume_down" -> am.adjustVolume(AudioManager.ADJUST_LOWER, AUDIO_FLAGS)
            "volume_up" -> am.adjustVolume(AudioManager.ADJUST_RAISE, AUDIO_FLAGS)
            "android.appwidget.action.APPWIDGET_DISABLED" -> {}
            "android.appwidget.action.APPWIDGET_ENABLED" -> {}
            "android.appwidget.action.APPWIDGET_DELETED" -> {}
            "android.appwidget.action.APPWIDGET_UPDATE" -> {}
            else -> Log.w("Invalid intent action", "action: " + intent.action)
        }
        Log.d("Intent action detected", "action: " + intent.action)
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.sound_setting_widget)

    views.setOnClickPendingIntent(
        R.id.sound_settings_widget_down,
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            Intent(context, SoundSettingWidget::class.java).setAction("volume_down"),
            PendingIntent.FLAG_IMMUTABLE
        )
    )
    views.setOnClickPendingIntent(
        R.id.sound_settings_widget_up,
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            Intent(context, SoundSettingWidget::class.java).setAction("volume_up"),
            PendingIntent.FLAG_IMMUTABLE
        )
    )

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun clamp(value: Int, min: Int, max: Int): Int {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}