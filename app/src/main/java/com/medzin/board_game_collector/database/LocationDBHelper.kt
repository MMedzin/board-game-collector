package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.medzin.board_game_collector.database.objects.Location
import com.medzin.board_game_collector.util.DBQuery

class LocationDBHelper {

    companion object {
        private const val TABLE_LOCATION = "location"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_COMMENT = "comment"

        fun onCreate(db: SQLiteDatabase) {
            val CREATE_ARTISTS_TABLE = ("CREATE TABLE " + TABLE_LOCATION + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT," +
                    COLUMN_COMMENT + " TEXT)")
            db.execSQL(CREATE_ARTISTS_TABLE)
        }

        fun addLocation(location: Location, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_NAME, location.name)
            if (location.comment != null) {
                values.put(COLUMN_COMMENT, location.comment)
            }
            db.insert(TABLE_LOCATION, null, values)
        }

        fun updateLocation(location: Location, db: SQLiteDatabase){
            val values = ContentValues()
            values.put(COLUMN_NAME, location.name)
            if (location.comment != null) {
                values.put(COLUMN_COMMENT, location.comment)
            }
            db.update(TABLE_LOCATION, values, "$COLUMN_ID=?",
                arrayOf(location.id.toString()))
        }

        fun findLocation(id: Int, db: SQLiteDatabase): Location? {
            val query = "SELECT * FROM $TABLE_LOCATION WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(id.toString()))

            if(cursor.moveToFirst()){
                val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
                val name = DBQuery.queryNullable(cursor, COLUMN_NAME)
                val comment = DBQuery.queryNullable(cursor, COLUMN_COMMENT)
                val resultLocation = Location(id, name, comment)
                cursor.close()
                return resultLocation
            }
            cursor.close()
            return null
        }

        fun getLocations(db: SQLiteDatabase)= sequence {
            val query = "SELECT * FROM $TABLE_LOCATION"
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
                val name = DBQuery.queryNullable(cursor, COLUMN_NAME)
                val comment = DBQuery.queryNullable(cursor, COLUMN_COMMENT)
                val resultLocation = Location(id, name, comment)
                yield(resultLocation)
            }
            cursor.close()
        }

        fun getIdOfLocation(name: String?, db: SQLiteDatabase): Int {
            if(name == null) return 0
            val query = "SELECT * FROM $TABLE_LOCATION WHERE $COLUMN_NAME = ?"
            val cursor = db.rawQuery(query, arrayOf(name))

            if(cursor.moveToFirst()){
                val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
                cursor.close()
                return id
            }
            cursor.close()
            return 0
        }

        fun deleteLocation(id: Int, db: SQLiteDatabase): Boolean {
            return db.delete(TABLE_LOCATION, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
        }
    }
}