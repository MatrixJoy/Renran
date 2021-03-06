package com.aidchow.renran.realm

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by aidchow on 17-6-8.
 */
object RealmHelper {
    val realmName: String = "renran.realm"
    fun getRenRanRealmInstance(): Realm {

        return Realm.getInstance(RealmConfiguration
                .Builder()
                .name(realmName)
                .build())
    }
}