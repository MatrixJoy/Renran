package com.aidchow.renran.schedules

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.utils.Utils
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aidchow on 17-6-9.
 */
class SchedulesAdapter(var schedules: MutableList<Schedule>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var context: Context? = null
    var mShareButtonListener: OnShareButtonClickListener? = null
    var position: Int? = 0
    val TYPE_HAS_IMAGE = 1
    val TYPE_NO_IMAGE = 2
    override fun getItemCount(): Int {
        return schedules?.size!!
    }

    fun setData(dates: MutableList<Schedule>?) {
        schedules?.clear()
        dates?.let { schedules?.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder?.itemViewType) {
            TYPE_HAS_IMAGE -> {
                val viewHolder = (holder as? SchedulesViewHolder)

                val day = Utils.getDay(schedules!![position].date)

                viewHolder?.tvScheduleText?.text = Utils.formatDescription(context!!, day, schedules!![position].description!!)

                viewHolder?.tvDay?.text = Utils.formatDay(context!!, day)

                val trueDate = SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())
                viewHolder?.tvTrueDate!!.text = context?.getString(R.string.true_date)!!
                        .format(trueDate.format(Date(schedules!![position].date * 1000)))
                viewHolder.itemView?.setOnLongClickListener {
                    this.position = holder.adapterPosition
                    false
                }
                Glide.with(context).load(schedules!![position].imagePath).into(viewHolder.imageView)
                viewHolder.shareButton?.setOnClickListener {
                    if (mShareButtonListener != null) {
                        mShareButtonListener?.onShareButtonClick(viewHolder.container!!, holder.adapterPosition)
                    }
                }
            }

            TYPE_NO_IMAGE -> {
                val viewHolder = (holder as? SchedulesNoImageViewHolder)
                val day = Utils.getDay(schedules!![position].date)

                viewHolder?.tvScheduleText?.text = schedules!![position].description!!
                when {
                    day > 0 -> {
                        viewHolder?.tvHas?.text = context?.getString(R.string.pass)
                        viewHolder?.tvDistance?.visibility = View.GONE
                    }
                    day < 0 -> {
                        viewHolder?.tvHas?.text = context?.getString(R.string.has)
                        viewHolder?.tvDistance?.text = context?.getString(R.string.distance)
                    }
                    else -> {
                        viewHolder?.tvHas?.text = context?.getString(R.string.now)
                        viewHolder?.tvDistance?.visibility = View.GONE
                    }
                }
                viewHolder?.tvDay?.text = Utils.formatDay(context!!, day)

                val trueDate = SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())
                viewHolder?.tvTrueDate!!.text = context?.getString(R.string.true_date)!!
                        .format(trueDate.format(Date(schedules!![position].date * 1000)))
                viewHolder.itemView?.setOnLongClickListener {
                    this.position = holder.adapterPosition
                    false
                }
                viewHolder.shareButton?.setOnClickListener {
                    if (mShareButtonListener != null) {
                        mShareButtonListener?.onShareButtonClick(viewHolder.container!!, holder.adapterPosition)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var holder: RecyclerView.ViewHolder? = null
        when (viewType) {
            TYPE_HAS_IMAGE -> {
                val view = LayoutInflater.from(parent?.context).inflate(R.layout.scedule_item, parent, false)
                context = parent?.context
                holder = SchedulesViewHolder(view)
            }
            TYPE_NO_IMAGE -> {
                val view = LayoutInflater.from(parent?.context).inflate(R.layout.scedule_no_image_item, parent, false)
                context = parent?.context
                holder = SchedulesNoImageViewHolder(view)
            }

        }
        return holder!!
    }

    override fun getItemViewType(position: Int): Int {
        if (schedules?.get(position)?.imagePath == null) {
            return TYPE_NO_IMAGE
        }
        return TYPE_HAS_IMAGE
    }

    inner class SchedulesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (menu != null) {
                menu.add(Menu.NONE, R.id.action_delete, 0, R.string.delete)
                menu.add(Menu.NONE, R.id.action_modify, 0, R.string.modify)
//                menu.add(Menu.NONE, R.id.action_add_on_widget, 0, R.string.add_on_screen)
            }
        }

        val tvDay = itemView?.findViewById(R.id.tv_day) as TextView
        val tvScheduleText = itemView?.findViewById(R.id.tv_schedule_text) as TextView
        val tvTrueDate = itemView?.findViewById(R.id.tv_true_date) as TextView
        val shareButton = itemView?.findViewById(R.id.btu_share)
        val imageView = itemView?.findViewById(R.id.image_of_schedule) as ImageView
        val container = itemView?.findViewById(R.id.card_container)
        init {
            itemView?.setOnCreateContextMenuListener(this)
        }

    }


    inner class SchedulesNoImageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (menu != null) {
                menu.add(Menu.NONE, R.id.action_delete, 0, R.string.delete)
                menu.add(Menu.NONE, R.id.action_modify, 0, R.string.modify)
//                menu.add(Menu.NONE, R.id.action_add_on_widget, 0, R.string.add_on_screen)
            }
        }

        val tvDay = itemView?.findViewById(R.id.tv_day) as TextView
        val tvScheduleText = itemView?.findViewById(R.id.tv_schedule_text) as TextView
        val tvTrueDate = itemView?.findViewById(R.id.tv_true_date) as TextView
        val shareButton = itemView?.findViewById(R.id.btu_share)
        val tvDistance = itemView?.findViewById(R.id.tv_distance_text) as TextView
        val tvHas = itemView?.findViewById(R.id.tv_has_text) as TextView
        val container = itemView?.findViewById(R.id.card_container)
        init {
            itemView?.setOnCreateContextMenuListener(this)
        }

    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        holder?.itemView?.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    fun getData(position: Int): Schedule {
        return schedules!![position]
    }

    fun removeItem(position: Int) {
        schedules?.remove(schedules!![position])
        notifyItemRemoved(position)
    }

    fun getDatas(): MutableList<Schedule>? {
        return schedules
    }

    interface OnShareButtonClickListener {
        fun onShareButtonClick(view: View, position: Int)
    }

}