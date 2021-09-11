package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.medzin.board_game_collector.database.objects.GameRank
import com.medzin.board_game_collector.util.DBQuery
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RankDBHelper {

    companion object {
        val TABLE_RANKS = "ranks"
        val COLUMN_ID = "id"
        val COLUMN_GAME_ID = "game_id"
        val COLUMN_DATE = "date"
        val COLUMN_RANK = "rank"

        fun onCreate(db: SQLiteDatabase){
            val CREATE_RANKS_TABLE = ("CREATE TABLE " + TABLE_RANKS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_GAME_ID + " INTEGER," +
                    COLUMN_DATE + " TEXT," + COLUMN_RANK + " INTEGER," +
                    "FOREIGN KEY(" + COLUMN_GAME_ID + ") REFERENCES " +
                    GameDBHandler.TABLE_GAMES + "(" + GameDBHandler.COLUMN_ID + "))")
            db.execSQL(CREATE_RANKS_TABLE)
        }

        fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            TODO("Not yet implemented")
        }

        fun addRank(gameId: Int, date: LocalDate, rank: Int, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_ID, gameId)
            values.put(COLUMN_DATE, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            values.put(COLUMN_RANK, rank)
            db.insert(TABLE_RANKS, null, values)
        }

        fun updateRank(id: Int, gameId: Int, date: LocalDate, rank: Int, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_ID, gameId)
            values.put(COLUMN_DATE, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            values.put(COLUMN_RANK, rank)
            db.update(TABLE_RANKS, values, "${COLUMN_ID}=?", arrayOf(id.toString()))
        }

        fun getAllGameRanks(gameId: Int, db: SQLiteDatabase):Array<GameRank> {
            val query = "SELECT * FROM $TABLE_RANKS WHERE $COLUMN_GAME_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(gameId.toString()))
            var ranksArr = arrayOf<GameRank>()

            while(cursor.moveToNext()){
                val rank = DBQuery.queryNullableInt(cursor, COLUMN_RANK)
                val date = DBQuery.queryDate(cursor, COLUMN_DATE)
                ranksArr = ranksArr.plus(GameRank(rank, date))
            }
            cursor.close()
            return ranksArr
        }

        fun deleteArtists(gameId: Int, db: SQLiteDatabase) {
            db.delete(TABLE_RANKS, "$COLUMN_GAME_ID = ?", arrayOf(gameId.toString()))
        }

    }

}