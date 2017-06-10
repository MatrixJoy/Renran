package com.aidchow.renran

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.aidchow.renran.utils.Utils

/**
 * Created by aidchow on 17-6-10.
 * BaseFragment extends this can choose photo from file or take photo
 */
open abstract class BaseFragment : Fragment() {
    private val TAKEPHOTO_REQUEST_CODE = 20001
    private val CHOOSEPHOTO_REQUEST_CODE = 20002
    private var uri: Uri? = null
    fun showChooseDialog() {
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

    /**
     * access image path
     */
    abstract fun setImagePath(imagePath: String)

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


    private fun openAlbum() {
        val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
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
                if (resultCode == Activity.RESULT_OK) {
                    setImagePath(uri.toString())
                }
            }
            CHOOSEPHOTO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        setImagePath(Utils.handleImagePath(context, data)!!)
                    } catch (e: Exception) {
                        println(data)
                    }
                }
            }

        }

    }


}