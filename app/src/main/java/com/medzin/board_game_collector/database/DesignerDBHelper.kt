package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.medzin.board_game_collector.database.objects.Person
import com.medzin.board_game_collector.util.PersonParser

class DesignerDBHelper {

    companion object {
        private const val TABLE_DESIGNERS = "designers"
        private const val COLUMN_ID = "id"
        private const val COLUMN_GAME_TITLE = "game_title"
        private const val COLUMN_DESIGNERS_LIST = "designers_list"

        fun onCreate(db: SQLiteDatabase) {
            val CREATE_DESIGNERS_TABLE = ("CREATE TABLE " + TABLE_DESIGNERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_GAME_TITLE + " TEXT," +
                    COLUMN_DESIGNERS_LIST+ " TEXT," + "FOREIGN KEY(" + COLUMN_GAME_TITLE +
                    ") REFERENCES " + GameDBHandler.TABLE_GAMES + "(" +
                    GameDBHandler.COLUMN_ORG_TITLE + "))")
            db.execSQL(CREATE_DESIGNERS_TABLE)
        }

        fun addDesigner(gameTitle: String, designers: MutableList<Person>?, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_TITLE, gameTitle)
            values.put(COLUMN_DESIGNERS_LIST, PersonParser.stringifyPersonList(designers))
            db.insertOrThrow(TABLE_DESIGNERS, null, values)
        }

        fun updateDesigner(gameTitle: String, designers: MutableList<Person>?, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_TITLE, gameTitle)
            values.put(COLUMN_DESIGNERS_LIST, PersonParser.stringifyPersonList(designers))
            db.update(TABLE_DESIGNERS, values, "$COLUMN_GAME_TITLE=?", arrayOf(gameTitle))
        }

        fun findDesigners(gameTitle: String, db: SQLiteDatabase): MutableList<Person> {
            val query = "SELECT * FROM $TABLE_DESIGNERS WHERE $COLUMN_GAME_TITLE = ?"
            val cursor = db.rawQuery(query, arrayOf(gameTitle))

            if(cursor.moveToFirst()){
                val resultList = PersonParser.parsePersonList(cursor.getString(cursor.getColumnIndex(
                    COLUMN_DESIGNERS_LIST
                )))
                cursor.close()
                return resultList
            }
            cursor.close()
            return mutableListOf()
        }

    }

}