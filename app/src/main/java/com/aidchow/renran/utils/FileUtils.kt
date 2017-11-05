package com.aidchow.renran.utils

import android.os.Environment
import android.util.Log
import io.realm.internal.IOException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zhoujunjiang on 12/10/2017.
 */
object FileUtils {
    val RENRAN = "RenRan"
    val BACKUP_DIR = "backup"
    val BACKUP_FILE_NAME = "renran.bac"
    val IMAGE_DIR = "image"
    fun getRenRanDir(): File {
        val dir = File(Environment.getExternalStorageDirectory().path + File.separator + RENRAN)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getBackUpDir(): File {
        val dir = File(getRenRanDir(), BACKUP_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun createBackupFile(): File {
        val backUpFile = File(getBackUpDir(), BACKUP_FILE_NAME)
        if (!backUpFile.exists()) {
            backUpFile.createNewFile()
        } else {
            backUpFile.deleteOnExit()
            backUpFile.createNewFile()
        }
        return backUpFile
    }

    fun getBackUpImageDir(): File {
        val dir = File(getBackUpDir(), IMAGE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        } else {
            dir.deleteOnExit()
            dir.mkdirs()
        }
        return dir
    }


    fun backUpImage(imagePath: String): String {

        val imageName = SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(System.currentTimeMillis())
        val imageFile = File(getBackUpImageDir(), imageName)
        try {
            val im = File(imagePath)
            val input = FileInputStream(im)
            val out = FileOutputStream(imageFile)
            val len = input.read()
           out.write(input.readBytes())
            input.close()
            out.close()

        } catch (file: FileNotFoundException) {
            file.stackTrace
        } catch (io: IOException) {
            io.stackTrace
        }

        return imageFile.path
    }

}