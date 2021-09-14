package com.bd.deliverytiger.app.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.bd.deliverytiger.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun createNewFile(context: Context, type: FileType = FileType.Video): File? {

    return try {
        val storageDir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
        }
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        var prefix = ""
        var suffix = ""
        when (type) {
            FileType.Picture -> {
                prefix = "IMG_${timeStamp}_"
                suffix = ".jpg"
            }
            FileType.Video -> {
                prefix = "VID_${timeStamp}_"
                suffix = ".mp4"
            }
            FileType.Audio -> {
                prefix = "AUD_${timeStamp}_"
                suffix = ".mp3"
            }
        }
        File.createTempFile(prefix, suffix, storageDir).apply {
            val filePath = absolutePath
            Timber.d("createNewFile TempFilePath: $filePath")
            if (type == FileType.Video) {
                val videoPath = absolutePath
            }
        }
    } catch (e: Exception) {
        Timber.d(e)
        null
    }

    /*val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val relativeLocation = Environment.DIRECTORY_DCIM + "/" + getString(R.string.app_name)
    var contentUri = Uri.EMPTY
    var mimeType = ""
    var name = ""
    var displayNameKey = ""
    var relativePathKey = ""
    when (type) {
        FileType.Picture -> {
            name = "IMG_${timeStamp}.jpg"
            mimeType = "image/jpeg"
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            displayNameKey = MediaStore.Images.ImageColumns.DISPLAY_NAME
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                relativePathKey = MediaStore.Images.ImageColumns.RELATIVE_PATH
            }
        }
        FileType.Video -> {
            name = "VID_${timeStamp}.mp4"
            mimeType = "video/mp4"
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            displayNameKey = MediaStore.Video.VideoColumns.DISPLAY_NAME
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                relativePathKey = MediaStore.Video.VideoColumns.RELATIVE_PATH
            }
        }
    }
    val contentValues = ContentValues().apply {
        put(displayNameKey, name)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(relativePathKey, relativeLocation)
        }
    }
    Timber.d("createNewFile relativePath: $relativeLocation")


    return try {
        val uri: Uri? = contentResolver.insert(contentUri, contentValues)
        uri?.let {
            Timber.d("createNewFile contentUri: $it")
            val uriForFile = FileProvider.getUriForFile(this, "com.ajkerdeal.app.livestream.fileprovider", File(it.path ?: ""))
            fileUri = uriForFile
            Timber.d("createNewFile fileUri: $fileUri")
            return File(fileUri.path ?: "")
        }
        null
    } catch (e: Exception) {
        Timber.d(e)
        null
    }*/

}

fun createNewFilePath(context: Context, type: FileType = FileType.Video): String {
    val storageDir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
    } else {
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
    }
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    var prefix = ""
    var suffix = ""
    when (type) {
        FileType.Picture -> {
            prefix = "IMG_${timeStamp}_"
            suffix = ".jpg"
        }
        FileType.Video -> {
            prefix = "VID_${timeStamp}_"
            suffix = ".mp4"
        }
        FileType.Audio -> {
            prefix = "AUD_${timeStamp}_"
            suffix = ".mp3"
        }
    }
    return storageDir.absolutePath + prefix + suffix
}

fun moveToDICM(context: Context, sourcePath: String, type: FileType = FileType.Video, listener: (isSuccess: Boolean, path: String) -> Unit) {

    CoroutineScope(Dispatchers.IO).launch {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val relativeLocation = Environment.DIRECTORY_DCIM + "/" + context.getString(R.string.app_name)
        var contentUri = Uri.EMPTY
        var mimeType = ""
        var name = ""
        var displayNameKey = ""
        var relativePathKey = ""
        when (type) {
            FileType.Picture -> {
                name = "IMG_${timeStamp}.jpg"
                mimeType = "image/jpeg"
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                displayNameKey = MediaStore.Images.ImageColumns.DISPLAY_NAME
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    relativePathKey = MediaStore.Images.ImageColumns.RELATIVE_PATH
                }
            }
            FileType.Video -> {
                name = "VID_${timeStamp}.mp4"
                mimeType = "video/mp4"
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                //val contentUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                displayNameKey = MediaStore.Video.VideoColumns.DISPLAY_NAME
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    relativePathKey = MediaStore.Video.VideoColumns.RELATIVE_PATH
                }
            }
        }
        val contentValues = ContentValues().apply {
            put(displayNameKey, name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(relativePathKey, relativeLocation)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }
        Timber.d("createNewFile relativePath: $relativeLocation")

        try {
            val uri: Uri? = context.contentResolver.insert(contentUri, contentValues)
            if (uri != null) {
                context.contentResolver.openFileDescriptor(uri, "w").use { pfd ->
                    val outputStream = FileOutputStream(pfd!!.fileDescriptor)
                    val inputStream = File(sourcePath).inputStream()
                    val buf = ByteArray(8192)
                    var len = 0
                    while (inputStream.read(buf).also { length -> len = length } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                    outputStream.close()
                    inputStream.close()
                    pfd.close()
                }

                val contentValuesUpdate = ContentValues().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.IS_PENDING, 0)
                    }
                }
                val row = context.contentResolver.update(uri, contentValuesUpdate, null, null)
                if (row > 0) {
                    if (File(sourcePath).delete()) {
                        Timber.d("File moved")
                    }
                }
                val videoPath = FileUtils(context).getPath(uri)
                listener.invoke(true, videoPath)
            }
        } catch (e: Exception) {
            Timber.d(e)
            listener.invoke(false, sourcePath)
        }
    }

}

fun saveLogoToStorage(context: Context, @DrawableRes resourceId: Int): String {

    val bm = (ResourcesCompat.getDrawable(context.resources, resourceId, null) as BitmapDrawable).bitmap
    val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    val file = File(storageDir, "logo.png")

    try {
        file.outputStream().use { outStream ->
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file.absolutePath
}

fun deleteAjkerdealDirectory(context: Context) {
    val storageDir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
    } else {
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
    }
    storageDir.deleteRecursively()
}

