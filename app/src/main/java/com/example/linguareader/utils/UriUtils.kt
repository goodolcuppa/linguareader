package com.example.linguareader.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object UriUtils {
    // Reads the contents of the uri to a byte array
    fun readTxtUri(context: Context, uri: Uri?): String? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri!!)
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len = 0
            while ((inputStream!!.read(buffer).also { len = it }) != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            inputStream.close()
            return byteBuffer.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun readPdfUri(context: Context, uri: Uri?): String {
        PDFBoxResourceLoader.init(context)

        var text = ""

        val inputStream: InputStream = context.contentResolver?.openInputStream(uri!!)!!
        com.tom_roush.pdfbox.pdmodel.PDDocument.load(inputStream).use { pdfDocument ->
            if (!pdfDocument.isEncrypted) {
                text = PDFTextStripper().getText(pdfDocument)
            }
        }
        inputStream.close()

        return text
    }

//    fun readPdfUri(context: Context, uri: Uri?): String {
//        val inputStream: InputStream?
//        try {
//            inputStream = context.contentResolver.openInputStream(uri!!)
//        } catch (e: FileNotFoundException) {
//            throw RuntimeException(e)
//        }
//
//        val stringBuilder = StringBuilder()
//
//        Thread {
//            var reader: PdfReader? = null
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    var text = ""
//                    reader = PdfReader(inputStream)
//
//                    val pages: Int = reader.getNumberOfPages()
//                    for (i in 0 until pages) {
//                        text = PdfTextExtractor.getTextFromPage(reader, i)
//                    }
//                    stringBuilder.append(text)
//                }
//                reader!!.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }.start()
//
//        return stringBuilder.toString()
//    }

    // Reads the text from the content uri and writes it to a file in internal storage
    fun writeUriToFile(context: Context, uri: Uri?, filename: String): Boolean {
        // Reads data from uri into a string
        val text: String?
        val name = getFilename(context, uri)
        when (name.substring(name.lastIndexOf(".") + 1)) {
            "txt" -> {
                text = readTxtUri(context, uri)
                if (text.isNullOrBlank()) {
                    Toast.makeText(context, "Failed reading .txt", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, "Successfully read .txt", Toast.LENGTH_SHORT).show()
                }
            }

            "pdf" -> {
                text = readPdfUri(context, uri)
                if (text.isBlank()) {
                    Toast.makeText(context, "Failed reading .pdf", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, "Successfully read .pdf", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                Toast.makeText(context, "Reading invalid file", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        // Creates a file object which references internal storage
        val file = File(context.filesDir, "$filename.txt")
        // Writes the text to the file
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            outputStream.write(text!!.toByteArray())
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    // Gets the filename from a uri
    fun getFilename(context: Context, uri: Uri?): String {
        val cursor = context.contentResolver.query(uri!!, null, null, null, null)

        // Throws an exception if the cursor is empty
        if (cursor!!.count <= 0) {
            cursor.close()
            throw IllegalArgumentException("Can't get filename.'")
        }

        // Gets the display name of the file
        cursor.moveToFirst()
        val filename = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        cursor.close()

        return filename
    }
}
