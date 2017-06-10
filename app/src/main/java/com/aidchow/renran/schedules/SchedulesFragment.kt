package com.aidchow.renran.schedules

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.aidchow.renran.R
import com.aidchow.renran.addschedule.AddScheduleActivity
import com.aidchow.renran.addschedule.AddScheduleFragment
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.data.source.ScheduleRepository
import com.aidchow.renran.data.source.local.ScheduleLocalDataSource
import com.aidchow.renran.sharescedule.ScheduleShareFragment
import com.aidchow.renran.sharescedule.ShareSchedulePresenter
import com.aidchow.renran.utils.Utils
import kotlinx.android.synthetic.main.schedules_fragment.*

/**
 * Created by aidchow on 17-6-8.
 */
class SchedulesFragment : Fragment(), SchedulesContract.View, SchedulesAdapter.OnShareButtonClickListener {


    private var presenter: SchedulesContract.Presenter? = null
    private var adapter: SchedulesAdapter? = null
    private var uri: Uri? = null
    private val TAKEPHOTO_REQUEST_CODE = 20001
    private val CHOOSEPHOTO_REQUEST_CODE = 20002

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
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                deleteSchedule()
                return true
            }
            R.id.action_modify -> {
                showModifySchedulesUi(adapter?.getData(adapter?.position!!)?.scheduleID!!)
                return true
            }
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
        openAddNewSchedule(null, scheduleId)
    }

    override fun showAddNewScheduleUi() {
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

    override fun isActive(): Boolean {
        return isAdded
    }


    override fun setPresenter(presenter: SchedulesContract.Presenter) {
        this.presenter = presenter
    }


    override fun onShareButtonClick(view: View, position: Int) {
        val frg = ScheduleShareFragment.newInstance()

        fragmentManager.beginTransaction().add(R.id.frg_container,
                frg)
                .addToBackStack(ScheduleShareFragment::class.java.canonicalName)
                .hide(this)
                .commit()

        ScheduleRepository.destroyInstance()

        frg.setPresenter(ShareSchedulePresenter(adapter?.getData(position)!!.scheduleID,
                ScheduleRepository.getInstance(ScheduleLocalDataSource.getInstance())
                , frg))

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

    override fun showAddSuccessView() {
        Snackbar.make(view!!, "add success", Snackbar.LENGTH_SHORT).show()
    }

    private fun openAlbum() {
        val chooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooseIntent.type = "image/*"
        startActivityForResult(chooseIntent, CHOOSEPHOTO_REQUEST_CODE)
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

    private fun showSnackbar(permissions: Array<out String>, requestCode: Int) {
        Snackbar.make(view!!, R.string.permission_denied, Snackbar.LENGTH_LONG).setAction(R.string.setting, {
            requestPermissions(permissions, requestCode)
        }).setActionTextColor(Color.RED).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println(requestCode)
        when (requestCode) {
            TAKEPHOTO_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    openAddNewSchedule(uri.toString(), null)
                }
            }
            CHOOSEPHOTO_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {

                    openAddNewSchedule(Utils.handleImagePath(context, data), null)
                }
            }
            else -> {
                presenter?.result(requestCode, resultCode)
            }
        }

    }


    private fun openAddNewSchedule(imagePath: String?, scheduleId: String?) {
        val addIntent = Intent(activity, AddScheduleActivity::class.java)
        if (imagePath != null) {
            addIntent.putExtra(AddScheduleFragment.ARGUMENT_ADD_SCHEDULE_IMAGE_PATH, imagePath)
        } else if (scheduleId != null) {
            addIntent.putExtra(AddScheduleFragment.ARGUMENT_EDIT_SCHEDULE_ID, scheduleId)
        }
        startActivityForResult(addIntent, AddScheduleActivity.ADD_SCHEDULE_REQUEST_CODE)
    }

}