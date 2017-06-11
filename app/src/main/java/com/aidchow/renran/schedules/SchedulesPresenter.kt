package com.aidchow.renran.schedules

import android.app.Activity.RESULT_OK
import com.aidchow.renran.addschedule.AddScheduleActivity
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.data.source.ScheduleDataSource
import com.aidchow.renran.data.source.ScheduleRepository

/**
 * Created by aidchow on 17-6-8.
 */
class SchedulesPresenter(val scheduleRepository: ScheduleRepository,
                         val view: SchedulesContract.View)
    : SchedulesContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadSchedules()
    }

    override fun loadSchedules() {
        scheduleRepository.getSchedules(object : ScheduleDataSource.LoadSchedulesCallback {
            override fun onScheduleLoaded(schedules: MutableList<Schedule>) {
                view.showSchedules(schedules)
                if (schedules.size == 0) {
                    view.noSchedules()
                }
            }

            override fun onDataNotAvailable() {
                view.noSchedules()
            }
        })
    }

    override fun modifySchedules(scheduleId: String) {
        view.showModifySchedulesUi(scheduleId)
    }

    override fun deleteSchedules(schedule: Schedule) {
        scheduleRepository.deleteSchedule(schedule)
        view.setShowOnscreen()

    }

    override fun addNewSchedules() {
        view.showAddNewScheduleUi()
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (AddScheduleActivity.ADD_SCHEDULE_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            view.showAddSuccessView()
        }
    }

    override fun showOnScreen(schedule: Schedule) {
        scheduleRepository.updateSchedule(schedule)
        view.setShowOnscreen()

    }
}