package com.medzin.board_game_collector

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.databinding.ActivityBGGBinding
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import com.medzin.board_game_collector.util.BGGapiClient
import com.medzin.board_game_collector.xml_parser.GameDetailsParser
import com.medzin.board_game_collector.xml_parser.UserCollectionParser
import java.time.LocalDate

class BGGActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBGGBinding
    private lateinit var user: String
    private lateinit var games: Array<Game>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBGGBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.userName.hint = getString(R.string.username_hint)
        binding.userName.setOnFocusChangeListener { _, _ ->
            binding.userName.hint = ""
        }

        binding.importUserGamesBtn.text = getString(R.string.import_user_btn)
        binding.manualnputBtn.text = getString(R.string.refresh_ranks_btn)
    }

    fun addGame(game: Game?) {
        if(game != null){
            val dbHandler = GameDBHandler(this, null, null, 1)

            game.dateOfCollecting = LocalDate.now()

            dbHandler.addGame(game)
        }
    }

    fun findUserCollection(v: View) {
        if (!binding.userName.text.isEmpty()){
            UserSearchTask().execute()
        }
        else {
            Toast.makeText(this, getString(R.string.empty_username_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshRankings(v: View) {

    }

    private inner class UserSearchTask: AsyncTask<String, Int, Array<Game>>(){
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Array<Game>?) {
            super.onPostExecute(result)
            if (result == null){
                Toast.makeText(this@BGGActivity,
                    getString(R.string.user_notfound_msg), Toast.LENGTH_SHORT).show()
            }
            else{
                games = result
                AddMultipleGames().execute()
                Toast.makeText(this@BGGActivity,
                        getString(R.string.user_games_import_began), Toast.LENGTH_SHORT).show()
            }
        }

        override fun doInBackground(vararg params: String?): Array<Game> {
            user = binding.userName.text.toString()
            var i = 0
            while (i in 0..5) {
                try {
                    if (BGGapiClient.getUserCollection(this@BGGActivity, user)) {
                        return UserCollectionParser.parse(this@BGGActivity)
                    }
                } catch (e: ResultNotReadyException) {
                    i++
                    Thread.sleep(1000)
                }
            }
            return arrayOf()
        }

    }

    inner class AddMultipleGames: AsyncTask<String, Int, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Toast.makeText(this@BGGActivity,
                getString(R.string.user_games_import_success_msg), Toast.LENGTH_SHORT).show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            var i = 0
            games.forEach {
                i = 0
                while (i in 0..5) {
                    try {
                        if (it.bggId > 0 && BGGapiClient.getGameDetails(this@BGGActivity, it.bggId)){
                            addGame(it.title?.let { it1 ->
                                GameDetailsParser.parse(this@BGGActivity, it1)
                            })
                            i = -1
                        }
                    } catch (e: ResultNotReadyException) {
                        i++
                        Thread.sleep(1000)
                    }
                }
            }
            return true
        }

    }

}