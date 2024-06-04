package com.example.linguareader.ui.reading

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.R
import com.example.linguareader.UserPreferences
import com.example.linguareader.data.WordModel
import com.example.linguareader.utils.FileUtils
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlin.math.min


class ReadActivity : AppCompatActivity(), PageRecyclerViewInterface {
    var userPreferences: UserPreferences? = null
    private lateinit var dbHelper: DatabaseHelper

    // Page
    private lateinit var rvPage: RecyclerView
    private lateinit var words: Array<String>
    private var wordViews: ArrayList<TextView>? = null
    private var translationPopup: PopupWindow? = null
    private var selectedWord: TextView? = null

    // Translation
    private var options: TranslatorOptions? = null
    private var translator: Translator? = null
    private var language: Int = -1

    // Display settings
    val textSize: Float = 20f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        var text = ""
        // Gets extras passed through the intent
        val extras: Bundle? = intent.extras
        // Sets the action bar title and subtitle
        if (extras != null) {
            supportActionBar!!.title = extras.getString("TITLE")
            supportActionBar!!.subtitle = extras.getString("AUTHOR")
            // Reads the book content from internal storage
            text = FileUtils.readFile(this, extras.getString("FILEPATH")!!)
            language = extras.getInt("LANGUAGE")
        }

        // Initialises preferences, the database helper, and translator
        userPreferences = application as UserPreferences?
        dbHelper = DatabaseHelper(applicationContext)
        initializeTranslator()

        // Displays the back button
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Creates a list of words
        wordViews = ArrayList()
        words = text.split("\\s+".toRegex()).toTypedArray()
        var wordIndex = 0

        // Sets the page content from the list of words
        // using a RecyclerView and FlexLayoutManager
        rvPage = findViewById(R.id.rv_page)
        val pageAdapter = PageRecyclerViewAdapter(
            applicationContext,
            words,
            this,
            textSize
        )
        rvPage.setAdapter(pageAdapter)
        rvPage.setLayoutManager(FlexboxLayoutManager(applicationContext))
    }

    // Adds functionality to the back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Initialises the translator based on book data and shared preferences
    private fun initializeTranslator() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(
            UserPreferences.PREFERENCES,
            Context.MODE_PRIVATE
        )

        // Sets the source and target language
        options = TranslatorOptions.Builder()
            .setSourceLanguage(dbHelper.getLanguage(language)!!.code)
            .setTargetLanguage(
                sharedPreferences.getString(
                    UserPreferences.TARGET_LANGUAGE,
                    TranslateLanguage.ENGLISH
                )!!
            )
            .build()
        translator = Translation.getClient(options!!)
    }

    // Creates the translation popup
    private fun createPopup(text: String, x: Int, y: Int): PopupWindow? {
        if (selectedWord == null) return null

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_translation, null)

        val radioGroup = popupView.findViewById<RadioGroup>(R.id.familiarity_radio_group)
        val unknownButton = popupView.findViewById<RadioButton>(R.id.radio_unknown)
        val learningButton = popupView.findViewById<RadioButton>(R.id.radio_learning)
        val knownButton = popupView.findViewById<RadioButton>(R.id.radio_known)

        // Configures the translation TextView
        val translationText = popupView.findViewById<TextView>(R.id.translation_popup_text)
        translationText.text = text
        translationText.textSize = textSize


        // Queries for the trimmed word in the database
        val word = dbHelper.getWord(
            selectedWord!!.text.toString().replace("[^\\s\\p{L}0-9]".toRegex(), "")
        )
            ?: return null

        when (word.familiarity) {
            0 -> radioGroup.check(R.id.radio_unknown)
            1 -> radioGroup.check(R.id.radio_learning)
            2 -> radioGroup.check(R.id.radio_known)
            else -> {}
        }
        // Creates the popup window from the view
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Adds an onClick the set the word's familiarity, updating the database and RadioButton
        unknownButton.setOnClickListener {
            word.familiarity = 0
            dbHelper.updateWord(word)
            popupWindow.dismiss()
        }

        // Adds an onClick the set the word's familiarity, updating the database and RadioButton
        learningButton.setOnClickListener {
            word.familiarity = 1
            dbHelper.updateWord(word)
            popupWindow.dismiss()
        }

        // Adds an onClick the set the word's familiarity, updating the database and RadioButton
        knownButton.setOnClickListener {
            word.familiarity = 2
            dbHelper.updateWord(word)
            popupWindow.dismiss()
        }

        // Shows the PopupWindow at the pressed word's location
        popupWindow.showAtLocation(findViewById(R.id.page_parent), Gravity.NO_GRAVITY, x, y)
        // Adds an onDismiss to return the word's highlighting to the correct state
        popupWindow.setOnDismissListener {
            when (word.familiarity) {
                0 -> selectedWord!!.setBackgroundResource(R.drawable.bg_word_unknown)
                1 -> selectedWord!!.setBackgroundResource(R.drawable.bg_word_learning)
                2 -> selectedWord!!.setBackgroundColor(0)
                else -> {}
            }
        }

        return popupWindow
    }

    override fun onItemClick(pos: Int, textWord: TextView) {
        // Highlights the TextView
        selectedWord = textWord
        val word = words[pos]
        selectedWord!!.setBackgroundResource(R.drawable.bg_word_selected)
        // Gets the TextView's location
        val wordLocation = IntArray(2)
        textWord.getLocationOnScreen(wordLocation)
        val trimmedWord = word.replace("[^\\s\\p{L}0-9]".toRegex(), "")

        // Translates the trimmed words and creates a translation popup onSuccess
        translator!!.translate(trimmedWord).addOnSuccessListener { translation ->
            // Adds the word to the database if necessary
            if (dbHelper.getWord(trimmedWord) == null) {
                dbHelper.addWord(
                    WordModel(
                        -1,
                        trimmedWord,
                        translation,
                        language
                    )
                )
            }
            // Creates a translation popup at the TextView's location
            translationPopup = createPopup(
                translation,
                wordLocation[0],
                wordLocation[1] + textWord.measuredHeight
            )
        }
    }
}