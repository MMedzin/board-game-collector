package com.medzin.board_game_collector

import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.databinding.ActivityAddFromBGGBinding
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import com.medzin.board_game_collector.util.BGGapiClient
import com.medzin.board_game_collector.xml_parser.GameDetailsParser
import com.medzin.board_game_collector.xml_parser.SearchedGamesParser
import java.time.LocalDate

class AddFromBGG : AppCompatActivity() {
    private val TAG = "AddFromBGGActivity"

    private lateinit var binding: ActivityAddFromBGGBinding
    private var bggID: Int? = -1
    private lateinit var title: String
    private lateinit var orgTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, getString(R.string.log_init))
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        binding = ActivityAddFromBGGBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.gameTitle.hint = getString(R.string.game_title_hint)
        binding.gameTitle.setOnFocusChangeListener { _, _ ->
            binding.gameTitle.hint = ""
        }

        binding.searchBtn.text = getString(R.string.search_games_btn)
        binding.manualnputBtn.text = getString(R.string.manual_game_input_btn)

        Log.v(TAG, getString(R.string.log_init_done))
    }

    fun loadSearchResult(result: Map<String, Int>?) {
        binding.searchResults.removeAllViews()
        if (result == null || result.isEmpty()){
            val emptySearchInfo = TextView(this)
            emptySearchInfo.text = getString(R.string.empty_search_info)
            binding.searchResults.addView(emptySearchInfo)
        }
        else {
            for (title: String in result.keys){
                val game = Button(this)
                game.text = title
                game.setOnClickListener{
                    bggID = result[title]
                    orgTitle = title
                    GameDetailsQuery().execute()
                    binding.searchResults.removeAllViews()
                }
                binding.searchResults.addView(game)
            }
        }
    }

    fun addGame(game: Game?) {
        if(game != null){
            val dbHandler = GameDBHandler(this, null, null, 1)

            game.dateOfCollecting = LocalDate.now()

            try {
                dbHandler.addGame(game)
                binding.gameTitle.setText("")
                Toast.makeText(this, getString(R.string.add_success_msg), Toast.LENGTH_SHORT).show()
            } catch(e: SQLiteConstraintException){
                Toast.makeText(this, getString(R.string.game_exists_msg), Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this, getString(R.string.add_failed_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun search(v: View) {
        if (binding.gameTitle.text.isNotEmpty()){
            GamesSearchTask().execute()
        }
        else {
            Toast.makeText(this, getString(R.string.empty_title_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun goToManualNewGame(v: View){
        val i = Intent(this, NewGameActivity::class.java)
        startActivityForResult(i, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setResult(1)
        this.finish()
    }

    fun goBack(v: View) {
        Log.v(TAG, getString(R.string.log_end))
        setResult(1)
        this.finish()
    }

    private inner class GamesSearchTask: AsyncTask<String, Int, Map<String, Int>>(){

        override fun onPostExecute(result: Map<String, Int>?) {
            super.onPostExecute(result)
            loadSearchResult(result)
        }

        override fun doInBackground(vararg params: String?): Map<String, Int> {
            Log.v(TAG, getString(R.string.log_game_search))
            title = binding.gameTitle.text.toString()
            var i = 0
            while (i in 0..5) {
                try {
                    if (BGGapiClient.searchGame(this@AddFromBGG, title)) {
                        Log.v(TAG, getString(R.string.log_game_found))
                        return SearchedGamesParser.parse(this@AddFromBGG)
                    }
                } catch (e: ResultNotReadyException){
                    i++
                    Thread.sleep(1000)
                }
            }
            Log.v(TAG, getString(R.string.log_game_not_found))
            return mapOf()
        }

    }

    private inner class GameDetailsQuery: AsyncTask<String, Int, Game?>(){

        override fun onPostExecute(result: Game?) {
            super.onPostExecute(result)
            addGame(result)
        }

        override fun doInBackground(vararg params: String?): Game? {
            Log.v(TAG, getString(R.string.log_download_game_data))
            var i = 0
            while (i in 0..5) {
                try {
                    if (bggID != null && bggID!! > 0 &&
                            BGGapiClient.getGameDetails(this@AddFromBGG, bggID!!)) {
                        Log.v(TAG, getString(R.string.log_download_game_data_done))
                        return GameDetailsParser.parse(this@AddFromBGG, orgTitle)
                    }
                } catch (e: ResultNotReadyException){
                    i++
                    Thread.sleep(1000)
                }
            }
            Log.v(TAG, getString(R.string.log_download_game_data_failed))
            return null
        }

    }

}