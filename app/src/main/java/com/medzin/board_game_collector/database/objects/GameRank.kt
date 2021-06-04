package com.medzin.board_game_collector.database.objects

import java.time.LocalDate

class GameRank {

    var rank: Int = 0
    var sinceDate: LocalDate? = null

    constructor(rank: Int, sinceDate: LocalDate?){
        this.rank = rank
        this.sinceDate = sinceDate
    }

}