package com.aidchow.renran.ui.appwidget

import android.content.Intent
import android.widget.RemoteViewsService

/**
 * Created by aidchow on 17-6-11.
 */
class RenRanWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return RenRanWidgetViewsFactory(this.applicationContext,intent!!)
    }

}