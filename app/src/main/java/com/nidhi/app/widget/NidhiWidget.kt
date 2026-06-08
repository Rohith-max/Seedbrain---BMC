package com.nidhi.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nidhi.app.MainActivity
import com.nidhi.app.R

/**
 * NIDHI Home Screen Widget.
 * Shows a summary card: app name + "Tap to open" shortcut.
 * Uses RemoteViews with standard Android layouts (no Compose in widgets).
 *
 * To add the widget: long-press on your Android home screen → Widgets → NIDHI
 */
class NidhiWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { widgetId ->
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_nidhi)

            // Tap the whole widget → open main app
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, widgetId, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }

        /** Call this from anywhere to force-refresh all NIDHI widgets on the home screen */
        fun forceUpdate(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, NidhiWidget::class.java))
            if (ids.isNotEmpty()) {
                val intent = Intent(context, NidhiWidget::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
