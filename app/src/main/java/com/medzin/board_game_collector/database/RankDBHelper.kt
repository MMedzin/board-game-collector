package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.medzin.board_game_collector.database.objects.GameRank
import com.medzin.board_game_collector.util.DBQuery
import java.time.format.DateTimeFormatter

class RankDBHelper {

    companion object {
        private const val TABLE_RANKS = "ranks"
        private const val COLUMN_GAME_TITLE = "game_id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_RANK = "rank"

        fun onCreate(db: SQLiteDatabase){
            val CREATE_RANKS_TABLE = ("CREATE TABLE " + TABLE_RANKS + "(" +
                    COLUMN_GAME_TITLE + " INTEGER NOT NULL," +
                    COLUMN_DATE + " TEXT NOT NULL," + COLUMN_RANK + " INTEGER," +
                    "FOREIGN KEY(" + COLUMN_GAME_TITLE + ") REFERENCES " +
                    GameDBHandler.TABLE_GAMES + "(" + GameDBHandler.COLUMN_ORG_TITLE + ")," +
                    "PRIMARY KEY(" + COLUMN_GAME_TITLE + ", " + COLUMN_DATE + "))")
            db.execSQL(CREATE_RANKS_TABLE)
        }

        fun addRank(rank: GameRank, gameTitle: String, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_GAME_TITLE, gameTitle)
            values.put(COLUMN_DATE, rank.sinceDate?.format(DateTimeFormatter.ISO_LOCAL_DATE))
            values.put(COLUMN_RANK, rank.rank)
            db.insertOrThrow(TABLE_RANKS, null, values)
        }

        fun getAllGameRanks(gameTitle: String, db: SQLiteDatabase)=sequence {
            val query = "SELECT * FROM $TABLE_RANKS WHERE $COLUMN_GAME_TITLE = ?"
            val cursor = db.rawQuery(query, arrayOf(gameTitle))

            while(cursor.moveToNext()){
                val rank = DBQuery.queryNullableInt(cursor, COLUMN_RANK)
                val date = DBQuery.queryDate(cursor, COLUMN_DATE)
                yield(GameRank(rank, date))
            }
            cursor.close()
        }

    }

}