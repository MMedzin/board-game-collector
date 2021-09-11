package com.medzin.board_game_collector.database.objects

import android.graphics.Bitmap
import com.medzin.board_game_collector.util.GameType
import java.time.LocalDate

class Game {

    var id: Int = 0
    var title: String? = null
    var originalTitle: String? = null
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

    constructor(id: Int, title: String?, originalTitle: String?, yearPublished: Int,
                designers: MutableList<Person>?, artists: MutableList<Person>?, description: String?,
                dateOfOrder: LocalDate?, dateOfCollecting: LocalDate?, pricePaid: String?,
                suggestedDetailPrice: String?, eanOrUpcCode: Int, bggId: Int,
                productCode: String?, currRank: Int, type: GameType?, comment: String?,
                thumbnail: Bitmap?, location: Location?){
        this.id = id
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

}