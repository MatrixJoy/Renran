package com.aidchow.renran

/**
 * Created by aidchow on 17-6-8.
 */
interface BaseView<in T : BasePresenter> {
    fun setPresenter(presenter: T)
}