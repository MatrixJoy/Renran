package com.aidchow.renran.sharescedule

import com.aidchow.renran.BasePresenter
import com.aidchow.renran.BaseView

/**
 * Created by aidchow on 17-6-10.
 */
interface ShareScheduleContract {
    interface Presenter : BasePresenter {

        fun populateSchedule()
    }

    interface View : BaseView<Presenter> {
        fun setImage(imagePath: String)

        fun setDescriptionAndDate(description: String, date: Long)


        fun isActive(): Boolean

        fun showEmptyScheduleError()
    }
}