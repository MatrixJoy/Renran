package com.aidchow.renran.sharescedule

import com.aidchow.renran.data.Schedule
import com.aidchow.renran.data.source.ScheduleDataSource
import com.aidchow.renran.data.source.ScheduleRepository

/**
 * Created by aidchow on 17-6-10.
 */
class ShareSchedulePresenter(private val mScheduleID: String?,
                             val scheduleRepository: ScheduleRepository,
                             val view: ShareScheduleContract.View)
    : ShareScheduleContract.Presenter, ScheduleDataSource.GetScheduleCallback {


    init {
        view.setPresenter(this)
    }

    override fun start() {
        if (!isScheduleID) {
            populateSchedule()
        }
    }


    override fun populateSchedule() {
        if (isScheduleID) {
            throw RuntimeException("isScheduleID must exist")
        }
        scheduleRepository.getSchedule(mScheduleID!!, this)
    }

    private val isScheduleID: Boolean
        get() = mScheduleID == null

    override fun onScheduleLoaded(schedule: Schedule) {
        if (view.isActive()) {
            view.setDescriptionAndDate(schedule.description!!,schedule.date)
            view.setImage(schedule.imagePath!!)
        }
    }

    override fun onDataNotAvailable() {
        if (view.isActive()) {
            view.showEmptyScheduleError()
        }
    }
}