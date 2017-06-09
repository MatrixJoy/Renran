package com.aidchow.renran.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Created by aidchow on 17-6-9.
 */
object Utils {
    fun createBitemapUri(context: Context, bitmap: Bitmap): Uri {
        val sharePath = File(context.cacheDir, "share")
        if (!sharePath.exists()) {
            sharePath.mkdirs()
        }
        val file = File(sharePath, "share.jpeg")
        if (!file.exists()) {
            file.createNewFile()
        }
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        val uri: Uri = FileProvider.getUriForFile(context, "com.aidchow.renran.fileprovider", file)
        return uri
    }
}