package com.aidchow.renran.schedules

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.realm.RealemHelper
import kotlinx.android.synthetic.main.schedules_fragment.*

/**
 * Created by aidchow on 17-6-8.
 */
class SchedulesFragment : Fragment(), SchedulesContract.View, SchedulesAdapter.OnShareButtonClickListener {


    private var presenter: SchedulesContract.Presenter? = null
    private var adapter: SchedulesAdapter? = null

    companion object {
        fun newInstance(): SchedulesFragment {
            return SchedulesFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        presenter?.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.schedules_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = ""
        recycler_view_of_schedules.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        adapter = SchedulesAdapter(ArrayList(0))
        adapter?.mShareButtonListener = this
        recycler_view_of_schedules.adapter = adapter
        tv_empty_tips.setOnClickListener { showAddNewScheduleUi() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_new_schedule -> {
                showAddNewScheduleUi()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> deleteSchedule()
        }
        return super.onContextItemSelected(item)
    }

    private fun deleteSchedule() {
        presenter?.deleteSchedules(adapter?.getData(adapter?.position!!)!!)
        adapter?.removieItem(adapter?.position!!)
        if (adapter?.getdatas()!!.isEmpty()) {
            noSchedules()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.schedules_menu, menu)
    }

    override fun showSchedules(schedules: MutableList<Schedule>) {
        relative_empty.visibility = View.GONE
        adapter?.setData(schedules)
    }

    override fun noSchedules() {
        relative_empty.visibility = View.VISIBLE
    }

    override fun showModifySchedulesUi(scheduleId: String) {

    }

    override fun showAddNewScheduleUi() {

    }

    override fun isActive(): Boolean {
        return isAdded
    }


    override fun setPresenter(presenter: SchedulesContract.Presenter) {
        this.presenter = presenter
    }

    override fun onShareButtonClick(view: View, position: Int) {
        fragmentManager.beginTransaction().add(R.id.frg_container,
                ScheduleShareFragment
                        .newInstance(RealemHelper.getRenRanRealmInstance()
                                .copyFromRealm(adapter?.getData(position)!!)))
                .addToBackStack(ScheduleShareFragment::class.java.canonicalName)
                .hide(this)
                .commitAllowingStateLoss()

    }
}