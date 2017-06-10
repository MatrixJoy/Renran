package com.aidchow.renran.addschedule

import com.aidchow.renran.BasePresenter
import com.aidchow.renran.BaseView

/**
 * Created by aidchow on 17-6-10.
 */
interface AddScheduleContract {
    interface Presenter : BasePresenter {
        fun saveSchedule(description: String?, imagePath: String?, date: Long, scheduleId: String? = null)

        fun populateSchedule()
    }

    interface View : BaseView<Presenter> {
        fun setImage(imagePath: String?)

        fun setDescription(description: String)

        fun setDate(date: Long)

        fun setScheduleId(scheduleId: String?)

        fun showSchedules()

        fun isActive(): Boolean

        fun showEmptyScheduleError()
    }

}