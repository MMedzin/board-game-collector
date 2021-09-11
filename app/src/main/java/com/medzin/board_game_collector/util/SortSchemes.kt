package com.medzin.board_game_collector.util

enum class SortScheme(val ind: Int) {
    NAME_ASC(0), NAME_DESC(1), RANK_ASC(2), RANK_DESC(3), ADD_DATE_ASC(4), ADD_DATE_DESC(5);

    companion object {
        fun translateTypeName(name: String, translationsArr: Array<String>): SortScheme{
            values().forEach {
                if (name == translationsArr[it.ind]) return it
            }
            return NAME_ASC
        }
    }

}