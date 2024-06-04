package com.example.linguareader.data

class BookModel {
    var id: Int
    var title: String
    var author: String
    var filepath: String
    var language: Int
    var rating: Int
    var progress: Int

    constructor(id: Int, title: String, author: String, filepath: String) {
        this.id = id
        this.title = title
        this.author = author
        this.filepath = filepath
        this.language = -1
        this.rating = 0
        this.progress = 0
    }

    constructor(id: Int, title: String, author: String, filepath: String, language: Int) {
        this.id = id
        this.title = title
        this.author = author
        this.filepath = filepath
        this.language = language
        this.rating = 0
        this.progress = 0
    }

    override fun toString(): String {
        return "$title by $author"
    }
}
