package com.medzin.board_game_collector

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.databinding.ActivityAddFromBGGBinding
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import com.medzin.board_game_collector.util.BGGapiClient
import com.medzin.board_game_collector.xml_parser.GameDetailsParser
import com.medzin.board_game_collector.xml_parser.SearchedGamesParser
import com.medzin.board_game_collector.xml_parser.UserCollectionParser
import java.time.LocalDate

class AddFromBGG : AppCompatActivity() {

    private lateinit var binding: ActivityAddFromBGGBinding
    private var bggID: Int? = -1
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFromBGGBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.gameTitle.hint = getString(R.string.game_title_hint)
        binding.gameTitle.setOnFocusChangeListener { _, _ ->
            binding.gameTitle.hint = ""
        }

        binding.searchBtn.text = getString(R.string.search_games_btn)
        binding.manualnputBtn.text = getString(R.string.manual_game_input_btn)

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
                val game = TextView(this)
                game.text = title
                game.setOnClickListener{ _ ->
                    bggID = result[title]
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

            dbHandler.addGame(game)
            binding.gameTitle.setText("")
            Toast.makeText(this, getString(R.string.add_success_msg), Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, getString(R.string.add_failed_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun search(v: View) {
        if (!binding.gameTitle.text.isEmpty()){
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
        setResult(1)
        this.finish()
    }

    private inner class GamesSearchTask: AsyncTask<String, Int, Map<String, Int>>(){
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Map<String, Int>?) {
            super.onPostExecute(result)
            loadSearchResult(result)
        }

        override fun doInBackground(vararg params: String?): Map<String, Int> {
            title = binding.gameTitle.text.toString()
            var i = 0
            while (i in 0..5) {
                try {
                    if (BGGapiClient.searchGame(this@AddFromBGG, title)) {
                        return SearchedGamesParser.parse(this@AddFromBGG)
                    }
                } catch (e: ResultNotReadyException){
                    i++
                    Thread.sleep(1000)
                }
            }
            return mapOf()
        }

    }

    private inner class GameDetailsQuery: AsyncTask<String, Int, Game?>(){
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Game?) {
            super.onPostExecute(result)
            addGame(result)
        }

        override fun doInBackground(vararg params: String?): Game? {
            var i = 0
            while (i in 0..5) {
                try {
                    if (bggID != null && bggID!! > 0 &&
                            BGGapiClient.getGameDetails(this@AddFromBGG, bggID!!)) {
                        return GameDetailsParser.parse(this@AddFromBGG, title)
                    }
                } catch (e: ResultNotReadyException){
                    e.printStackTrace()
                    i++
                    Thread.sleep(1000)
                }
            }
            return null
        }

    }

}