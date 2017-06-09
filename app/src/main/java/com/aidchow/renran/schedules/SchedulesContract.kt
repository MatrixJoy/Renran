package com.aidchow.renran.schedules

import com.aidchow.renran.BasePresenter
import com.aidchow.renran.BaseView
import com.aidchow.renran.data.Schedule

/**
 * Created by aidchow on 17-6-8.
 */
interface SchedulesContract {
    interface View : BaseView<Presenter> {

        fun showSchedules(schedules: MutableList<Schedule>)

        fun showModifySchedulesUi(scheduleId: String)

        fun noSchedules()

        fun showAddNewScheduleUi()

        fun isActive(): Boolean

    }

    interface Presenter : BasePresenter {
        fun loadSchedules()

        fun modifySchedules(scheduleId: String)

        fun deleteSchedules(schedule: Schedule)

        fun addNewSchedules()

    }
}