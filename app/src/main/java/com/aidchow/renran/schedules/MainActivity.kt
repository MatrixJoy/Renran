package com.aidchow.renran.schedules

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.aidchow.renran.R
import com.aidchow.renran.data.source.ScheduleRepository
import com.aidchow.renran.data.source.local.ScheduleLocalDataSource

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = SchedulesFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.frg_container, fragment,
                SchedulesFragment::class.java.canonicalName)
                .commit()
        ScheduleRepository.destroyInstance()
        fragment.setPresenter(SchedulesPresenter(ScheduleRepository
                .getInstance(ScheduleLocalDataSource.getInstance()), fragment))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
