package com.aidchow.renran.addschedule

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.DatePicker
import android.widget.TextView
import com.aidchow.renran.R
import com.aidchow.renran.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.add_schedule_fragment.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aidchow on 17-6-9.
 */
class AddScheduleFragment : Fragment(), AddScheduleContract.View, DatePickerFragment.OnDateSetListener {

    private var presenter: AddScheduleContract.Presenter? = null
    private var imagePath: String? = null
    private var mScheduleId: String? = null
    private var date: Long = 0L
    private val TAKEPHOTO_REQUEST_CODE = 20004
    private val CHOOSEPHOTO_REQUEST_CODE = 20005
    private var uri: Uri? = null

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
        val datePicker = DatePickerFragment()
        relative_choose_date.setOnClickListener {
            datePicker.show(activity.supportFragmentManager, getString(R.string.pass_time))
        }
        datePicker.onDateSetListener = this
        image_review.setOnClickListener { showChosePhotoUi() }
    }

    fun showChosePhotoUi() {
        val builder = AlertDialog.Builder(activity)
                .setItems(arrayOf(getString(R.string.from_camera), getString(R.string.from_photo)),
                        { dialog, which ->
                            when (which) {
                                0 -> {
                                    takePhoto()
                                    dialog.dismiss()
                                }
                                1 -> {
                                    choosePhoto()
                                    dialog.dismiss()
                                }
                            }
                        })
        builder.create().show()
    }

    fun takePhoto() {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), TAKEPHOTO_REQUEST_CODE)
        } else {
            openCamera()
        }
    }

    fun choosePhoto() {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CHOOSEPHOTO_REQUEST_CODE)
        } else {
            openAlbum()
        }
    }


    private fun openCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = Utils.captureUri(context)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(captureIntent, TAKEPHOTO_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            TAKEPHOTO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    showSnackbar(arrayOf(Manifest.permission.CAMERA), TAKEPHOTO_REQUEST_CODE)
                }
            }
            CHOOSEPHOTO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum()
                } else {
                    showSnackbar(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CHOOSEPHOTO_REQUEST_CODE)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TAKEPHOTO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    setImage(uri.toString())
                }
            }
            CHOOSEPHOTO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    setImage(Utils.handleImagePath(context, data)!!)
                }
            }
        }

    }

    private fun openAlbum() {
        val chooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooseIntent.type = "image/*"
        startActivityForResult(chooseIntent, CHOOSEPHOTO_REQUEST_CODE)
    }


    private fun showSnackbar(permissions: Array<out String>, requestCode: Int) {
        Snackbar.make(view!!, R.string.permission_denied, Snackbar.LENGTH_LONG).setAction(R.string.setting, {
            requestPermissions(permissions, requestCode)
        }).setActionTextColor(Color.RED).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDateSet(date: String, formatDate: Long) {
        this.date = formatDate
        tv_pass_date.text = getString(R.string.pass_time)
                .plus(" ").plus(date)
    }

    override fun setImage(imagePath: String) {
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
        Snackbar.make(edit_description!!, "Schedule must not None ", Snackbar.LENGTH_LONG).show()
    }

    override fun setPresenter(presenter: AddScheduleContract.Presenter) {
        this.presenter = presenter
    }

    override fun setScheduleId(scheduleId: String?) {
        mScheduleId = scheduleId
    }
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    var onDateSetListener: OnDateSetListener? = null

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (onDateSetListener != null) {
            val date = StringBuilder().append(year).append("-")
                    .append(month.plus(1)).append("-").append(dayOfMonth).toString()
            val format = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
            val time = format.parse(date).time.div(1000)
            onDateSetListener?.onDateSet(
                    context.getString(R.string.year_mon_date)
                            .format(year, month + 1, dayOfMonth), time)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity, this, year, month, day)
    }

    interface OnDateSetListener {
        fun onDateSet(date: String, formatDate: Long)
    }
}