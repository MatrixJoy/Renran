package com.aidchow.renran.data.source.local

import com.aidchow.renran.data.Schedule
import com.aidchow.renran.data.source.ScheduleDataSource
import com.aidchow.renran.realm.RealemHelper
import io.realm.RealmResults

/**
 * Created by aidchow on 17-6-8.
 */

class ScheduleLocalDataSource private constructor() : ScheduleDataSource {

    companion object {
        private var INSTANCE: ScheduleLocalDataSource? = null
        fun getInstance(): ScheduleLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = ScheduleLocalDataSource()
            }
            return INSTANCE as ScheduleLocalDataSource
        }
    }

    init {
        saveSchedule(Schedule("/ssss", "计算机网络考试", 1497974400))
        saveSchedule(Schedule("/ssss", "四六级考试", 1497628800))
        saveSchedule(Schedule("/ssss", "她生日", 1514908800))
    }

    override fun getSchedules(loadSchedulesCallback: ScheduleDataSource.LoadSchedulesCallback) {
        val rlm = RealemHelper.getRenRanRealmInstance()
        val results: RealmResults<Schedule> = rlm.where(Schedule::class.java).equalTo("isDelete", false).findAll()
        if (results.isEmpty()) {
            loadSchedulesCallback.onDataNotAvailable()
        } else {
            loadSchedulesCallback.onScheduleLoaded(results)
        }
    }

    override fun getSchedule(scheduleId: String, scheduleCallback: ScheduleDataSource.GetScheduleCallback) {
        val rlm = RealemHelper.getRenRanRealmInstance()
        val result = rlm.where(Schedule::class.java).equalTo("scheduleID", scheduleId).findFirst()
        if (result == null) {
            scheduleCallback.onDataNotAvailable()
        } else {
            scheduleCallback.onScheduleLoaded(result)
        }
    }

    /**
     * create or update a schedule
     */
    override fun saveSchedule(schedule: Schedule) {
        val rlm = RealemHelper.getRenRanRealmInstance()
        rlm.beginTransaction()
        rlm.copyToRealmOrUpdate(schedule)
        rlm.commitTransaction()
        rlm.close()
    }

    override fun updateSchedule(schedule: Schedule) {
        saveSchedule(schedule)
    }

    override fun deleteSchedule(schedule: Schedule) {
        val rlm = RealemHelper.getRenRanRealmInstance()
        rlm.beginTransaction()
        schedule.isDelete = true
        rlm.copyToRealmOrUpdate(schedule)
        rlm.commitTransaction()
        rlm.close()
    }
}
