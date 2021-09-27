package com.medzin.board_game_collector

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.database.objects.GameRank
import com.medzin.board_game_collector.databinding.ActivityBGGBinding
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import com.medzin.board_game_collector.util.BGGapiClient
import com.medzin.board_game_collector.xml_parser.GameDetailsParser
import com.medzin.board_game_collector.xml_parser.UserCollectionParser
import java.time.LocalDate

class BGGActivity : AppCompatActivity() {
    private val TAG = "BGGActivity"

    private lateinit var binding: ActivityBGGBinding
    private lateinit var user: String
    private lateinit var games: Array<Game>
    private var ifGetRanks: Boolean = false
    private lateinit var gameOrgTitlesToRefresh: Map<Int, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, getString(R.string.log_init))
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        binding = ActivityBGGBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.userName.hint = getString(R.string.username_hint)
        binding.userName.setOnFocusChangeListener { _, _ ->
            binding.userName.hint = ""
        }

        binding.bggTitle.text = getString(R.string.bgg_title)

        binding.importUserGamesBtn.text = getString(R.string.import_user_btn)
        binding.manualnputBtn.text = getString(R.string.refresh_ranks_btn)
        Log.v(TAG, getString(R.string.log_init_done))
    }

    fun addGame(game: Game?, dbHandler: GameDBHandler) {
        if(game != null){
            game.dateOfCollecting = LocalDate.now()

            try {
                dbHandler.addGame(game)
            } catch (ie: SQLiteConstraintException) {}
        }
    }

    fun refreshRank(gameOrgTitle: String, rank: GameRank, dbHandler: GameDBHandler) {
        try {
            dbHandler.addRank(gameOrgTitle, rank)
        } catch (ie: SQLiteConstraintException) {}
    }

    fun findUserCollection(v: View) {
        if (binding.userName.text.isNotEmpty()){
            UserSearchTask().execute()
            Toast.makeText(this@BGGActivity,
                    getString(R.string.user_games_import_began), Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, getString(R.string.empty_username_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshRankings(v: View) {
        val dbHandler = GameDBHandler(this, null, null, 1)
        gameOrgTitlesToRefresh = mapOf()
        dbHandler.getGamesCollection().forEach {
            if (it.bggId > 0) gameOrgTitlesToRefresh = gameOrgTitlesToRefresh.plus(Pair(it.bggId, it.originalTitle))
        }
        ifGetRanks = true
        if (binding.userName.text.isNotEmpty()){
            UserSearchTask().execute()
        }
        else {
            games = arrayOf()
            RefreshRanksTask().execute()
        }
        Toast.makeText(this@BGGActivity,
                getString(R.string.ranks_refresh_began), Toast.LENGTH_SHORT).show()
    }

    fun goBack(v: View) {
        Log.v(TAG, getString(R.string.log_end))
        setResult(1)
        this.finish()
    }

    private inner class UserSearchTask: AsyncTask<String, Int, Array<Game>>(){

        override fun onPostExecute(result: Array<Game>?) {
            super.onPostExecute(result)
            if (result == null){
                Toast.makeText(this@BGGActivity,
                    getString(R.string.user_notfound_msg), Toast.LENGTH_SHORT).show()
            }
            else{
                games = result
                if (ifGetRanks) {
                    RefreshRanksTask().execute()
                }
                else {
                    AddMultipleGames().execute()
                }
            }
        }

        override fun doInBackground(vararg params: String?): Array<Game> {
            Log.v(TAG, getString(R.string.log_api_user_search))
            user = binding.userName.text.toString()
            var i = 0
            while (i in 0..5) {
                try {
                    if (BGGapiClient.getUserCollection(this@BGGActivity, user, ifGetRanks)) {
                        Log.v(TAG, getString(R.string.log_user_found))
                        return UserCollectionParser.parse(this@BGGActivity)
                    }
                } catch (e: ResultNotReadyException) {
                    i++
                    Thread.sleep(1000)
                }
            }
            Log.v(TAG, getString(R.string.log_user_not_found))
            return arrayOf()
        }

    }

    inner class AddMultipleGames: AsyncTask<String, Int, Boolean>(){

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Toast.makeText(this@BGGActivity,
                getString(R.string.user_games_import_success_msg), Toast.LENGTH_SHORT).show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            var i: Int
            val dbHandler = GameDBHandler(this@BGGActivity, null, null, 1)
            if (games.isNotEmpty()) Log.v(TAG, getString(R.string.log_user_collection_download))
            games.forEach {
                i = 0
                while (i in 0..5) {
                    try {
                        if (it.bggId > 0 && BGGapiClient.getGameDetails(this@BGGActivity, it.bggId)){
                            val game = it.title?.let {
                                it1 -> GameDetailsParser.parse(this@BGGActivity, it1)
                            }
                            if (game != null) {
                                game.comment = it.comment
                            }
                            addGame(game, dbHandler)
                            i = -1
                        }
                    } catch (e: ResultNotReadyException) {
                        i++
                        Thread.sleep(1000)
                    }
                }
            }
            if (games.isNotEmpty()) Log.v(TAG, getString(R.string.log_user_collection_download_done))
            return true
        }

    }

    inner class RefreshRanksTask: AsyncTask<String, Int, Boolean>(){

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Toast.makeText(this@BGGActivity,
                    getString(R.string.ranks_refresh_success_msg), Toast.LENGTH_SHORT).show()
            ifGetRanks = false
        }

        override fun doInBackground(vararg params: String?): Boolean {
            val dbHandler = GameDBHandler(this@BGGActivity, null, null, 1)
            Log.v(TAG, getString(R.string.log_ranks_refresh))
            games.forEach {
                gameOrgTitlesToRefresh[it.bggId]?.let { it1 ->
                    refreshRank(
                            it1,
                            GameRank(it.currRank, LocalDate.now()),
                            dbHandler
                    )
                }
                gameOrgTitlesToRefresh = gameOrgTitlesToRefresh.minus(it.bggId)
            }
            var i: Int
            gameOrgTitlesToRefresh.keys.forEach{
                i = 0
                while (i in 0..10) {
                    try {
                        if (it > 0 && BGGapiClient.getGameDetails(this@BGGActivity, it)){
                            val game = GameDetailsParser.parse(this@BGGActivity, "")
                            gameOrgTitlesToRefresh[it]?.let { it1 ->
                                if (game != null) {
                                    refreshRank(
                                            it1,
                                            GameRank(game.currRank, LocalDate.now()),
                                            dbHandler
                                    )
                                }
                            }
                            i = -1
                        }
                    } catch (e: ResultNotReadyException) {
                        i++
                        Thread.sleep(1000)
                    }
                }
            }
            Log.v(TAG, getString(R.string.log_ranks_refresh_done))
            return true
        }

    }

}