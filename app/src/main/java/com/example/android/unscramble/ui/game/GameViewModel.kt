package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _score = MutableLiveData(0)
    private val _currentWordCount = MutableLiveData(0)
    private val _currentScrambledWord = MutableLiveData<String>()
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var _currentWord: String

    //BACKING PROPERTY ->
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    val score: MutableLiveData<Int>
        get() = _score

    val currentWordCount: MutableLiveData<Int>
        get() = _currentWordCount

    val currentWord:String
        get() = _currentWord

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }


    private fun getNextWord() {
        _currentWord = allWordsList.random()
        val tempWord = _currentWord.toCharArray()
        tempWord.shuffle()

        while (tempWord.toString().equals(_currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(_currentWord)) {
            getNextWord()
        } else {

            //.value is used to access data within a LiveData object

            _currentScrambledWord.value = String(tempWord)

            // inc() method is used to increment the value of _currentWordCount by one
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(_currentWord)

        }
    }

    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false

    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(_currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun reInitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}