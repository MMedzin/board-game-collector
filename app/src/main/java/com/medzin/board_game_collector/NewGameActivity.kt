package com.medzin.board_game_collector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.databinding.ActivityNewGameBinding
import com.medzin.board_game_collector.util.GameType
import com.medzin.board_game_collector.util.PersonParser
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewGameBinding

    lateinit var title: EditText
    lateinit var orgTitle: EditText
    lateinit var yearPublished: EditText
    lateinit var designers: EditText
    lateinit var artists: EditText
    lateinit var description: EditText
    lateinit var orderDate: EditText
    lateinit var price: EditText
    lateinit var sdc: EditText
    lateinit var eanUpc: EditText
    lateinit var productCode: EditText
    lateinit var rank: EditText
    lateinit var typeDropdown: Spinner
    lateinit var type: String
    lateinit var comment: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setLabels()

        typeDropdown = findViewById<Spinner>(R.id.typeDropdown)
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

        title = findViewById(R.id.title)
        orgTitle = findViewById(R.id.originalTitle)
        yearPublished = findViewById(R.id.yearPublished)
        designers = findViewById(R.id.designers)
        artists = findViewById(R.id.artists)
        description = findViewById(R.id.description)
        orderDate = findViewById(R.id.orderDate)
        price = findViewById(R.id.price)
        sdc = findViewById(R.id.sdc)
        eanUpc = findViewById(R.id.eanUpc)
        productCode = findViewById(R.id.productCode)
        rank = findViewById(R.id.rank)
        comment = findViewById(R.id.rank)

        binding.cancelBtn.text = getString(R.string.cancel_btn)
        binding.confirmBtn.text = getString(R.string.add_new_btn)
        binding.titleNewGame.text = getString(R.string.new_game_manual_title)

    }

    fun setLabels(){
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

    fun setLabel(viewId: Int, labelId: Int){
        val lbl = findViewById<TextView>(viewId)
        lbl.text = getString(labelId)
    }

    fun addNewGame(v: View){
        val dbHandler = GameDBHandler(this, null, null, 1)

        val yearPublishedInt = Integer.parseInt(yearPublished.text.toString())
        val eanUpcInt = Integer.parseInt(eanUpc.text.toString())
        val orderDateLocalDate = LocalDate.parse(orderDate.text.toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        val rankInt = Integer.parseInt(rank.text.toString())
        val designersList = PersonParser.parsePersonList(designers.text.toString())
        val artistsList = PersonParser.parsePersonList(artists.text.toString())
        val gameType = GameType.translateTypeName(type, resources.getStringArray(R.array.type_options))


        dbHandler.addGame(Game(0, title.text.toString(), orgTitle.text.toString(),
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
    }

    fun cancel(v: View){
        setResult(1)
        this.finish()
    }

}