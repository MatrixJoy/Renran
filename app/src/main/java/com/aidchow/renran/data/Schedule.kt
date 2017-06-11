package com.aidchow.renran.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by aidchow on 17-6-8.
 * Schedule bean
 */

open class Schedule(


        var imagePath: String? = null, // schedule image

        var description: String? = null, //  schedule description

        var date: Long = 0, //  schedule execute date

        var isDelete: Boolean = false // schedule is delete
) : RealmObject() {

    @PrimaryKey var scheduleID: String? = UUID.randomUUID().toString()

    var isShowOnScreen: Boolean? = false
    override fun toString(): String {
        return "Schedule(imagePath=$imagePath, description=$description, date=$date, isDelete=$isDelete, scheduleID=$scheduleID, isShowOnScreen=$isShowOnScreen)"
    }

}
