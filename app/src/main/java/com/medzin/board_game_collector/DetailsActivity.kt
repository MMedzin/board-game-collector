package com.medzin.board_game_collector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.medzin.board_game_collector.database.GameDBHandler
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.database.objects.Location
import com.medzin.board_game_collector.databinding.ActivityDetailsBinding
import com.medzin.board_game_collector.util.GameType
import com.medzin.board_game_collector.util.PersonParser
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var id: Int = 0
    private var locationNameMap: Map<String, Location> = mapOf()
    private var locationIdPosMap: Map<Int, Int> = mapOf()

    companion object {
        private fun formatLabel(label: String?): String{
            val format =  buildSpannedString {
                bold {
                    append(label)
                    append(":")
                }
            }
            return format.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val extras = intent.extras ?: return
        id = extras.getInt("id")

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.deleteGameBtnDetails.text = getString(R.string.delete_game_btn)
        
        fillContents()

    }
    
    fun fillContents(){
        val dbHandler = GameDBHandler(this, null, null, 1)
        val game = dbHandler.findGame(id)

        val img = findViewById<ImageView>(R.id.thumbnail)
        if (game?.thumbnail != null) img.setImageBitmap(game.thumbnail)

        val mainTitle = findViewById<TextView>(R.id.mainTitle)
        mainTitle.text = game?.title

        findViewById<TextView>(R.id.originalGameTitleLblDetails).text =
            formatLabel(getString(R.string.game_org_title_lbl))
        val originalTitle = findViewById<EditText>(R.id.originalGameTitleDetails)
        originalTitle.setText(game?.originalTitle)
        originalTitle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game?.originalTitle){
                    val changedGame = Game(game?.id!!, game.title, s.toString(), game.yearPublished,
                        game.designers, game.artists, game.description, game.dateOfOrder,
                        game.dateOfCollecting, game.pricePaid, game.suggestedDetailPrice,
                        game.eanOrUpcCode, game.bggId, game.productCode, game.currRank, game.type,
                        game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })


        findViewById<TextView>(R.id.yearPublishedLblDetails).text =
            formatLabel(getString(R.string.year_published_lbl))
        val yearPublished = findViewById<EditText>(R.id.yearPublishedDetails)
        yearPublished.setText(game?.yearPublished.toString())
        yearPublished.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game?.yearPublished.toString()){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        Integer.parseInt(s.toString()), game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.typeLblDetails).text =
            formatLabel(getString(R.string.type_lbl))
        val typeDropdown = findViewById<Spinner>(R.id.typeDropdownDetails)
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
                val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                    game.yearPublished, game.designers, game.artists,
                    game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                    game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                    game.currRank,
                    GameType.translateTypeName(parent.getItemAtPosition(pos).toString(),
                        resources.getStringArray(R.array.type_options)),
                    game.comment, game.thumbnail, game.location)
                dbHandler.updateGame(changedGame)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(0)
            }
        }
        typeDropdown.setSelection(game?.type?.ind!!)

        findViewById<TextView>(R.id.rankLblDetails).text =
            formatLabel(getString(R.string.rank_lbl))
        val rank = findViewById<EditText>(R.id.rankDetails)
        rank.setText(game.currRank.toString())
        rank.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.currRank.toString()){
                    val changedGame = Game(game.id, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        Integer.parseInt(s.toString()), game.type, game.comment, game.thumbnail,
                        game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.descriptionLblDetails).text =
            formatLabel(getString(R.string.description_lbl))
        val description = findViewById<EditText>(R.id.descriptionDetails)
        description.setText(Html.fromHtml(game.description))
        description.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.description){
                    val changedGame = Game(game.id, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        s.toString(), game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.designersLblDetails).text =
            formatLabel(getString(R.string.designers_lbl))
        val designers = findViewById<EditText>(R.id.designersDetails)
        designers.setText(PersonParser.stringifyPersonList(game.designers))
        designers.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != PersonParser.stringifyPersonList(game.designers)){
                    val changedGame = Game(game.id, game.title, game.originalTitle,
                        game.yearPublished, PersonParser.parsePersonList(s.toString()), game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.artistsLblDetails).text =
            formatLabel(getString(R.string.artists_lbl))
        val artists = findViewById<EditText>(R.id.artistsDetails)
        artists.setText(PersonParser.stringifyPersonList(game.artists))
        artists.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != PersonParser.stringifyPersonList(game.artists)){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, PersonParser.parsePersonList(s.toString()),
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.orderDateLblDetails).text =
            formatLabel(getString(R.string.order_date_lbl))
        val orderDate = findViewById<EditText>(R.id.orderDateDetails)
        orderDate.setText(game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        orderDate.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE)){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists, game.description,
                        LocalDate.parse(s.toString(), DateTimeFormatter.ISO_LOCAL_DATE),
                        game.dateOfCollecting, game.pricePaid, game.suggestedDetailPrice,
                        game.eanOrUpcCode, game.bggId, game.productCode, game.currRank,
                        game.type, game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.collectDateLblDetails).text =
            formatLabel(getString(R.string.collect_date_lbl))
        val collectDate = findViewById<EditText>(R.id.collectDateDetails)
        collectDate.setText(game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        collectDate.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE)){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists, game.description,
                        game.dateOfOrder,
                        LocalDate.parse(s.toString(), DateTimeFormatter.ISO_LOCAL_DATE),
                        game.pricePaid, game.suggestedDetailPrice,
                        game.eanOrUpcCode, game.bggId, game.productCode, game.currRank,
                        game.type, game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.priceLblDetails).text =
            formatLabel(getString(R.string.price_lbl))
        val price = findViewById<EditText>(R.id.priceDetails)
        price.setText(game.pricePaid)
        price.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.pricePaid.toString()){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, s.toString(),
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.scdLblDetails).text =
            formatLabel(getString(R.string.sdc_lbl))
        val scd = findViewById<EditText>(R.id.scdDetails)
        scd.setText(game.suggestedDetailPrice)
        scd.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.suggestedDetailPrice.toString()){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        s.toString(), game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.eanUpcLblDetails).text =
            formatLabel(getString(R.string.ean_upc_lbl))
        val eanUpc = findViewById<EditText>(R.id.eanUpcDetails)
        eanUpc.setText(game.eanOrUpcCode.toString())
        eanUpc.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.eanOrUpcCode.toString()){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, Integer.parseInt(s.toString()), game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        findViewById<TextView>(R.id.locationLblDetails).text =
            formatLabel(getString(R.string.location_lbl))
        val locationDropdown = findViewById<Spinner>(R.id.locationDropdownDetails)
        val locations = dbHandler.getLocations()
        var pos = 0
        locations.forEach {
            locationNameMap = locationNameMap.plus(Pair(it.name!!, it))
            locationIdPosMap = locationIdPosMap.plus(Pair(it.id, pos++))
        }
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, locations.map{it.name}
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationDropdown.adapter = adapter
        locationDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (parent.getItemAtPosition(pos).toString() != game.location?.name){
                    val changedGame = Game(game.id, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        locationNameMap[parent.getItemAtPosition(pos).toString()]
                    )
                    dbHandler.updateGame(changedGame)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(0)
            }
        }
        if(game.location != null){
            locationIdPosMap[game.location?.id!!]?.let { locationDropdown.setSelection(it) }
        }

        findViewById<TextView>(R.id.bggCodeLblDetails).text =
            formatLabel(getString(R.string.bgg_code_lbl))
        findViewById<TextView>(R.id.bggCodeDetails).text = game.bggId.toString()

        findViewById<TextView>(R.id.commentLblDetails).text =
            formatLabel(getString(R.string.comment_lbl))
        val comment = findViewById<EditText>(R.id.commentDetails)
        comment.setText(game.comment)
        comment.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.suggestedDetailPrice.toString()){
                    val changedGame = Game(game?.id!!, game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, s.toString(), game.thumbnail, game.location)
                    dbHandler.updateGame(changedGame)
                }
            }

        })

    }

    fun deleteGame(v: View){
        val dbHandler = GameDBHandler(this, null, null, 1)
        val game = dbHandler.findGame(id)

        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_assertion))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (game != null) {
                    if (dbHandler.deleteGame(game.id)) {
                        Toast.makeText(this, getString(R.string.delete_success_msg), Toast.LENGTH_SHORT).show()
                        goBack(v)
                    }
                }
                else {
                    Toast.makeText(this, getString(R.string.delete_failed_msg), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun goBack(v: View){
        setResult(1)
        this.finish()
    }

}