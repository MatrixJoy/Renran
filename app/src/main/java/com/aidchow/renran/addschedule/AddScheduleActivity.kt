package com.aidchow.renran.addschedule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aidchow.renran.R
import com.aidchow.renran.data.source.ScheduleRepository
import com.aidchow.renran.data.source.local.ScheduleLocalDataSource

class AddScheduleActivity : AppCompatActivity() {

    companion object {
        val ADD_SCHEDULE_REQUEST_CODE = 20003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)
        var addScheduleFragment: AddScheduleFragment? = supportFragmentManager
                .findFragmentById(R.id.frame_container) as? AddScheduleFragment
        val mScheduleID = intent.getStringExtra(AddScheduleFragment.ARGUMENT_EDIT_SCHEDULE_ID)
        val imagePath = intent.getStringExtra(AddScheduleFragment.ARGUMENT_ADD_SCHEDULE_IMAGE_PATH)
        if (addScheduleFragment == null) {
            addScheduleFragment = AddScheduleFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.frame_container, addScheduleFragment)
                    .commit()
        }

        AddSchedulePresenter(mScheduleID, imagePath,
                ScheduleRepository.getInstance(ScheduleLocalDataSource.getInstance()),
                addScheduleFragment)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
