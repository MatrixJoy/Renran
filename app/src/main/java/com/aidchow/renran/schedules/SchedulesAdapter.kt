package com.aidchow.renran.schedules

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.utils.Utils
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

        holder?.tvScheduleText?.text = Utils.formatDescription(context!!,day,schedules!![position].description!!)

        holder?.tvDay?.text = Utils.formatDay(context!!,day)

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

    inner class SchedulesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (menu != null) {
                menu.add(Menu.NONE, R.id.action_delete, 0, R.string.delete)
                menu.add(Menu.NONE, R.id.action_modify, 0, R.string.modify)
                if (schedules!![adapterPosition].isShowOnScreen!!) {
                    menu.add(Menu.NONE, R.id.action_add_on_widget, 0, R.string.delete_on_screen)
                } else {
                    menu.add(Menu.NONE, R.id.action_add_on_widget, 0, R.string.add_on_screen)
                }
            }
        }

        val tvDay = itemView?.tv_day
        val tvScheduleText = itemView?.tv_schedule_text
        val tvTrueDate = itemView?.tv_true_date
        val shareButton = itemView?.btu_share
        val imageView = itemView?.image_of_schedule

        init {
            itemView?.setOnCreateContextMenuListener(this)
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