package com.aidchow.renran.data.source

import com.aidchow.renran.data.Schedule

/**
 * Created by aidchow on 17-6-8.
 */

class ScheduleRepository private constructor(private val mScheduleLocalDataSource: ScheduleDataSource)

    : ScheduleDataSource {

    internal var mCacheSchedules: MutableMap<String, Schedule>? = null

    companion object {
        private var INSTANCE: ScheduleRepository? = null

        fun getInstance(scheduleLocalDataSource: ScheduleDataSource): ScheduleRepository {
            if (INSTANCE == null) {
                INSTANCE = ScheduleRepository(scheduleLocalDataSource)
            }
            return INSTANCE as ScheduleRepository
        }

        /**
         * Used to force [.getInstance] to create a new instance
         * next time it's called.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getSchedules(loadSchedulesCallback: ScheduleDataSource.LoadSchedulesCallback) {
        if (mCacheSchedules != null) {
            loadSchedulesCallback.onScheduleLoaded(ArrayList<Schedule>(mCacheSchedules?.values))
            return
        }
        mScheduleLocalDataSource.getSchedules(object : ScheduleDataSource.LoadSchedulesCallback {
            override fun onScheduleLoaded(schedules: MutableList<Schedule>) {
                if (mCacheSchedules == null) {
                    mCacheSchedules = LinkedHashMap<String, Schedule>()
                }
                mCacheSchedules?.clear()
                for (s: Schedule in schedules) {
                    s.scheduleID?.let { mCacheSchedules?.put(it, s) }
                }
                loadSchedulesCallback.onScheduleLoaded(schedules)
            }

            override fun onDataNotAvailable() {
                loadSchedulesCallback.onDataNotAvailable()
            }
        })

    }

    override fun getSchedule(scheduleId: String, scheduleCallback: ScheduleDataSource.GetScheduleCallback) {
        if (mCacheSchedules != null) {
            scheduleCallback.onScheduleLoaded(mCacheSchedules?.get(scheduleId)!!)
            return
        }
        mScheduleLocalDataSource.getSchedule(scheduleId, object : ScheduleDataSource.GetScheduleCallback {
            override fun onScheduleLoaded(schedule: Schedule) {
                if (mCacheSchedules == null) {
                    mCacheSchedules = LinkedHashMap<String, Schedule>()
                }
                mCacheSchedules?.put(schedule.scheduleID!!, schedule)
                scheduleCallback.onScheduleLoaded(schedule)
            }

            override fun onDataNotAvailable() {
                scheduleCallback.onDataNotAvailable()
            }
        })

    }

    override fun saveSchedule(schedule: Schedule) {
        mScheduleLocalDataSource.saveSchedule(schedule)
        if (mCacheSchedules == null) {
            mCacheSchedules = LinkedHashMap<String, Schedule>()
        }
        schedule.scheduleID?.let { mCacheSchedules?.put(it, schedule) }
    }


    override fun updateSchedule(schedule: Schedule) {
        mScheduleLocalDataSource.updateSchedule(schedule)
        if (mCacheSchedules == null) {
            mCacheSchedules = LinkedHashMap<String, Schedule>()
        }
        mCacheSchedules?.put(schedule.scheduleID!!, schedule)
    }

    override fun deleteSchedule(schedule: Schedule) {
        mScheduleLocalDataSource.deleteSchedule(schedule)
        if (mCacheSchedules != null) {
            mCacheSchedules?.remove(schedule.scheduleID)
        }
    }
}
