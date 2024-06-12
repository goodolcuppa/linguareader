# Linguareader
Language learning tool built using Android Studio and Kotlin

# Overview
- Login and registration using [Firebase Authentication](https://firebase.google.com/docs/auth/)
- Imported content and data managed using [SQLite](https://developer.android.com/training/data-storage/sqlite)
- Import reading content as .txt files
- Read content with word highlighting and translation using [Google ML Kit](https://developers.google.com/ml-kit/language/translation/)
- Import flashcard decks as .txt files using custom formatting (word:translation;next word:next translation)
- Study flashcard decks

For testing purposes, use files stored under `TestFiles` to import into the app.

# Login
The login page allows users to create or login with an account using email and password, or bypass this step as a local user.

<p align="center">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/91275f91-7dd7-41e5-a726-16be5750d187" width="200">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/57f250b2-f4dd-4bb5-b812-6bedbf810492" width="200">
</p>

# Home
The home page provides quick access to the last read book, as well as a compact list of all imported books and decks.

<p align="center">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/86e77c4b-cf09-430b-aed2-e2430588873b" width="200">
</p>

# Library
The library page allows users to manage books in the app: importing, deleting, filtering, and reading.

Books can be filtered by search term or by language using the filter bar.

<p align="center">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/7463dd86-1085-4a08-a821-b06e759b1c47" width="200">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/412c457a-dea3-44b5-b088-9b3855812d49" width="200">
</p>

# Flashcards
The flashcards page allows users to manage decks in the app: importing, deleting, filtering, and studying.

Decks can be filtered in the same way as books by search term or by language using the filter bar.

<p align="center">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/ac77cfb0-6cdd-4c6b-aac7-54a9dba33629" width="200">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/65fc0981-7ce7-4268-ae03-481137aaa205" width="200">
</p>

# Reading
The reading page is content is displayed from the selected book. 

Unknown and unfamiliar words are highlighted, and tapping on a word will display a popup. This popup shows the word's translation, and allows user's to decide how familiar the word is.

Before entering this page, the correct translation model is downloaded onto the device (only for the first book with each language) so that translations can be displayed even offline.

<p align="center">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/37eed92b-12e5-4e0a-85be-a92b75d9c0f6" width="200">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/ac327db4-f98e-4f83-bd3d-a7ef36fed6a6" width="200">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/8f0544a9-cfc0-4b3e-a0c7-1a5aed2bbe23" width="200">
</p>

# Studying
The studying page is where cards are studied from the selected deck. 

After revealing a word, users should determine whether they knew the word or not. This will ammend the word's familiarity in the database, which is the same value used to determine a word's highlighting in the reading page.

<p align="center">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/1d4cec6e-39b8-44b9-b803-bf3acc197cdb" width="200">
      <img src="https://github.com/goodolcuppa/linguareader/assets/38227160/0caa3bc3-412c-4fa6-8b71-5f16a14c4a42" width="200">
</p>
