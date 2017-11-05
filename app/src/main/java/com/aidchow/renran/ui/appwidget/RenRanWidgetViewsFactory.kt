package com.aidchow.renran.ui.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.realm.RealmHelper
import com.aidchow.renran.utils.Utils
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort

/**
 * Created by aidchow on 17-6-11.
 */
class RenRanWidgetViewsFactory(val context: Context, val intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    var mAppWidgetId: Int = 0
    var results: MutableList<Schedule>? = null

    init {
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
    }

    override fun onCreate() {
        val rlm = Realm.getInstance(RealmConfiguration.Builder()
                .name(RealmHelper.realmName)
                .build())
        results = rlm?.copyFromRealm(
                rlm.where((Schedule::class.java))
                        .equalTo("isDelete", false)
                        .findAllSorted("date"))
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        val rlm = Realm.getInstance(RealmConfiguration.Builder()
                .name(RealmHelper.realmName)
                .build())
        results = rlm?.copyFromRealm(
                rlm.where((Schedule::class.java))
                        .equalTo("isDelete", false)
                        .findAllSorted("date"))
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position >= count) {
            return loadingView
        }
        val rv = RemoteViews(context.packageName, R.layout.renran_wiget_item)

        val schedule: Schedule? = results?.get(position)
        val day = Utils.getDay(schedule!!.date)
        schedule.let {

            rv.setTextViewText(R.id.tv_widget_schedule_text,
                    Utils.formatDescription(context, day, schedule.description!!))
            rv.setTextViewText(R.id.tv_widget_day, formatDay(context, day))

            val intent = Intent()
            rv.setOnClickFillInIntent(R.id.relative_widget, intent)
        }


        return rv

    }

    fun formatDay(context: Context, day: Long): SpannableString? {
        var textDay: String? = null
        val dayStr: String = Math.abs(day).toString()
        textDay = if (day == 0L) {
            context.getString(R.string.today)
        } else {
            context.getString(R.string.day)?.format(dayStr)
        }
        val spanString = SpannableString(textDay)
        spanString.setSpan(ForegroundColorSpan(context.resources!!.getColor(android.R.color.black)),
                0, dayStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanString.setSpan(ForegroundColorSpan(context.resources!!.getColor(android.R.color.darker_gray)),
                spanString.length - 1, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spanString
    }


    override fun getCount(): Int {
        return results!!.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        results!!.clear()
    }


}