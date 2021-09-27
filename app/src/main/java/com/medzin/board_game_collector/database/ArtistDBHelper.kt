package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.medzin.board_game_collector.database.objects.Person
import com.medzin.board_game_collector.util.PersonParser

class ArtistDBHelper {

    companion object {
        private const val TABLE_ARTISTS = "artists"
        private const val COLUMN_ID = "id"
        private const val COLUMN_GAME_ID = "game_id"
        private const val COLUMN_ARTISTS_LIST = "artists_list"

        fun onCreate(db: SQLiteDatabase) {
            val CREATE_ARTISTS_TABLE = ("CREATE TABLE " + TABLE_ARTISTS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_GAME_ID + " INTEGER," +
                    COLUMN_ARTISTS_LIST + " TEXT," + "FOREIGN KEY(" + COLUMN_GAME_ID +
                    ") REFERENCES " + GameDBHandler.TABLE_GAMES + "(" +
                    GameDBHandler.COLUMN_ORG_TITLE + "))")
            db.execSQL(CREATE_ARTISTS_TABLE)
        }

    fun addArtist(gameTitle: String, artists: MutableList<Person>?, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_ID, gameTitle)
            values.put(COLUMN_ARTISTS_LIST, PersonParser.stringifyPersonList(artists))
            db.insertOrThrow(TABLE_ARTISTS, null, values)
        }

        fun updateArtist(gameTitle: String, artists: MutableList<Person>?, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_ID, gameTitle)
            values.put(COLUMN_ARTISTS_LIST, PersonParser.stringifyPersonList(artists))
            db.update(TABLE_ARTISTS, values, "$COLUMN_GAME_ID=?", arrayOf(gameTitle))
        }

        fun findArtists(gameTitle: String, db: SQLiteDatabase): MutableList<Person> {
            val query = "SELECT * FROM $TABLE_ARTISTS WHERE $COLUMN_GAME_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(gameTitle))

            if(cursor.moveToFirst()){
                val resultList = PersonParser.parsePersonList(cursor.getString(cursor.getColumnIndex(
                    COLUMN_ARTISTS_LIST
                )))
                cursor.close()
                return resultList
            }
            cursor.close()
            return mutableListOf()
        }

    }
}