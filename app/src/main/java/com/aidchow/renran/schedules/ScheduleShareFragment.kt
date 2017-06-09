package com.aidchow.renran.schedules

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.utils.Utils
import kotlinx.android.synthetic.main.scedule_share_fragment.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aidchow on 17-6-9.
 */
class ScheduleShareFragment : Fragment() {
    private var schedule: Schedule? = null

    companion object {
        fun newInstance(schedule: Schedule): ScheduleShareFragment {
            val bundle = Bundle()
            bundle.putSerializable("schedule", schedule)
            val frg = ScheduleShareFragment()
            frg.arguments = bundle
            return frg
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedule = arguments.getSerializable("schedule") as Schedule
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.shcedules_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_share -> {
                card_container.isDrawingCacheEnabled = true
                card_container.buildDrawingCache()
                val bitmap = Bitmap.createBitmap(card_container.drawingCache)
                val uri = Utils.createBitemapUri(activity, bitmap)
                val shareIntent: Intent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "image/jpeg"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.scedule_share_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = context.getString(R.string.share_preview)
        val ab = (activity as AppCompatActivity).supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        val day = (System.currentTimeMillis().div(1000) - schedule!!.date).div(86400)
        if (day < 0) {
            tv_schedule_text?.text = context?.getString(R.string.schedule_string)
                    ?.format(schedule?.description)
        } else {
            tv_schedule_text?.text = context?.getString(R.string.schedule_pass_string)
                    ?.format(schedule?.description)
        }

        val textDay = context?.getString(R.string.day)?.format(Math.abs(day))

        val spanString = SpannableString(textDay)

        val size: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 64.0f,
                context?.resources?.displayMetrics).toInt()

        spanString.setSpan(AbsoluteSizeSpan(size), 0, spanString.length - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanString.setSpan(ForegroundColorSpan(context?.resources!!.getColor(android.R.color.darker_gray)),
                spanString.length - 1, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        println(spanString)
        tv_day?.text = spanString

        val trueDate = SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())
        tv_true_date!!.text = context?.getString(R.string.true_date)!!
                .format(trueDate.format(Date(schedule!!.date * 1000)))
    }

    override fun onDestroy() {
        schedule = null
        super.onDestroy()
    }

}