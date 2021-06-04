package com.medzin.board_game_collector.database

import android.database.sqlite.SQLiteDatabase
import com.medzin.board_game_collector.database.objects.Person
import com.medzin.board_game_collector.util.PersonParser

class DesignerDBHelper {

    companion object {
        val TABLE_DESIGNERS = "designers"
        val COLUMN_ID = "id"
        val COLUMN_GAME_ID = "game_id"
        val COLUMN_DESIGNERS_LIST = "designers_list"

        fun onCreate(db: SQLiteDatabase) {
            val CREATE_DESIGNERS_TABLE = ("CREATE TABLE " + TABLE_DESIGNERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_GAME_ID + " INTEGER," +
                    COLUMN_DESIGNERS_LIST+ " TEXT," + "FOREIGN KEY(" + COLUMN_GAME_ID +
                    ") REFERENCES " + GameDBHandler.TABLE_GAMES + "(" + GameDBHandler.COLUMN_ID + "))")
            db.execSQL(CREATE_DESIGNERS_TABLE)
        }

        fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            TODO("Not yet implemented")
        }

        fun findDesigners(gameId: Int, db: SQLiteDatabase): MutableList<Person> {
            val query = "SELECT * FROM $TABLE_DESIGNERS WHERE $COLUMN_GAME_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(gameId.toString()))

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

        fun deleteDesigners(gameId: Int, db: SQLiteDatabase) {
            db.delete(TABLE_DESIGNERS, "$COLUMN_GAME_ID = ?", arrayOf(gameId.toString()))
        }
    }

}