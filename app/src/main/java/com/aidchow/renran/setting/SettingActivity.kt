package com.aidchow.renran.setting

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.aidchow.renran.R
import com.aidchow.renran.data.Schedule
import com.aidchow.renran.realm.RealmHelper
import com.aidchow.renran.utils.FileUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.setting_activity.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileOutputStream

/**
 * Created by zhoujunjiang on 11/10/2017.
 */
class SettingActivity : AppCompatActivity() {
    companion object {
        val REQUEST_WRITE_EXTERNAL_PERMISSION_CODE = 2004
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        start_backup.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_PERMISSION_CODE)
                }else{
                    startBackUp()
                }
            }else{
                startBackUp()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_PERMISSION_CODE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startBackUp()
                } else {
                    showSnackbar(arrayOf(Manifest.permission.CAMERA), REQUEST_WRITE_EXTERNAL_PERMISSION_CODE)
                }
            }
        }
    }
    private fun showSnackbar(permissions: Array<out String>, requestCode: Int) {
        Snackbar.make(root_layout!!, R.string.permission_denied, Snackbar.LENGTH_LONG).setAction(R.string.setting, {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode)
            }
        }).setActionTextColor(Color.RED).show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun startBackUp(){
       val showDialog = AlertDialog.Builder(this).setMessage("备份后的文件将会/sdcard/RenRan/backup下请勿删除此文件")
               .setPositiveButton("开始") { dialog, which ->
        Toast.makeText(this, "还没做", Toast.LENGTH_SHORT).show()
                   val rlm = Realm.getInstance(RealmConfiguration.Builder()
                           .name(RealmHelper.realmName)
                           .build())
                  val results = rlm?.copyFromRealm(
                           rlm.where((Schedule::class.java))
                                   .equalTo("isDelete", false)
                                   .findAll())

//                   object : AsyncTask<MutableList<Schedule>, Int, Boolean>() {
//                       override fun doInBackground(vararg params: MutableList<Schedule>): Boolean? {
//                           val jsonArray = JSONArray()
//                           for (shedule in params[0]){
//                               val jsonObj = JSONObject()
//                               jsonObj.put("description", shedule.description)
//                               jsonObj.put("date", shedule.date)
//                               jsonObj.put("imagePath",FileUtils.backUpImage((shedule.imagePath!!)))
//                               jsonObj.put("scheduleID", shedule.scheduleID)
//                               jsonArray.put(jsonObj)
//                           }
//                           val outFile = FileOutputStream(FileUtils.createBackupFile())
//                           val byte = jsonArray.toString().toByteArray()
//                           outFile.write(byte)
//                           outFile.close()
//                           return true
//
//                       }
//
//                       override fun onPostExecute(result: Boolean?) {
//                          Toast.makeText(this@SettingActivity,"备份成功", Toast.LENGTH_SHORT).show()
//                       }
//                   }.execute(results)

               }.setNegativeButton("稍后"){ dialog, which -> dialog.dismiss() }
        showDialog.create().show()
    }
}