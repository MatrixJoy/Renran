package com.aidchow.renran.addschedule

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import com.aidchow.renran.BaseFragment
import com.aidchow.renran.R
import com.bumptech.glide.Glide
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.add_schedule_fragment.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by aidchow on 17-6-10.
 */
class AddScheduleFragment : BaseFragment(), AddScheduleContract.View, DatePickerDialog.OnDateSetListener {
    private var presenter: AddScheduleContract.Presenter? = null
    private var mScheduleId: String? = null
    private var date: Long = 0L
    private var imagePath: String? = null

    companion object {
        val ARGUMENT_EDIT_SCHEDULE_ID = "EDIT_SCHEDULE_ID"
        val ARGUMENT_ADD_SCHEDULE_IMAGE_PATH = "ADD_SCHEDULE_IMAGE_PATH"
        fun newInstance(): AddScheduleFragment {
            val bundle = Bundle()
            bundle.putBoolean("reload", false)
            val frg = AddScheduleFragment()
            frg.arguments = bundle
            return frg
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!arguments.getBoolean("reload")) {
            presenter?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        arguments.putBoolean("reload", true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.add_new_schedule, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add_new_schedule -> presenter?.saveSchedule(imagePath!!,
                    edit_description?.text.toString(), date, mScheduleId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.add_schedule_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.title = context.getString(R.string.add_new_schedule)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val ab = (activity as AppCompatActivity).supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        val now = Calendar.getInstance()
        val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.setVersion(DatePickerDialog.Version.VERSION_2)
        dpd.accentColor = context.resources.getColor(R.color.colorPrimary)
        relative_choose_date.setOnClickListener {
            dpd.show(activity.fragmentManager, getString(R.string.pass_time))
        }
        image_review.setOnClickListener { showChooseDialog() }
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun setImage(imagePath: String?) {
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_border_add_image)
                .into(image_review)
        this.imagePath = imagePath
    }

    override fun setDescription(description: String) {
        (edit_description as TextView).text = description
    }

    override fun setDate(date: Long) {
        this.date = date
        tv_pass_date.text = getString(R.string.pass_time).plus(" ").plus(SimpleDateFormat(getString(R.string.date_format),
                Locale.getDefault()).format(Date(date * 1000)))
    }

    override fun showSchedules() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    override fun showEmptyScheduleError() {
        Snackbar.make(relative_choose_date!!, R.string.can_not_empty, Snackbar.LENGTH_LONG).show()
    }

    override fun setPresenter(presenter: AddScheduleContract.Presenter) {
        this.presenter = presenter
    }

    override fun setScheduleId(scheduleId: String?) {
        mScheduleId = scheduleId
    }

    override fun setImagePath(imagePath: String) {
        setImage(imagePath)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = StringBuilder().append(year).append("-")
                .append(monthOfYear + 1).append("-").append(dayOfMonth).toString()
        val format = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
        val time = format.parse(date).time.div(1000)
        this.date = time
        tv_pass_date.text = context.getString(R.string.year_mon_date)
                .format(year, monthOfYear + 1, dayOfMonth)
    }
}
