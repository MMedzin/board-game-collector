package com.medzin.board_game_collector.util

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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

//        fun queryBitmap(cursor: Cursor, db: SQLiteDatabase, tableName: String, id: Int,
//                        idColumn: String, blobColumn: String): Bitmap? {
////            if (cursor.isNull(cursor.getColumnIndex(blobColumn))) return null
//            val lengthQuery = "SELECT length($blobColumn) FROM $tableName WHERE $idColumn = ?"
//            val bitmapCursor = db.rawQuery(lengthQuery, arrayOf(id.toString()))
//            if(bitmapCursor.moveToNext()){
//                val length = bitmapCursor.getInt(0)
//                var byteArr = byteArrayOf()
//                var bufferSize = 1000000
//                for (i in 1..(length-bufferSize) step bufferSize){
//                    val end = i + bufferSize - 1
//                    val bufferedQuery = "SELECT substr($blobColumn, $i, $end) FROM $tableName WHERE id = ?"
//                    val bufferCursor = db.rawQuery(bufferedQuery, arrayOf(id.toString()))
//                    if(bufferCursor.moveToNext()){
//                           byteArr = byteArr.plus(bufferCursor.getBlob(0))
//                    }
//                }
//                val bufferedQuery = "SELECT substr($blobColumn, ${(length - bufferSize + 1)}, $length) FROM $tableName WHERE id = ?"
//                val bufferCursor = db.rawQuery(bufferedQuery, arrayOf(id.toString()))
//                if(bufferCursor.moveToNext()){
//                    byteArr = byteArr.plus(bufferCursor.getBlob(0))
//                }
//                return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
//            }
//            return null
//        }
    }

}