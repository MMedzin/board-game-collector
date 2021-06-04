package com.medzin.board_game_collector.util

import com.medzin.board_game_collector.database.objects.Person

class PersonParser {
    companion object{
        fun parsePersonList(listStr: String): MutableList<Person> {
            val listStrSplit = listStr.split(";")
            val resultList = mutableListOf<Person>()
            listStrSplit.forEach {
                resultList.add(Person(it))
            }
            return resultList
        }
    }
}