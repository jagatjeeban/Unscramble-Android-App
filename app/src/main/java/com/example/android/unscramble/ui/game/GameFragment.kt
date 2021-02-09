/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {


    private val viewModel: GameViewModel by viewModels()


    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        Log.d("GameFragment", "GameFragment created/re-created!")
        Log.d(
            "GameFragment",
            "Word: ${viewModel.currentScrambledWord}, WordCount: ${viewModel.currentWordCount},Score: ${viewModel.score}"
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }


        // Update the UI
        //updateNextWordOnScreen()
        //OBSERVERS :
//        viewModel.score.observe(viewLifecycleOwner ,{
//            newScore ->
//              binding.score.text = getString(R.string.score, newScore)
//        })

//        viewModel.currentWordCount.observe(viewLifecycleOwner,{
//            newWordCount ->
//               binding.wordCount.text = getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
//        })

        // Observe the scrambledCharArray LiveData, passing in the LifecycleOwner and the observer.
//        viewModel.currentScrambledWord.observe(viewLifecycleOwner,{
//            newWord ->
//            binding.textViewUnscrambledWord.text = newWord
//        })
    }

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isWordCorrect(playerWord)) {
            setErrorTextField(false)

            if (!viewModel.nextWord()) {
                //updateNextWordOnScreen()
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }


    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
            //updateNextWordOnScreen()
        } else {
            showFinalScoreDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.hints_menu, menu)

        val hintsButton = menu.findItem(R.id.word_hint)
        setIcon(hintsButton)


    }

    private fun setIcon(menuItem: MenuItem?){
        if (menuItem == null){
            return
        }
        menuItem.icon = ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_word_hint)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.word_hint ->{

                setIcon(item)
                showWordAlertDialogue()

              return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    private fun showWordAlertDialogue(){
        MaterialAlertDialogBuilder(requireContext())
                .setTitle("It's OK!")
                .setMessage("Word is ${viewModel.currentWord}")
                .setCancelable(false)

                .setPositiveButton("Resume")
                { _,_ -> onSkipWord() }.show()
    }

    /*
     * Gets a random word for the list of words and shuffles the letters in it.
     */
//    private fun getNextScrambledWord(): String {
//        val tempWord = allWordsList.random().toCharArray()
//        tempWord.shuffle()
//        return String(tempWord)
//    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reInitializeData()
        setErrorTextField(false)
        //updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
     * Displays the next scrambled word on screen.
     */
//    private fun updateNextWordOnScreen() {
//        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
//    }

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)

            .setNegativeButton(getString(R.string.exit))
            { _, _ -> exitGame() }
            .setPositiveButton(getString(R.string.play_again))
            { _, _ -> restartGame() }
            .show()
    }
}
