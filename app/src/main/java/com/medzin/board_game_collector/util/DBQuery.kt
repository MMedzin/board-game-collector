package com.medzin.board_game_collector.util

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DBQuery {

    companion object{
        fun queryNullable(cursor: Cursor, column: String): String? {
            return if (cursor.isNull(cursor.getColumnIndex(column)))
                null else cursor.getString(cursor.getColumnIndex(column))
        }

        fun queryDate(cursor: Cursor, column: String): LocalDate? {
            val dateStr = queryNullable(cursor, column)
            return if (dateStr == null) null else
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        }

        fun queryNullableInt(cursor: Cursor, column: String): Int {
            return if (cursor.isNull(cursor.getColumnIndex(column)))
                0 else Integer.parseInt(cursor.getString(cursor.getColumnIndex(column)))
        }

        fun queryBitmap(cursor: Cursor, column: String): Bitmap? {
            if (cursor.isNull(cursor.getColumnIndex(column))) return null
            val byteArr = cursor.getBlob(cursor.getColumnIndex(column))
            return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
        }
    }

}