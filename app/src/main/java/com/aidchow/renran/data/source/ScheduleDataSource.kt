package com.aidchow.renran.data.source

import com.aidchow.renran.data.Schedule

/**
 * Created by aidchow on 17-6-8.
 */

interface ScheduleDataSource {
    interface LoadSchedulesCallback {
        fun onScheduleLoaded(schedules: MutableList<Schedule>)

        fun onDataNotAvailable()
    }

    interface GetScheduleCallback {
        fun onScheduleLoaded(schedule: Schedule)

        fun onDataNotAvailable()
    }


    fun getSchedules(loadSchedulesCallback: LoadSchedulesCallback)

    fun getSchedule(scheduleId: String, scheduleCallback: GetScheduleCallback)

    fun saveSchedule(schedule: Schedule)

    fun updateSchedule(schedule: Schedule)

    fun deleteSchedule(schedule: Schedule)


}
