package com.aidchow.renran.ui.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.aidchow.renran.R
import com.aidchow.renran.schedules.MainActivity

/**
 * Created by aidchow on 17-6-11.
 */
class RenRanAppWidgetProvider : AppWidgetProvider() {
    companion object {
        val REFRESH_ACTION = "com.aidchow.renran.WIDGET.REFRESH_ACTION"

        fun getRefreshBroadcastIntent(context: Context?): Intent {
            return Intent(REFRESH_ACTION)
                    .setComponent(ComponentName(context, RenRanAppWidgetProvider::class.java))
        }
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        for (i in appWidgetIds?.indices!!) {

            val intent = Intent(context, RenRanWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val rv = RemoteViews(context?.packageName, R.layout.renran_wiget_layout)

            rv.setRemoteAdapter(R.id.widget_list_view, intent)
            rv.setEmptyView(R.id.widget_list_view, R.id.tv_empty_view)

            val itemIntent = Intent(context, MainActivity::class.java)
            itemIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val itemPendingIntent = PendingIntent.getActivity(context, 0, itemIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT)

            rv.setPendingIntentTemplate(R.id.widget_list_view, itemPendingIntent)
            rv.setOnClickPendingIntent(R.id.tv_empty_view, itemPendingIntent)

            appWidgetManager?.updateAppWidget(appWidgetIds[i], rv)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
              super.onReceive(context, intent)

        if (REFRESH_ACTION == intent?.action) {
            val amg = AppWidgetManager.getInstance(context)
            val name = ComponentName(context, RenRanAppWidgetProvider::class.java)
            amg.notifyAppWidgetViewDataChanged(amg.getAppWidgetIds(name), R.id.widget_list_view)
        }

    }


}