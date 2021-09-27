package com.medzin.board_game_collector

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import java.time.format.DateTimeParseException

class DetailsActivity : AppCompatActivity() {
    private val TAG = "DetailsActivity"

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var orgTitle: String
    private var locationNameMap: Map<String, Location?> = mapOf()
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
        Log.v(TAG, getString(R.string.log_init))

        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val extras = intent.extras ?: return
        orgTitle = extras.getString("orgTitle").toString()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.deleteGameBtnDetails.text = getString(R.string.delete_game_btn)
        
        fillContents()

        Log.v(TAG, getString(R.string.log_init_done))
    }
    
    private fun fillContents(){
        val dbHandler = GameDBHandler(this, null, null, 1)
        val game = dbHandler.find(orgTitle)

        val img = binding.thumbnail
        val gameImage = game?.getImage(this)
        if (gameImage != null) img.setImageBitmap(gameImage)
        else if (game?.thumbnail != null) img.setImageBitmap(game.thumbnail)

        val mainTitle = binding.mainTitle
        mainTitle.text = game?.originalTitle

        binding.userGameTitleLblDetails.text =
            formatLabel(getString(R.string.game_title_lbl))
        val originalTitle = binding.userGameTitleDetails
        originalTitle.setText(game?.title)
        originalTitle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game?.title){
                    val changedGame = Game(s.toString(), game!!.originalTitle, game.yearPublished,
                        game.designers, game.artists, game.description, game.dateOfOrder,
                        game.dateOfCollecting, game.pricePaid, game.suggestedDetailPrice,
                        game.eanOrUpcCode, game.bggId, game.productCode, game.currRank, game.type,
                        game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })


        binding.yearPublishedLblDetails.text =
            formatLabel(getString(R.string.year_published_lbl))
        val yearPublished = binding.yearPublishedDetails
        yearPublished.setText(game?.yearPublished.toString())
        yearPublished.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game?.yearPublished.toString()){
                    val changedGame = game?.originalTitle?.let {
                        Game(game.title, it,
                                Integer.parseInt(s.toString()), game.designers, game.artists,
                                game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                                game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                                game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    }
                    if (changedGame != null) {
                        changedGame.imagePath = game.imagePath
                        dbHandler.updateGame(changedGame)
                    }
                }
            }

        })

        binding.typeLblDetails.text =
            formatLabel(getString(R.string.type_lbl))
        val typeDropdown = binding.typeDropdownDetails
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
                val changedGame = game?.originalTitle?.let {
                    Game(game.title, it,
                            game.yearPublished, game.designers, game.artists,
                            game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                            game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                            game.currRank,
                            GameType.translateTypeName(parent.getItemAtPosition(pos).toString(),
                                resources.getStringArray(R.array.type_options)),
                            game.comment, game.thumbnail, game.location)
                }
                if (changedGame != null) {
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(0)
            }
        }
        typeDropdown.setSelection(game?.type?.ind!!)

        binding.rankLblDetails.text =
            formatLabel(getString(R.string.rank_lbl))
        val rank = binding.rankDetails
        if (game.currRank == 0){
            rank.text = getString(R.string.empty)
            rank.isEnabled = false
            rank.isClickable = false
        }
        else{
            rank.text = game.currRank.toString()
        }

        binding.descriptionLblDetails.text =
            formatLabel(getString(R.string.description_lbl))
        val description = binding.descriptionDetails
        description.setText(Html.fromHtml(game.description))
        description.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.description){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        s.toString(), game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.designersLblDetails.text =
            formatLabel(getString(R.string.designers_lbl))
        val designers = binding.designersDetails
        designers.setText(PersonParser.stringifyPersonList(game.designers))
        designers.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != PersonParser.stringifyPersonList(game.designers)){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, PersonParser.parsePersonList(s.toString()), game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.artistsLblDetails.text =
            formatLabel(getString(R.string.artists_lbl))
        val artists = binding.artistsDetails
        artists.setText(PersonParser.stringifyPersonList(game.artists))
        artists.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != PersonParser.stringifyPersonList(game.artists)){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, PersonParser.parsePersonList(s.toString()),
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.orderDateLblDetails.text =
            formatLabel(getString(R.string.order_date_lbl))
        val orderDate = binding.orderDateDetails
        orderDate.setText(game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        orderDate.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    && datesFormatCorrect(s.toString(), getString(R.string.order_date_lbl))){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists, game.description,
                        LocalDate.parse(s.toString(), DateTimeFormatter.ISO_LOCAL_DATE),
                        game.dateOfCollecting, game.pricePaid, game.suggestedDetailPrice,
                        game.eanOrUpcCode, game.bggId, game.productCode, game.currRank,
                        game.type, game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.collectDateLblDetails.text =
            formatLabel(getString(R.string.collect_date_lbl))
        val collectDate = binding.collectDateDetails
        collectDate.setText(game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        collectDate.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    && datesFormatCorrect(s.toString(), getString(R.string.collect_date_lbl))){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists, game.description,
                        game.dateOfOrder,
                        LocalDate.parse(s.toString(), DateTimeFormatter.ISO_LOCAL_DATE),
                        game.pricePaid, game.suggestedDetailPrice,
                        game.eanOrUpcCode, game.bggId, game.productCode, game.currRank,
                        game.type, game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.priceLblDetails.text =
            formatLabel(getString(R.string.price_lbl))
        val price = binding.priceDetails
        price.setText(game.pricePaid)
        price.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.pricePaid.toString()){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, s.toString(),
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.scdLblDetails.text =
            formatLabel(getString(R.string.sdc_lbl))
        val scd = binding.scdDetails
        scd.setText(game.suggestedDetailPrice)
        scd.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.suggestedDetailPrice.toString()){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        s.toString(), game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.eanUpcLblDetails.text =
            formatLabel(getString(R.string.ean_upc_lbl))
        val eanUpc = binding.eanUpcDetails
        eanUpc.setText(game.eanOrUpcCode.toString())
        eanUpc.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.eanOrUpcCode.toString() && s != null && s.length > 0){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, Integer.parseInt(s.toString()), game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.productCodeLblDetails.text =
            formatLabel(getString(R.string.product_code_lbl))
        val productCode = binding.productCodeDetails
        productCode.setText(game.productCode)
        productCode.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.eanOrUpcCode.toString()){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, s.toString(),
                        game.currRank, game.type, game.comment, game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

        binding.locationLblDetails.text =
            formatLabel(getString(R.string.location_lbl))
        val locationDropdown = binding.locationDropdownDetails
        val locations = dbHandler.getLocations()
        var pos = 0
        locations.forEach {
            locationNameMap = locationNameMap.plus(Pair(it.name!!, it))
            locationIdPosMap = locationIdPosMap.plus(Pair(it.id, pos++))
        }
        locationNameMap = locationNameMap.plus(Pair(getString(R.string.empty), null))
        val adapter = ArrayAdapter<String>(
            this,
                android.R.layout.simple_spinner_item,
                locations.map{it.name}.plus(getString(R.string.empty))
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationDropdown.adapter = adapter
        locationDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (parent.getItemAtPosition(pos).toString() != game.location?.name){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, game.comment, game.thumbnail,
                        locationNameMap[parent.getItemAtPosition(pos).toString()]
                    )
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                parent.setSelection(pos)
            }
        }
        if(game.location != null){
            locationIdPosMap[game.location?.id!!]?.let { locationDropdown.setSelection(it) }
        }
        else {
            locationDropdown.setSelection(pos)
        }

        binding.bggCodeLblDetails.text =
            formatLabel(getString(R.string.bgg_code_lbl))
        binding.bggCodeDetails.text = game.bggId.toString()

        binding.commentLblDetails.text =
            formatLabel(getString(R.string.comment_lbl))
        val comment = binding.commentDetails
        comment.setText(game.comment)
        comment.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != game.suggestedDetailPrice.toString()){
                    val changedGame = Game(game.title, game.originalTitle,
                        game.yearPublished, game.designers, game.artists,
                        game.description, game.dateOfOrder, game.dateOfCollecting, game.pricePaid,
                        game.suggestedDetailPrice, game.eanOrUpcCode, game.bggId, game.productCode,
                        game.currRank, game.type, s.toString(), game.thumbnail, game.location)
                    changedGame.imagePath = game.imagePath
                    dbHandler.updateGame(changedGame)
                }
            }

        })

    }

    fun deleteGame(v: View){
        val dbHandler = GameDBHandler(this, null, null, 1)
        val game = dbHandler.find(orgTitle)

        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_assertion))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (game != null) {
                    if (game.originalTitle.let { dbHandler.deleteGame(it) }) {
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

    private fun datesFormatCorrect(dateString: String, label: String): Boolean{
        if (dateString.isEmpty()){
            Toast.makeText(this, getString(R.string.fill_field_msg,
                label),
                Toast.LENGTH_SHORT).show()
            return false
        }
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            true
        } catch (e: DateTimeParseException){
            Toast.makeText(this,
                getString(R.string.date_format_msg, getString(R.string.date_format)),
                Toast.LENGTH_LONG).show()
            false
        }
    }

    fun goBack(v: View){
        Log.v(TAG, getString(R.string.log_end))
        setResult(1)
        this.finish()
    }

    fun showRanks(v: View){
        val i = Intent(this, GameRanksActivity::class.java)
        i.putExtra("orgTitle", orgTitle)
        startActivity(i)
    }

}