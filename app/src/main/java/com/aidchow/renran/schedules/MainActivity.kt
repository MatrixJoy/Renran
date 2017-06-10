package com.aidchow.renran.schedules

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aidchow.renran.R
import com.aidchow.renran.data.source.ScheduleRepository
import com.aidchow.renran.data.source.local.ScheduleLocalDataSource

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fragment: SchedulesFragment? = supportFragmentManager.findFragmentById(R.id.frg_container) as? SchedulesFragment
        if (fragment == null) {
            fragment = SchedulesFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.frg_container, fragment,
                    SchedulesFragment::class.java.canonicalName)
                    .commit()
        }
        ScheduleRepository.destroyInstance()

        SchedulesPresenter(ScheduleRepository
                .getInstance(ScheduleLocalDataSource.getInstance()), fragment)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()

        }
    }

}
