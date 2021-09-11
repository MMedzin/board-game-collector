package com.medzin.board_game_collector

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.databinding.ActivityMainBinding
import com.medzin.board_game_collector.util.SortScheme


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var sortScheme = SortScheme.NAME_ASC

    val TEXT_PREVIEW_LINES_LIMIT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val title: TextView = findViewById(R.id.mainTitle)
        title.text = getString(R.string.collection_main_title)

        binding.bggButton.text = getString(R.string.bgg_btn)
        binding.newGameButton.text = getString(R.string.new_game_btn)
        binding.locationsBtn.text = getString(R.string.loc_btn)

        binding.refreshBtn.height = binding.refreshBtn.width

        val sortSchemeSpinner = binding.sortSchemeDropdown
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
                fillCollectionTable()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(sortScheme.ind)
            }
        }
        sortSchemeSpinner.setSelection(sortScheme.ind)
    }

    fun goToBGG(v: View){
        val i = Intent(this, BGGActivity::class.java)
        startActivity(i)
    }

    fun goToNewGame(v: View){
        val i = Intent(this, AddFromBGG::class.java)
        startActivityForResult(i, 1)
    }

    fun goToLocations(v: View){
        val i = Intent(this, LocationActivity::class.java)
        startActivity(i);
    }

    fun refresh(v: View){
        fillCollectionTable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            fillCollectionTable()
        }
    }

    fun fillCollectionTable(){
        val gamesTable: TableLayout = binding.collectionTable
        gamesTable.setColumnShrinkable(2, true)

        val dbHandler = GameDBHandler(this, null, null, 1)

        gamesTable.removeAllViews()

        when (sortScheme){
            SortScheme.NAME_ASC -> dbHandler.getGamesCollection().sortedBy{ it.title }
            SortScheme.NAME_DESC -> dbHandler.getGamesCollection().sortedByDescending { it.title }
            SortScheme.RANK_ASC -> dbHandler.getGamesCollection().sortedBy { it.currRank }
            SortScheme.RANK_DESC -> dbHandler.getGamesCollection().sortedByDescending { it.currRank }
            SortScheme.ADD_DATE_ASC -> dbHandler.getGamesCollection().sortedBy { it.dateOfCollecting }
            SortScheme.ADD_DATE_DESC -> dbHandler.getGamesCollection().sortedByDescending { it.dateOfCollecting }
        }.forEach { game ->
            val row = TableRow(this)
            val tableRowParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            row.layoutParams = tableRowParams
            row.background = ContextCompat.getDrawable(this, R.drawable.border)
            row.setOnClickListener {
                val i = Intent(this, DetailsActivity::class.java)
                i.putExtra("id", game.id)
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

}