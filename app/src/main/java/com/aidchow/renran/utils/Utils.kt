package com.aidchow.renran.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import com.aidchow.renran.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aidchow on 17-6-9.
 */
object Utils {
    /***
     * for share to qq
     *  because qq not support file provider  so use this way to create a uri to share image
     */
    fun createBitmapUri(context: Context, bitmap: Bitmap): Uri? {
        val sharePath = File(context.externalCacheDir, "images")
        var uri: Uri? = null
        var out: FileOutputStream? = null
        if (!sharePath.exists()) {
            sharePath.mkdirs()
        }
        val file = File(sharePath, "share.jpg")
        if (!file.exists()) {
            file.createNewFile()
        }
        try{
            out = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "com.aidchow.renran.fileprovider", file)
            } else {
                Uri.fromFile(file)
            }
        }finally {
            if(out != null){
                out.close()
            }
        }

        return uri
    }

    /**
     * create take photos uri
     */
    fun captureUri(context: Context): Uri? {
        val filePath = File(context.externalCacheDir, "images")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpeg"
        val file = File(filePath, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        var uri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, "com.aidchow.renran.fileprovider", file)
        } else {
            uri = Uri.fromFile(file)
        }
        return uri
    }

    /**
     * handle the image path when from the file
     */
    fun handleImagePath(context: Context, data: Intent?): String? {
        var imagePath: String? = null
        val uri: Uri? = data?.data
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority) {
                val id = docId.split(":")[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(context, MediaStore.
                        Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri?.authority) {
                val contentUri: Uri? = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), docId.toLong())
                imagePath = getImagePath(context, contentUri, null)
            }
        } else if ("content".equals(uri?.scheme, true)) {
            imagePath = getImagePath(context, uri, null)

        } else if ("file".equals(uri?.scheme, true)) {
            imagePath = uri?.path
        }
        return imagePath
    }

    private fun getImagePath(context: Context, uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = context.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    fun formatDescription(context: Context, day: Long, description: String): String? {
        return when {
            day < 0 -> context.getString(R.string.schedule_string)
                    ?.format(description)
            day > 0 -> context.getString(R.string.schedule_pass_string)
                    ?.format(description)
            else -> context.getString(R.string.schedule_has_today_string).format(description)
        }
    }

    fun formatDay(context: Context, day: Long): SpannableString? {
        var textDay: String? = null
        val dayStr: String = Math.abs(day).toString()
        textDay = if (day == 0L) {
            context.getString(R.string.today)
        } else {

            context.getString(R.string.day)?.format(dayStr)
        }


        val spanString = SpannableString(textDay)

        val size: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 64.0f,
                context.resources?.displayMetrics).toInt()

        spanString.setSpan(AbsoluteSizeSpan(size), 0, dayStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanString.setSpan(ForegroundColorSpan(context.resources!!.getColor(android.R.color.darker_gray)),
                spanString.length - 1, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spanString
    }

    fun getDay(day: Long): Long {
        val simpleFormatDta = SimpleDateFormat("yyy-MM-dd 00:00:00 ", Locale.getDefault())
        val currentDate: Date = simpleFormatDta.parse(simpleFormatDta.format(System.currentTimeMillis()))
        val dueDate: Date = simpleFormatDta.parse(simpleFormatDta.format(day * 1000))
        val result = (currentDate.time - dueDate.time) / (1000 * 3600 * 24)

        return result
    }

}