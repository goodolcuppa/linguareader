package com.example.linguareader

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.util.Log
import com.example.linguareader.data.BookModel
import com.example.linguareader.data.DeckModel
import com.example.linguareader.data.LanguageModel
import com.example.linguareader.data.WordModel
import com.google.mlkit.nl.translate.TranslateLanguage

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, "name", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        val createBookTableStatement =
            "CREATE TABLE " + BOOK_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BOOK_TITLE_FIELD + " TEXT, " +
                    BOOK_AUTHOR_FIELD + " TEXT, " +
                    BOOK_FILEPATH_FIELD + " TEXT, " +
                    BOOK_LANGUAGE_FIELD + " INT, " +
                    BOOK_RATING_FIELD + " INT, " +
                    BOOK_PROGRESS_FIELD + " INT)"
        db.execSQL(createBookTableStatement)

        val createDeckTableStatement =
            "CREATE TABLE " + DECK_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DECK_NAME_FIELD + " TEXT, " +
                    DECK_LANGUAGE_FIELD + " INT, " +
                    DECK_SIZE_FIELD + " INT, " +
                    DECK_PROGRESS_FIELD + " INT)"
        db.execSQL(createDeckTableStatement)

        val createWordTableStatement =
            "CREATE TABLE " + WORD_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WORD_ORIGINAL_FIELD + " TEXT, " +
                    WORD_TRANSLATION_FIELD + " TEXT, " +
                    WORD_LANGUAGE_FIELD + " INT, " +
                    WORD_FAMILIARITY_FIELD + " INT)"
        db.execSQL(createWordTableStatement)

        val createCardsTableStatement =
            "CREATE TABLE " + CARD_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CARD_DECK_ID_FIELD + " INT, " +
                    CARD_WORD_ID_FIELD + " INT)"
        db.execSQL(createCardsTableStatement)

        val createLanguagesTableStatement =
            "CREATE TABLE " + LANGUAGE_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LANGUAGE_CODE_FIELD + " TEXT, " +
                    LANGUAGE_COLOR_FIELD + " INT)"
        db.execSQL(createLanguagesTableStatement)

        var languageIndex = 0
        val TranslateLanguages: List<String> = TranslateLanguage.getAllLanguages()
        val languageList: MutableList<LanguageModel> = ArrayList<LanguageModel>()
        for (language in TranslateLanguages) {
            val hsv = FloatArray(3)
            val hue = ((360f / TranslateLanguages.size) * languageIndex).toInt()
            Log.d(language, "" + hue)
            hsv[0] = hue.toFloat()
            hsv[1] = 0.5f
            hsv[2] = 1f

            languageList.add(
                LanguageModel(
                    -1,
                    language,
                    Color.HSVToColor(hsv)
                )
            )
            languageIndex += 1
        }

        val cv: ContentValues = ContentValues()

        for (language in languageList) {
            cv.put(LANGUAGE_CODE_FIELD, language.code)
            cv.put(LANGUAGE_COLOR_FIELD, language.color)
            db.insert(LANGUAGE_TABLE, null, cv)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $BOOK_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $DECK_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $WORD_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $CARD_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $LANGUAGE_TABLE")
        onCreate(db)
    }

    fun addBook(book: BookModel): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val cv: ContentValues = ContentValues()

        cv.put(BOOK_TITLE_FIELD, book.title)
        cv.put(BOOK_AUTHOR_FIELD, book.author)
        cv.put(BOOK_FILEPATH_FIELD, book.filepath)
        cv.put(BOOK_LANGUAGE_FIELD, book.language)
        cv.put(BOOK_RATING_FIELD, book.rating)
        cv.put(BOOK_PROGRESS_FIELD, book.progress)

        val insert: Long = db.insert(BOOK_TABLE, null, cv)
        db.close()
        return insert
    }

    fun addDeck(deck: DeckModel): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val cv: ContentValues = ContentValues()

        cv.put(DECK_NAME_FIELD, deck.name)
        cv.put(DECK_LANGUAGE_FIELD, deck.language)
        cv.put(DECK_SIZE_FIELD, deck.size)
        cv.put(DECK_PROGRESS_FIELD, deck.progress)

        val insert: Long = db.insert(DECK_TABLE, null, cv)
        db.close()
        return insert
    }

    fun addWord(word: WordModel): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val cv: ContentValues = ContentValues()

        cv.put(WORD_ORIGINAL_FIELD, word.word)
        cv.put(WORD_TRANSLATION_FIELD, word.translation)
        cv.put(WORD_LANGUAGE_FIELD, word.language)
        cv.put(WORD_FAMILIARITY_FIELD, word.familiarity)

        val insert: Long = db.insert(WORD_TABLE, null, cv)
        db.close()
        return insert
    }

    fun addCard(deckId: Long, wordId: Long): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val cv: ContentValues = ContentValues()

        cv.put(CARD_DECK_ID_FIELD, deckId)
        cv.put(CARD_WORD_ID_FIELD, wordId)

        val insert: Long = db.insert(CARD_TABLE, null, cv)
        db.close()
        return insert
    }

    fun addLanguage(language: LanguageModel): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val cv: ContentValues = ContentValues()

        cv.put(LANGUAGE_CODE_FIELD, language.code)
        cv.put(LANGUAGE_COLOR_FIELD, language.color)

        val insert: Long = db.insert(LANGUAGE_TABLE, null, cv)
        db.close()
        return insert
    }

    fun updateWord(word: WordModel): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv: ContentValues = ContentValues()

        cv.put(WORD_ORIGINAL_FIELD, word.word)
        cv.put(WORD_TRANSLATION_FIELD, word.translation)
        cv.put(WORD_LANGUAGE_FIELD, word.language)
        cv.put(WORD_FAMILIARITY_FIELD, word.familiarity)

        val update: Long =
            db.update(WORD_TABLE, cv, "id=?", arrayOf<String>(word.id.toString())).toLong()
        db.close()
        return update > 0
    }

    fun getBook(id: Int): BookModel? {
        val query = "SELECT * FROM $BOOK_TABLE WHERE id=$id"
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        var bookModel: BookModel? = null
        if (cursor.moveToFirst()) {
            val title = cursor.getString(1)
            val author = cursor.getString(2)
            val filepath = cursor.getString(3)
            val language = cursor.getInt(4)
            val rating = cursor.getInt(5)
            val progress = cursor.getInt(6)

            bookModel = BookModel(id, title, author, filepath, language)
        }

        cursor.close()
        db.close()
        return bookModel
    }

    fun getWord(word: String): WordModel? {
        val query = "SELECT * FROM " + WORD_TABLE +
                " WHERE " + WORD_ORIGINAL_FIELD + "='" + word + "'"
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        var wordModel: WordModel? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val original = cursor.getString(1)
            val translation = cursor.getString(2)
            val language = cursor.getInt(3)
            val familiarity = cursor.getInt(4)

            wordModel = WordModel(id, original, translation, language, familiarity)
        }

        cursor.close()
        db.close()
        return wordModel
    }

    fun getLanguage(code: String): LanguageModel? {
        val query = "SELECT * FROM " + LANGUAGE_TABLE +
                " WHERE " + LANGUAGE_CODE_FIELD + "='" + code + "'"
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        var language: LanguageModel? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val color = cursor.getInt(2)

            language = LanguageModel(id, code, color)
        }

        cursor.close()
        db.close()
        return language
    }

    fun getLanguage(id: Int): LanguageModel? {
        val query = "SELECT * FROM " + LANGUAGE_TABLE +
                " WHERE id=" + id
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        var language: LanguageModel? = null
        if (cursor.moveToFirst()) {
            val code = cursor.getString(1)
            val color = cursor.getInt(2)

            language = LanguageModel(id, code, color)
        }

        cursor.close()
        db.close()
        return language
    }

    fun deleteBook(id: Int): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        return db.delete(BOOK_TABLE, "id=?", arrayOf(id.toString())) > 0
    }

    fun deleteDeck(id: Int): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        if (db.delete(DECK_TABLE, "id=?", arrayOf(id.toString())) > 0) {
            return db.delete(CARD_TABLE, "$CARD_DECK_ID_FIELD=?", arrayOf(id.toString())) > 0
        }
        return false
    }

    val books: ArrayList<BookModel>
        get() {
            val bookList: ArrayList<BookModel> = ArrayList<BookModel>()

            val query = "SELECT * FROM $BOOK_TABLE"
            val db: SQLiteDatabase = this.readableDatabase
            val cursor: Cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val title = cursor.getString(1)
                    val author = cursor.getString(2)
                    val filepath = cursor.getString(3)
                    val language = cursor.getInt(4)
                    val rating = cursor.getInt(5)
                    val progress = cursor.getInt(6)

                    val book: BookModel = BookModel(id, title, author, filepath, language)
                    bookList.add(book)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return bookList
        }

    fun getBooks(language: Int): ArrayList<BookModel> {
        val bookList: ArrayList<BookModel> = ArrayList<BookModel>()

        val query = "SELECT * FROM " + BOOK_TABLE +
                " WHERE " + BOOK_LANGUAGE_FIELD + "=" + language
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val author = cursor.getString(2)
                val filepath = cursor.getString(3)
                val rating = cursor.getInt(5)
                val progress = cursor.getInt(6)

                val book: BookModel = BookModel(id, title, author, filepath, language)
                bookList.add(book)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookList
    }

    fun getBooks(term: String): ArrayList<BookModel> {
        val bookList: ArrayList<BookModel> = ArrayList<BookModel>()

        val query = "SELECT * FROM " + BOOK_TABLE +
                " WHERE " + BOOK_TITLE_FIELD + " LIKE ('%" + term + "%')"
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val author = cursor.getString(2)
                val filepath = cursor.getString(3)
                val language = cursor.getInt(4)
                val rating = cursor.getInt(5)
                val progress = cursor.getInt(6)

                val book: BookModel = BookModel(id, title, author, filepath, language)
                bookList.add(book)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookList
    }

    val decks: ArrayList<DeckModel>
        get() {
            val deckList: ArrayList<DeckModel> = ArrayList<DeckModel>()

            val query = "SELECT * FROM $DECK_TABLE"
            val db: SQLiteDatabase = this.readableDatabase
            val cursor: Cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val name = cursor.getString(1)
                    val language = cursor.getInt(2)
                    val size = cursor.getInt(3)
                    val progress = cursor.getInt(4)

                    val deck: DeckModel = DeckModel(id, name, language, size)
                    deckList.add(deck)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return deckList
        }

    fun getDecks(language: Int): ArrayList<DeckModel> {
        val deckList: ArrayList<DeckModel> = ArrayList<DeckModel>()

        val query = "SELECT * FROM " + DECK_TABLE +
                " WHERE " + DECK_LANGUAGE_FIELD + "=" + language
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val size = cursor.getInt(3)
                val progress = cursor.getInt(4)

                val deck: DeckModel = DeckModel(id, name, language, size)
                deckList.add(deck)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return deckList
    }

    fun getDecks(term: String): ArrayList<DeckModel> {
        val deckList: ArrayList<DeckModel> = ArrayList<DeckModel>()

        val query = "SELECT * FROM " + DECK_TABLE +
                " WHERE " + DECK_NAME_FIELD + " LIKE ('%" + term + "%')"
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val language = cursor.getInt(2)
                val size = cursor.getInt(3)
                val progress = cursor.getInt(4)

                val deck: DeckModel = DeckModel(id, name, language, size)
                deckList.add(deck)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return deckList
    }

    val words: ArrayList<WordModel>
        get() {
            val wordList: ArrayList<WordModel> = ArrayList<WordModel>()

            val query = "SELECT * FROM $WORD_TABLE"
            val db: SQLiteDatabase = this.readableDatabase
            val cursor: Cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val original = cursor.getString(1)
                    val translation = cursor.getString(2)
                    val language = cursor.getInt(3)
                    val familiarity = cursor.getInt(4)

                    val word: WordModel = WordModel(id, original, translation, language)
                    wordList.add(word)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return wordList
        }

    fun getCards(deckId: Int): ArrayList<WordModel> {
        val wordList: ArrayList<WordModel> = ArrayList<WordModel>()

        val query = "SELECT * FROM " + WORD_TABLE +
                " INNER JOIN " + CARD_TABLE +
                " ON " + WORD_TABLE + ".id=" + CARD_TABLE + "." + CARD_WORD_ID_FIELD +
                " WHERE " + CARD_TABLE + "." + CARD_DECK_ID_FIELD + "=" + deckId
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val original = cursor.getString(1)
                val translation = cursor.getString(2)
                val language = cursor.getInt(3)
                val familiarity = cursor.getInt(4)

                val word: WordModel = WordModel(id, original, translation, language, familiarity)
                wordList.add(word)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return wordList
    }

    val languages: ArrayList<LanguageModel>
        get() {
            val languageList: ArrayList<LanguageModel> = ArrayList<LanguageModel>()

            val query = "SELECT * FROM $LANGUAGE_TABLE"
            val db: SQLiteDatabase = this.readableDatabase
            val cursor: Cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val code = cursor.getString(1)
                    val color = cursor.getInt(2)

                    val language: LanguageModel = LanguageModel(id, code, color)
                    languageList.add(language)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return languageList
        }

    val bookLanguages: ArrayList<LanguageModel>
        get() {
            val languageList: ArrayList<LanguageModel> = ArrayList<LanguageModel>()
            val db: SQLiteDatabase = this.readableDatabase
            var cursor: Cursor = db.query(
                true, BOOK_TABLE, arrayOf(BOOK_LANGUAGE_FIELD),
                null, null, BOOK_LANGUAGE_FIELD, null, null, null
            )

            val ids: MutableList<String> = ArrayList()
            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getInt(0).toString())
                } while (cursor.moveToNext())
            }

            val query = "SELECT * FROM " + LANGUAGE_TABLE +
                    " WHERE id in (" + java.lang.String.join(",", ids) + ")"
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val code = cursor.getString(1)
                    val color = cursor.getInt(2)

                    val language: LanguageModel = LanguageModel(id, code, color)
                    languageList.add(language)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return languageList
        }

    val deckLanguages: ArrayList<LanguageModel>
        get() {
            val languageList: ArrayList<LanguageModel> = ArrayList<LanguageModel>()
            val db: SQLiteDatabase = this.readableDatabase
            var cursor: Cursor = db.query(
                true, DECK_TABLE, arrayOf(DECK_LANGUAGE_FIELD),
                null, null, DECK_LANGUAGE_FIELD, null, null, null
            )

            val ids: MutableList<String> = ArrayList()
            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getInt(0).toString())
                } while (cursor.moveToNext())
            }

            val query = "SELECT * FROM " + LANGUAGE_TABLE +
                    " WHERE id in (" + java.lang.String.join(",", ids) + ")"
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val code = cursor.getString(1)
                    val color = cursor.getInt(2)

                    val language: LanguageModel = LanguageModel(id, code, color)
                    languageList.add(language)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return languageList
        }

    companion object {
        // Book table
        const val BOOK_TABLE: String = "books"
        const val BOOK_TITLE_FIELD: String = "title"
        const val BOOK_AUTHOR_FIELD: String = "author"
        const val BOOK_FILEPATH_FIELD: String = "filepath"
        const val BOOK_LANGUAGE_FIELD: String = "language"
        const val BOOK_RATING_FIELD: String = "rating"
        const val BOOK_PROGRESS_FIELD: String = "progress"

        // Deck table
        const val DECK_TABLE: String = "decks"
        const val DECK_NAME_FIELD: String = "name"
        const val DECK_LANGUAGE_FIELD: String = "language"
        const val DECK_SIZE_FIELD: String = "size"
        const val DECK_PROGRESS_FIELD: String = "progress"

        // Word table
        const val WORD_TABLE: String = "words"
        const val WORD_ORIGINAL_FIELD: String = "word"
        const val WORD_TRANSLATION_FIELD: String = "translation"
        const val WORD_LANGUAGE_FIELD: String = "language"
        const val WORD_FAMILIARITY_FIELD: String = "familiarity"

        // Card table
        const val CARD_TABLE: String = "cards"
        const val CARD_DECK_ID_FIELD: String = "deck_id"
        const val CARD_WORD_ID_FIELD: String = "word_id"

        // Language table
        const val LANGUAGE_TABLE: String = "languages"
        const val LANGUAGE_CODE_FIELD: String = "code"
        const val LANGUAGE_COLOR_FIELD: String = "color"
    }
}
