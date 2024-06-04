package com.example.linguareader.utils

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

object FileUtils {
    // Reads a file from internal storage
    fun readFile(context: Context, filename: String?): String {
        var fileReader: FileReader? = null
        // Creates a file object which references internal storage
        val file = File(context.filesDir, filename!!)

        val text: String
        val stringBuilder = StringBuilder()
        // Reads text line by line using a StringBuilder and BufferedReader
        try {
            fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            text = stringBuilder.toString()
        }

        return text
    }
}
