package com.medzin.board_game_collector.util


enum class GameType(val ind: Int) {
    GAME(0), EXPANSION(1), MIXED(2);

    companion object {
        fun translateTypeName(name: String, translationsArr: Array<String>): GameType{
            values().forEach {
                if (name == translationsArr[it.ind]) return it
            }
            return MIXED
        }
    }
}