package com.dicoding.picodiploma.loginwithanimation.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun uriToFile(uri: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        } else {
            "temp_file"
        }
    } ?: "temp_file"

    val tempFile = File(context.cacheDir, fileName)
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: throw IOException("Unable to open input stream from URI")
    } catch (e: IOException) {
        e.printStackTrace()
        throw e
    }
    return tempFile
}