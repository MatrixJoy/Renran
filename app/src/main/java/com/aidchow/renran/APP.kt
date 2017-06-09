package com.aidchow.renran

import android.app.Application

import io.realm.Realm

/**
 * Created by aidchow on 17-6-7.
 */

class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
