package com.medzin.board_game_collector.database.objects

import java.time.LocalDate

class GameRank(var rank: Int, var sinceDate: LocalDate?) : Comparable<GameRank> {

    override fun compareTo(other: GameRank): Int {
        if (this.sinceDate == other.sinceDate){
            return 0
        }else if (this.sinceDate!! < other.sinceDate){
            return -1
        }
        return 1
    }

}