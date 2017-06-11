package com.aidchow.renran.ui.appwidget

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
class RenRanWidgetViewsFactory(val context: Context) : RemoteViewsService.RemoteViewsFactory {


    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.renran_wiget_item)
        val rlm = Realm.getInstance(RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name(RealmHelper.realmName)
                .build())
        val results = rlm?.copyFromRealm(
                rlm.where((Schedule::class.java))
                        .equalTo("isDelete", false)
                        .equalTo("isShowOnScreen", true)
                        .findAllSorted("date"))
        val schedule: Schedule? = results?.get(position)
        val day = (System.currentTimeMillis().div(1000) - schedule!!.date).div(86400)
        schedule.let {
            rv.setTextViewText(R.id.tv_widget_schedule_text,
                    Utils.formatDescription(context, day, schedule.description!!))
            rv.setTextViewText(R.id.tv_widget_day, formateDay(context, day))

            val intent = Intent()
            rv.setOnClickFillInIntent(R.id.relative_widget, intent)
        }

        return rv

    }

    private fun formateDay(context: Context, day: Long): SpannableString? {
        val textDay = context.getString(R.string.day)?.format(Math.abs(day))

        val spanString = SpannableString(textDay)

        val size: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 48.0f,
                context.resources?.displayMetrics).toInt()

        spanString.setSpan(AbsoluteSizeSpan(size), 0, spanString.length - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanString.setSpan(ForegroundColorSpan(context.resources!!.getColor(android.R.color.darker_gray)),
                spanString.length - 1, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spanString
    }

    override fun getCount(): Int {
        return Realm.getInstance(RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name(RealmHelper.realmName)
                .build())
                .where(Schedule::class.java)
                .equalTo("isDelete", false)
                .equalTo("isShowOnScreen", true)
                .findAllSorted("date", Sort.ASCENDING)
                .size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {

    }

}