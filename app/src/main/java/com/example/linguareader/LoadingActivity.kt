package com.example.linguareader

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.linguareader.ui.reading.ReadActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // Gets SharedPreferences to get the user's target language
        //val sharedPreferences = getSharedPreferences(UserPreferences.PREFERENCES, MODE_PRIVATE)
        val extras = intent.extras
        val dbHelper = DatabaseHelper(this)

        if (extras != null) {
            // Sets the source and target language
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(dbHelper.getLanguage(extras.getInt("LANGUAGE"))!!.code)
                .setTargetLanguage(
//                    sharedPreferences.getString(
//                        UserPreferences.TARGET_LANGUAGE,
//                        TranslateLanguage.ENGLISH
//                    )!!
                    TranslateLanguage.ENGLISH
                )
                .build()
            val translator = Translation.getClient(options)
            val conditions = DownloadConditions.Builder().requireWifi().build()
            // Downloads the required model if necessary and informs the user
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener { // Starts ReadActivity, passing book data as extras
                    val intent = Intent(this@LoadingActivity, ReadActivity::class.java)
                    intent.putExtra("TITLE", extras.getString("TITLE"))
                    intent.putExtra("AUTHOR", extras.getString("AUTHOR"))
                    intent.putExtra("FILEPATH", extras.getString("FILEPATH"))
                    intent.putExtra("LANGUAGE", extras.getInt("LANGUAGE"))
                    startActivity(intent)
                    finish()
                }
        }
    }
}