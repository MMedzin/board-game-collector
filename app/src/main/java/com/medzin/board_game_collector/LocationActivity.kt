package com.medzin.board_game_collector

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Location
import com.medzin.board_game_collector.databinding.ActivityLocationsBinding

class LocationActivity : AppCompatActivity() {
    private val TAG = "LocationActivity"

    private lateinit var binding: ActivityLocationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, getString(R.string.log_init))
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        binding = ActivityLocationsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fillLocationTable()

        binding.addNewBtn.text = getString(R.string.new_lostaion_btn)
        binding.addNewBtn.setOnClickListener {
            val dbHandler = GameDBHandler(this, null, null, 1)
            dbHandler.addLocation(Location(
                    binding.newLocation.text.toString(),
                    binding.newLocationComment.text.toString()
            ))
            binding.newLocation.setText("")
            binding.newLocationComment.setText("")
            fillLocationTable()
        }
        binding.backBtnLocations.text = getString(R.string.back_btn_location)
        binding.backBtnLocations.setOnClickListener {
            Log.v(TAG, getString(R.string.log_end))
            setResult(1)
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
        Log.v(TAG, getString(R.string.log_init_done))
    }

    private fun fillLocationTable() {
        val locationsTable: TableLayout = binding.locationTable

        val dbHandler = GameDBHandler(this, null, null, 1)

        locationsTable.removeAllViews()
        dbHandler.getLocations().forEach { location ->
            val row = TableRow(this)
            val tableRowParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
            row.layoutParams = tableRowParams
            row.background = ContextCompat.getDrawable(this, R.drawable.border)
//            row.gravity = Gravity.CENTER

            val name = EditText(this)
            name.setText(location.name.toString())
            name.gravity = Gravity.CENTER
            name.background = ContextCompat.getDrawable(this, R.drawable.border)


            val comment = EditText(this)
            comment.setText(location.comment.toString())
            comment.gravity = Gravity.CENTER

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

            val deleteBtn = Button(this)
            deleteBtn.setOnClickListener {
                if (dbHandler.deleteLocation(name.text.toString())) {
                    Toast.makeText(this, getString(R.string.location_delete_success_msg),
                            Toast.LENGTH_SHORT).show()
                    setResult(1)
                    this.finish()
                }
                else {
                    Toast.makeText(this, getString(R.string.location_delete_failed_msg),
                            Toast.LENGTH_SHORT).show()
                }
            }
            deleteBtn.text = getString(R.string.delete_location_btn)

            row.addView(deleteBtn)

            val gameListBtn = Button(this)
            gameListBtn.setOnClickListener {
                val i = Intent(this, GamesInLocationActivity::class.java)
                i.putExtra("locationId", location.id)
                startActivity(i)
            }
            gameListBtn.text = getString(R.string.games_in_location_btn)

            row.addView(gameListBtn)


            locationsTable.addView(row)
        }
    }

}