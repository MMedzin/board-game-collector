package com.medzin.board_game_collector.database.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.medzin.board_game_collector.R
import com.medzin.board_game_collector.util.GameType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.*

class Game {

    var title: String? = null
    lateinit var originalTitle: String
    var yearPublished: Int = 0
    var designers: MutableList<Person>? = null
    var artists: MutableList<Person>? = null
    var description: String? = null
    var dateOfOrder: LocalDate? = null
    var dateOfCollecting: LocalDate? = null
    var pricePaid: String? = null
    var suggestedDetailPrice: String? = null
    var eanOrUpcCode: Int = 0
    var bggId: Int = 0
    var productCode: String? = null
    var currRank: Int = 0
    var type: GameType? = null
    var comment: String? = null
    var thumbnail: Bitmap? = null
    var location: Location? = null
    var imagePath: String? = null

    constructor(title: String?, originalTitle: String, yearPublished: Int,
                designers: MutableList<Person>?, artists: MutableList<Person>?, description: String?,
                dateOfOrder: LocalDate?, dateOfCollecting: LocalDate?, pricePaid: String?,
                suggestedDetailPrice: String?, eanOrUpcCode: Int, bggId: Int,
                productCode: String?, currRank: Int, type: GameType?, comment: String?,
                thumbnail: Bitmap?, location: Location?){
        this.title = title
        this.originalTitle = originalTitle
        this.yearPublished = yearPublished
        this.designers = designers
        this.artists = artists
        this.description = description
        this.dateOfOrder = dateOfOrder
        this.dateOfCollecting = dateOfCollecting
        this.pricePaid = pricePaid
        this.suggestedDetailPrice = suggestedDetailPrice
        this.eanOrUpcCode = eanOrUpcCode
        this.bggId = bggId
        this.productCode = productCode
        this.currRank = currRank
        this.type = type
        this.comment = comment
        this.thumbnail = thumbnail
        this.location = location
    }

    constructor(title: String){
        this.title = title
    }

    constructor()

    fun addImage(context: Context, image: Bitmap){
        val workDir = File("${context.filesDir}/IMG")
        if (!workDir.exists()) workDir.mkdir()
        val output = originalTitle.toLowerCase(Locale.ROOT).replace(' ', '_')
        imagePath = "$workDir/$output.png"
        try {
            FileOutputStream(imagePath).use { out ->
                image.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            Toast.makeText(context,
                context.getString(R.string.save_img_failed_msg),
                Toast.LENGTH_SHORT).show()
        }
    }

    fun getImage(context: Context): Bitmap?{
        var image: Bitmap? = null
        if(imagePath != null){
            if (File(imagePath).exists()){
                try {
                    FileInputStream(imagePath).use { fileIn ->
                        image = BitmapFactory.decodeStream(fileIn)
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.load_img_failed_msg),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
        return image
    }

}