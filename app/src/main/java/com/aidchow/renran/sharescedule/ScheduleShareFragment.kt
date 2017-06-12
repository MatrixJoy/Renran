package com.aidchow.renran.sharescedule

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.aidchow.renran.R
import com.aidchow.renran.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.scedule_share_fragment.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aidchow on 17-6-9.
 */
class ScheduleShareFragment : Fragment(), ShareScheduleContract.View {
    private var presenter: ShareScheduleContract.Presenter? = null

    companion object {
        fun newInstance(): ScheduleShareFragment {
            return ScheduleShareFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        presenter?.start()
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
                card_container.isDrawingCacheEnabled = false
                val uri = Utils.createBitmapUri(activity, bitmap)
                val shareIntent: Intent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "image/jpeg"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
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

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun setPresenter(presenter: ShareScheduleContract.Presenter) {
        this.presenter = presenter
    }

    override fun setImage(imagePath: String) {
        Glide.with(context).load(imagePath).into(image_of_schedule)
    }


    override fun setDescriptionAndDate(description: String, date: Long) {
        val day = (System.currentTimeMillis().div(1000) - date).div(86400)

        tv_schedule_text.text = Utils.formatDescription(context, day, description)

        tv_day?.text = Utils.formatDay(context, day)

        val trueDate = SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())

        tv_true_date!!.text = context?.getString(R.string.true_date)!!
                .format(trueDate.format(Date(date * 1000)))
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    override fun showEmptyScheduleError() {
        Snackbar.make(view!!, "not found", Snackbar.LENGTH_SHORT).show()
    }
}