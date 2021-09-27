package com.medzin.board_game_collector

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.databinding.ActivityGameRanksBinding
import java.time.format.DateTimeFormatter

class GameRanksActivity : AppCompatActivity() {
    private val TAG = "GameRanksActivity"

    private lateinit var binding: ActivityGameRanksBinding
    private lateinit var orgTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, getString(R.string.log_init))

        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val extras = intent.extras ?: return
        orgTitle = extras.getString("orgTitle").toString()

        binding = ActivityGameRanksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ranksTitle.text = getString(R.string.ranks_title, orgTitle)

        fillRanksTable()

        Log.v(TAG, getString(R.string.log_init_done))
    }

    private fun fillRanksTable() {
        val ranksTable: TableLayout = binding.ranksTable

        val dbHandler = GameDBHandler(this, null, null, 1)

        ranksTable.removeAllViews()
        dbHandler.getRanks(orgTitle).sortedByDescending{it.sinceDate}.forEach { rank ->
            val row = TableRow(this)
            val tableRowParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            row.layoutParams = tableRowParams
            row.background = ContextCompat.getDrawable(this, R.drawable.border)

            val rankValue = TextView(this)
            rankValue.text = rank.rank.toString()
            rankValue.gravity = Gravity.CENTER
            rankValue.background = ContextCompat.getDrawable(this, R.drawable.border)


            val sinceDate = TextView(this)
            sinceDate.text = rank.sinceDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            sinceDate.gravity = Gravity.CENTER

            row.addView(rankValue)
            row.addView(sinceDate)

            ranksTable.addView(row)
        }
    }

    fun refresh(v: View) {
        fillRanksTable()
    }

    fun goBack(v: View){
        Log.v(TAG, getString(R.string.log_end))
        this.finish()
    }
}