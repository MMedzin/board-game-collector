package com.medzin.board_game_collector

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.databinding.ActivityNewGameBinding
import com.medzin.board_game_collector.util.GameType
import com.medzin.board_game_collector.util.PersonParser
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class NewGameActivity : AppCompatActivity() {
    private val TAG = "NewGameActivity"

    private lateinit var binding: ActivityNewGameBinding

    lateinit var title: EditText
    private lateinit var orgTitle: EditText
    private lateinit var yearPublished: EditText
    private lateinit var designers: EditText
    private lateinit var artists: EditText
    private lateinit var description: EditText
    private lateinit var orderDate: EditText
    private lateinit var price: EditText
    private lateinit var sdc: EditText
    private lateinit var eanUpc: EditText
    private lateinit var productCode: EditText
    lateinit var rank: EditText
    private lateinit var typeDropdown: Spinner
    lateinit var type: String
    lateinit var comment: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, getString(R.string.log_init))
        super.onCreate(savedInstanceState)
        binding = ActivityNewGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setLabels()

        typeDropdown = findViewById(R.id.typeDropdown)
        ArrayAdapter.createFromResource(
                this,
                R.array.type_options,
                android.R.layout.simple_spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeDropdown.adapter = adapter
        }
        typeDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                type = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                type = ""
            }
        }
        typeDropdown.setSelection(0)

        title = binding.title
        orgTitle = binding.originalTitle
        yearPublished = binding.yearPublished
        designers = binding.designers
        artists = binding.artists
        description = binding.description
        orderDate = binding.orderDate
        price = binding.price
        sdc = binding.sdc
        eanUpc = binding.eanUpc
        productCode = binding.productCode
        rank = binding.rank
        comment = binding.comment

        binding.confirmBtn.text = getString(R.string.add_new_btn)
        binding.titleNewGame.text = getString(R.string.new_game_manual_title)

        orderDate.hint = getString(R.string.date_format)
        Log.v(TAG, getString(R.string.log_init_done))
    }

    private fun setLabels(){
        setLabel(R.id.gameTitleLbl, R.string.game_title_lbl)

        setLabel(R.id.originalTitleLbl, R.string.game_org_title_lbl)

        setLabel(R.id.yearPublishedLbl, R.string.year_published_lbl)

        setLabel(R.id.designersLbl, R.string.designers_lbl)

        setLabel(R.id.artistsLbl, R.string.artists_lbl)

        setLabel(R.id.descriptionLbl, R.string.description_lbl)

        setLabel(R.id.orderDateLbl, R.string.order_date_lbl)

        setLabel(R.id.priceLbl, R.string.price_lbl)

        setLabel(R.id.sdcLbl, R.string.sdc_lbl)

        setLabel(R.id.eanUpcLbl, R.string.ean_upc_lbl)

        setLabel(R.id.productCodeLbl, R.string.product_code_lbl)

        setLabel(R.id.rankLbl, R.string.rank_lbl)

        setLabel(R.id.typeLbl, R.string.type_lbl)

        setLabel(R.id.commentLbl, R.string.comment_lbl)
    }

    private fun setLabel(viewId: Int, labelId: Int){
        val lbl = findViewById<TextView>(viewId)
        lbl.text = getString(labelId)
    }

    fun addNewGame(v: View){
        if (necessaryFilled() && datesFormatCorrect()){
            val dbHandler = GameDBHandler(this, null, null, 1)

            try {
                val yearPublishedInt = Integer.parseInt(yearPublished.text.toString())
                val eanUpcInt =  try { Integer.parseInt(eanUpc.text.toString()) } catch (e: NumberFormatException) {0}
                val orderDateLocalDate = LocalDate.parse(orderDate.text.toString(), DateTimeFormatter.ISO_LOCAL_DATE)
                val rankInt = try { Integer.parseInt(rank.text.toString()) } catch (e: NumberFormatException) {0}
                val designersList = PersonParser.parsePersonList(designers.text.toString())
                val artistsList = PersonParser.parsePersonList(artists.text.toString())
                val gameType = GameType.translateTypeName(type, resources.getStringArray(R.array.type_options))


                dbHandler.addGame(Game(title.text.toString(), orgTitle.text.toString(),
                        yearPublishedInt, designersList, artistsList, description.text.toString(),
                        orderDateLocalDate, LocalDate.now(), price.text.toString(), sdc.text.toString(),
                        eanUpcInt, 0, productCode.text.toString(), rankInt, gameType,
                        comment.text.toString(), null, null
                ))

                typeDropdown.setSelection(0)


                title.setText("")
                orgTitle.setText("")
                yearPublished.setText("")
                designers.setText("")
                artists.setText("")
                description.setText("")
                orderDate.setText("")
                price.setText("")
                sdc.setText("")
                eanUpc.setText("")
                productCode.setText("")
                rank.setText("")
                comment.setText("")
                Toast.makeText(this, getString(R.string.add_success_msg), Toast.LENGTH_SHORT).show()
            } catch (e: SQLiteConstraintException) {
                Toast.makeText(this, getString(R.string.game_exists_msg), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun cancel(v: View){
        Log.v(TAG, getString(R.string.log_end))
        setResult(1)
        this.finish()
    }

    private fun necessaryFilled(): Boolean{
        val result: Boolean
        when {
            title.text.isEmpty() -> {
                Toast.makeText(this,
                        getString(R.string.fill_field_msg, getString(R.string.game_title_lbl)),
                        Toast.LENGTH_SHORT).show()
                result =  false
            }
            orgTitle.text.isEmpty() -> {
                Toast.makeText(this,
                        getString(R.string.fill_field_msg, getString(R.string.game_org_title_lbl)),
                        Toast.LENGTH_SHORT).show()
                result =  false
            }
            yearPublished.text.isEmpty() -> {
                Toast.makeText(this,
                        getString(R.string.fill_field_msg, getString(R.string.year_published_lbl)),
                        Toast.LENGTH_SHORT).show()
                result =  false
            }
            designers.text.isEmpty() -> {
                Toast.makeText(this,
                        getString(R.string.fill_field_msg, getString(R.string.designers_lbl)),
                        Toast.LENGTH_SHORT).show()
                result =  false
            }
            artists.text.isEmpty() -> {
                Toast.makeText(this,
                        getString(R.string.fill_field_msg, getString(R.string.artists_lbl)),
                        Toast.LENGTH_SHORT).show()
                result =  false
            }
            else -> result =  true
        }
        return result
    }

    private fun datesFormatCorrect(): Boolean{
        if (orderDate.text.isEmpty()){
            Toast.makeText(this, getString(R.string.fill_field_msg,
                    getString(R.string.order_date_lbl)),
                    Toast.LENGTH_SHORT).show()
            return false
        }
        return try {
            LocalDate.parse(orderDate.text.toString(), DateTimeFormatter.ISO_LOCAL_DATE)
            true
        } catch (e: DateTimeParseException){
            Toast.makeText(this,
                    getString(R.string.date_format_msg, getString(R.string.date_format)),
                    Toast.LENGTH_LONG).show()
            false
        }
    }

}