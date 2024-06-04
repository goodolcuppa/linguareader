package com.example.linguareader.data

class WordModel {
    var id: Int
    var word: String
    var translation: String
    var language: Int
    var familiarity: Int

    constructor(id: Int, word: String, translation: String, language: Int) {
        this.id = id
        this.word = word
        this.translation = translation
        this.language = language
        this.familiarity = 0
    }

    constructor(id: Int, word: String, translation: String, language: Int, familiarity: Int) {
        this.id = id
        this.word = word
        this.translation = translation
        this.language = language
        this.familiarity = familiarity
    }

    fun incrementFamiliarity(): Boolean {
        if (familiarity < 2) {
            familiarity += 1
            return true
        }
        return false
    }

    fun decrementFamiliarity(): Boolean {
        if (familiarity > 0) {
            familiarity -= 1
            return true
        }
        return false
    }
}
