package com.medzin.board_game_collector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.database.objects.Location
import com.medzin.board_game_collector.databinding.ActivityLocationsBinding

class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fillLocationTable()

        binding.addNewBtn.text = getString(R.string.new_lostaion_btn)
        binding.addNewBtn.setOnClickListener { _ ->
            val dbHandler = GameDBHandler(this, null, null, 1)
            dbHandler.addLocation(Location(
                    binding.newLocation.text.toString(),
                    binding.newLocationComment.text.toString()
            ))
            binding.newLocation.setText("")
            binding.newLocationComment.setText("")
        }
        binding.backBtnLocations.text = getString(R.string.back_btn_location)
        binding.backBtnLocations.setOnClickListener { _ ->
            this.finish()
        }
        binding.newLocation.hint = getString(R.string.new_location_prompt)
        binding.newLocation.setOnFocusChangeListener { _, _ ->
            binding.newLocation.hint = ""
        }
        binding.newLocationComment.hint = getString(R.string.new_location_comment_prompt)
        binding.newLocation.setOnFocusChangeListener { _, _ ->
            binding.newLocationComment.hint = ""
        }


    }

    fun fillLocationTable() {
        val locationsTable: TableLayout = findViewById(R.id.locationTable)

        val dbHandler = GameDBHandler(this, null, null, 1)

        locationsTable.removeAllViews()
        dbHandler.getLocations().forEach { location ->
            val row = TableRow(this)
            val tableRowParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            row.layoutParams = tableRowParams
            row.background = ContextCompat.getDrawable(this, R.drawable.border)

            val name = EditText(this)
            name.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT)
            name.setText(location.name.toString())
            println(location.name)
            name.background = ContextCompat.getDrawable(this, R.drawable.border)


            val comment = EditText(this)
            comment.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT)
            comment.setText(location.comment.toString())
            println(location.comment )
            comment.background = ContextCompat.getDrawable(this, R.drawable.border)

            name.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val changedLocation = Location(location.id, s.toString(), comment.text.toString())
                    dbHandler.updateLocation(changedLocation)
                }

            })
            row.addView(name)

            comment.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val changedLocation = Location(location.id, name.text.toString(), s.toString())
                    dbHandler.updateLocation(changedLocation)
                }

            })
            row.addView(comment)

            locationsTable.addView(row)
        }
    }

}