package com.aidchow.renran.schedules

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.scedule_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aidchow on 17-6-9.
 */
class SchedulesAdapter(var schedules: MutableList<Schedule>?) : RecyclerView.Adapter<SchedulesAdapter.SchedulesViewHolder>() {
    private var context: Context? = null
    var mShareButtonListener: OnShareButtonClickListener? = null
    var position: Int? = 0
    override fun getItemCount(): Int {
        return schedules?.size!!
    }

    fun setData(dates: MutableList<Schedule>?) {
        schedules?.clear()
        dates?.let { schedules?.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SchedulesViewHolder?, position: Int) {

        val day = (System.currentTimeMillis().div(1000) - schedules!![position].date).div(86400)

        if (day < 0) {
            holder?.tvScheduleText?.text = context?.getString(R.string.schedule_string)
                    ?.format(schedules!![position].description)
        } else {
            holder?.tvScheduleText?.text = context?.getString(R.string.schedule_pass_string)
                    ?.format(schedules!![position].description)
        }

        val textDay = context?.getString(R.string.day)?.format(Math.abs(day))

        val spanString = SpannableString(textDay)
        val size: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 64.0f,
                context?.resources?.displayMetrics).toInt()
        spanString.setSpan(AbsoluteSizeSpan(size), 0, spanString.length - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanString.setSpan(ForegroundColorSpan(context?.resources!!.getColor(android.R.color.darker_gray)),
                spanString.length - 1, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder?.tvDay?.text = spanString

        val trueDate = SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())
        holder?.tvTrueDate!!.text = context?.getString(R.string.true_date)!!
                .format(trueDate.format(Date(schedules!![position].date * 1000)))

        holder.itemView?.setOnLongClickListener {
            this.position = holder.adapterPosition
            false
        }
        Glide.with(context).load(schedules!![position].imagePath).into(holder.imageView)
        holder.shareButton?.setOnClickListener { view ->
            if (mShareButtonListener != null) {
                mShareButtonListener?.onShareButtonClick(view, holder.adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SchedulesViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.scedule_item, parent, false)
        context = parent?.context
        return SchedulesViewHolder(view)
    }

    class SchedulesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvDay = itemView?.tv_day
        val tvScheduleText = itemView?.tv_schedule_text
        val tvTrueDate = itemView?.tv_true_date
        val shareButton = itemView?.btu_share
        val imageView = itemView?.image_of_schedule

        init {
            itemView?.setOnCreateContextMenuListener {
                menu, _, _ ->
                menu?.add(Menu.NONE, R.id.action_delete, 0, R.string.delete)
                menu?.add(Menu.NONE, R.id.action_modify, 0, R.string.modify)
            }
        }

    }

    override fun onViewRecycled(holder: SchedulesViewHolder?) {
        holder?.itemView?.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    fun getData(position: Int): Schedule {
        return schedules!![position]
    }

    fun removieItem(position: Int) {
        schedules?.remove(schedules!![position])
        notifyItemRemoved(position)
    }

    fun getdatas(): MutableList<Schedule>? {
        return schedules
    }

    interface OnShareButtonClickListener {
        fun onShareButtonClick(view: View, position: Int)
    }

}