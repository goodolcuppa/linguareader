package com.example.linguareader

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linguareader.data.WordModel

class StudyActivity : AppCompatActivity() {
    private lateinit var cardFront: LinearLayout
    private lateinit var cardBack: LinearLayout
    private lateinit var textWord: TextView
    private lateinit var textTranslation: TextView
    private lateinit var studyProgress: ProgressBar
    private var wordList: List<WordModel>? = null
    private var deckIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val dbHelper = DatabaseHelper(this)

        // Gets the intent extras, setting the action bar title and wordList
        val extras: Bundle? = intent.extras
        if (extras != null) {
            supportActionBar!!.title = extras.getString("NAME")
            wordList = dbHelper.getCards(extras.getInt("ID"))
        }

        // Displays the back button
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        deckIndex = 0

        cardFront = findViewById(R.id.card_front)
        cardBack = findViewById(R.id.card_back)

        textWord = findViewById(R.id.text_word)
        textTranslation = findViewById(R.id.text_translation)
        studyProgress = findViewById(R.id.study_progress)

        val btnReveal = findViewById<Button>(R.id.btn_reveal)
        val btnKnown = findViewById<Button>(R.id.btn_known)
        val btnUnknown = findViewById<Button>(R.id.btn_unknown)

        // Shows cardBack onClick
        btnReveal.setOnClickListener {
            cardFront.visibility = View.GONE
            cardBack.visibility = View.VISIBLE
        }

        // Shows cardFront, updates word familiarity and shows the next card's data onClick
        btnKnown.setOnClickListener {
            val word: WordModel = wordList!![deckIndex]
            word.incrementFamiliarity()
            dbHelper.updateWord(word)
            deckIndex++
            showCard()
        }

        // Shows cardFront, updates word familiarity and shows the next card's data onClick
        btnUnknown.setOnClickListener {
            val word: WordModel = wordList!![deckIndex]
            word.decrementFamiliarity()
            dbHelper.updateWord(word)
            deckIndex++
            showCard()
        }

        showCard()
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

    // Sets up the TextViews and layouts to display the next card,
    // or ends the activity if the deck is finished
    private fun showCard() {
        if (deckIndex < wordList!!.size) {
            studyProgress.progress =
                (100 * ((deckIndex.toFloat()) / wordList!!.size)).toInt()
            textWord.text = wordList!![deckIndex].word
            textTranslation.text = wordList!![deckIndex].translation
            cardFront.visibility = View.VISIBLE
            cardBack.visibility = View.GONE
        } else {
            finish()
        }
    }
}