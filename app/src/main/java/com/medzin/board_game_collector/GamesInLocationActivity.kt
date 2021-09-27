package com.medzin.board_game_collector

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.databinding.ActivityGamesInLocationBinding
import com.medzin.board_game_collector.util.SortScheme

class GamesInLocationActivity : AppCompatActivity() {
    private val TAG = "GamesInLocationActivity"
    private lateinit var binding: ActivityGamesInLocationBinding
    private var locationId: Int = 0
    var sortScheme = SortScheme.NAME_ASC
    private val TEXT_PREVIEW_LINES_LIMIT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, getString(R.string.log_init))
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val extras = intent.extras ?: return
        locationId = extras.getInt("locationId")

        binding = ActivityGamesInLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sortSchemeSpinner = binding.sortSchemeDropdownInLocation
        ArrayAdapter.createFromResource(
            this,
            R.array.sort_schemes,
            android.R.layout.simple_spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSchemeSpinner.adapter = adapter
        }
        sortSchemeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                sortScheme = SortScheme.translateTypeName(
                    parent.getItemAtPosition(pos).toString(),
                    resources.getStringArray(R.array.sort_schemes))
                fillGamesTable()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(sortScheme.ind)
            }
        }
        sortSchemeSpinner.setSelection(sortScheme.ind)


        fillGamesTable()
        Log.v(TAG, getString(R.string.log_init_done))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            fillGamesTable()
        }
    }

    fun fillGamesTable(){
        val gamesTable: TableLayout = binding.gamesTable
        gamesTable.setColumnShrinkable(2, true)

        val dbHandler = GameDBHandler(this, null, null, 1)

        gamesTable.removeAllViews()

        when (sortScheme){
            SortScheme.NAME_ASC -> dbHandler.getGamesInLocation(locationId).sortedBy{ it.title }
            SortScheme.NAME_DESC -> dbHandler.getGamesInLocation(locationId).sortedByDescending { it.title }
            SortScheme.RANK_ASC -> dbHandler.getGamesInLocation(locationId).sortedBy { it.currRank }
            SortScheme.RANK_DESC -> dbHandler.getGamesInLocation(locationId).sortedByDescending { it.currRank }
            SortScheme.ADD_DATE_ASC -> dbHandler.getGamesInLocation(locationId).sortedBy { it.dateOfCollecting }
            SortScheme.ADD_DATE_DESC -> dbHandler.getGamesInLocation(locationId).sortedByDescending { it.dateOfCollecting }
        }.forEach { game ->
            val row = TableRow(this)
            val tableRowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            row.layoutParams = tableRowParams
            row.background = ContextCompat.getDrawable(this, R.drawable.border)
            row.setOnClickListener {
                val i = Intent(this, DetailsActivity::class.java)
                i.putExtra("orgTitle", game.originalTitle)
                startActivityForResult(i, 1)
            }

            val rank = TextView(this)
            rank.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT)
            rank.text = game.currRank.toString()
            rank.background = ContextCompat.getDrawable(this, R.drawable.border)
            row.addView(rank)

            val image = ImageView(this)
            image.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT)
            if (game.thumbnail != null) image.setImageBitmap(game.thumbnail)
            else image.setImageResource(R.mipmap.ic_launcher)
            image.background = ContextCompat.getDrawable(this, R.drawable.border)
            row.addView(image)

            val str = buildSpannedString {
                bold{
                    color(Color.BLUE){
                        append(game.title)
                    }
                }
                append("(${game.yearPublished})\n")
                if (!game.description.isNullOrEmpty()){
                    append(Html.fromHtml(game.description))
                }
            }
            val description = TextView(this)
            description.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT)
            description.maxLines = TEXT_PREVIEW_LINES_LIMIT
            description.text = str
            description.background = ContextCompat.getDrawable(this, R.drawable.border)
            row.addView(description)

            gamesTable.addView(row)
        }

    }

    fun goBack(v: View){
        Log.v(TAG, getString(R.string.log_end))
        this.finish()
    }

}