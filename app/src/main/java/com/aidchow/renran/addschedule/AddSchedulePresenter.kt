package com.aidchow.renran.addschedule

import com.aidchow.renran.data.Schedule
import com.aidchow.renran.data.source.ScheduleDataSource
import com.aidchow.renran.data.source.ScheduleRepository

/**
 * Created by aidchow on 17-6-10.
 */
class AddSchedulePresenter(private val mScheduleID: String?,
                           private val imagePath: String?,
                           val scheduleRepository: ScheduleRepository,
                           val view: AddScheduleContract.View)
    : AddScheduleContract.Presenter, ScheduleDataSource.GetScheduleCallback {


    init {
        view.setPresenter(this)
    }

    override fun start() {
        if (!isNewSchedule) {
            populateSchedule()
        } else if (imagePath != null){
            view.setImage(imagePath)
        }else{
            view.hideAddImage()
        }
    }

    override fun saveSchedule(imagePath: String?, description: String?, date: Long, scheduleId: String?) {
        if (isNewSchedule) {
            createSchedule(imagePath, description, date)
        } else {
            updateSchedule(imagePath, description, date, scheduleId)
        }
    }

    private fun createSchedule(imagePath: String?, description: String?, date: Long) {

        if (description?.isEmpty()!! || date == 0L) {
            view.showEmptyScheduleError()
        } else {
            val schedule = Schedule(imagePath, description, date)
            scheduleRepository.saveSchedule(schedule)
            view.showSchedules()
        }
    }

    private fun updateSchedule(imagePath: String?, description: String?, date: Long, scheduleId: String?) {
        if (isNewSchedule) {
            throw RuntimeException("updateSchedule() was called but task is new.")
        } else {
            val schedule = Schedule(imagePath, description, date)
            schedule.scheduleID = scheduleId
            scheduleRepository.updateSchedule(schedule)
            view.showSchedules()
        }
    }

    override fun populateSchedule() {
        if (isNewSchedule) {
            throw RuntimeException("populateSchedule() was called but task is new.")
        }
        scheduleRepository.getSchedule(mScheduleID!!, this)
    }

    private val isNewSchedule: Boolean
        get() = mScheduleID == null

    override fun onScheduleLoaded(schedule: Schedule) {
        if (view.isActive()) {
            view.setDate(schedule.date)
            view.setDescription(schedule.description!!)
            view.setScheduleId(schedule.scheduleID)
            if (schedule.imagePath == null){
                view.hideAddImage()
            }else{
                view.setImage(schedule.imagePath)
            }
        }
    }

    override fun onDataNotAvailable() {
        if (view.isActive()) {
            view.showEmptyScheduleError()
        }
    }
}