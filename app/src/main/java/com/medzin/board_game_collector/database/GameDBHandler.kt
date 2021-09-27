package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.database.objects.GameRank
import com.medzin.board_game_collector.database.objects.Location
import com.medzin.board_game_collector.util.DBQuery
import com.medzin.board_game_collector.util.GameType
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GameDBHandler(var context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?,
                    version: Int) : SQLiteOpenHelper(context, DATABASE_NAME,
                                                factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "gameDB.db"
        const val TABLE_GAMES = "games"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ORG_TITLE = "original_title"
        const val COLUMN_YEAR_PUBLISHED = "year_published"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_ORDER_DATE = "order_date"
        const val COLUMN_COLLECT_DATE = "collect_date"
        const val COLUMN_PRICE = "price"
        const val COLUMN_SUGGESTED_DETAIL_PRICE = "suggested_detail_price"
        const val COLUMN_EAN_OR_UPC = "ean_or_upc"
        const val COLUMN_BGG_ID = "bgg_id"
        const val COLUMN_PRODUCT_CODE = "product_code"
        const val COLUMN_CURR_RANK = "curr_rank"
        const val COLUMN_TYPE = "type"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_LOCAL_ID = "localisation_id"
        const val COLUMN_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + "(" +
                COLUMN_TITLE + " TEXT," +
                COLUMN_ORG_TITLE + " TEXT PRIMARY KEY," + COLUMN_YEAR_PUBLISHED + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT," + COLUMN_ORDER_DATE + " TEXT," +
                COLUMN_COLLECT_DATE + " TEXT," + COLUMN_PRICE + " TEXT," +
                COLUMN_SUGGESTED_DETAIL_PRICE + " TEXT," + COLUMN_EAN_OR_UPC + " INTEGER," +
                COLUMN_BGG_ID + " INTEGER," + COLUMN_PRODUCT_CODE + " TEXT," +
                COLUMN_CURR_RANK + " INTEGER," + COLUMN_TYPE + " TEXT," +
                COLUMN_COMMENT + " TEXT," + COLUMN_THUMBNAIL + " BLOB," +
                COLUMN_LOCAL_ID + " INTEGER," + COLUMN_IMAGE_PATH + " TEXT)")
        db.execSQL(CREATE_GAMES_TABLE)
        DesignerDBHelper.onCreate(db)
        ArtistDBHelper.onCreate(db)
        LocationDBHelper.onCreate(db)
        RankDBHelper.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun addGame(game: Game){
        val values = ContentValues()
        val db = this.writableDatabase
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_ORG_TITLE, game.originalTitle)
        values.put(COLUMN_YEAR_PUBLISHED, game.yearPublished)
        values.put(COLUMN_DESCRIPTION, game.description)
        if (game.dateOfOrder != null) {
            values.put(
                COLUMN_ORDER_DATE,
                game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        if (game.dateOfCollecting != null){
            values.put(
                COLUMN_COLLECT_DATE,
                game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        values.put(COLUMN_PRICE, game.pricePaid)
        values.put(COLUMN_SUGGESTED_DETAIL_PRICE, game.suggestedDetailPrice)
        values.put(COLUMN_EAN_OR_UPC, game.eanOrUpcCode)
        values.put(COLUMN_BGG_ID, game.bggId)
        values.put(COLUMN_PRODUCT_CODE, game.productCode)
        values.put(COLUMN_CURR_RANK, game.currRank)
        values.put(COLUMN_TYPE, game.type?.name)
        if (game.location != null){
            values.put(
                    COLUMN_LOCAL_ID,
                    LocationDBHelper.getIdOfLocation(game.location?.name, db)
            )
        }
        if(game.comment != null){
            values.put(COLUMN_COMMENT, game.comment)
        }
        if(game.thumbnail != null){
            val stream = ByteArrayOutputStream()
            game.thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            values.put(COLUMN_THUMBNAIL, stream.toByteArray())
        }
        if(game.imagePath != null){
            values.put(COLUMN_IMAGE_PATH, game.imagePath)
        }

        db.insertOrThrow(TABLE_GAMES, null, values)

        ArtistDBHelper.addArtist(game.originalTitle, game.artists, db)
        DesignerDBHelper.addDesigner(game.originalTitle, game.designers, db)
        RankDBHelper.addRank(GameRank(game.currRank, LocalDate.now()), game.originalTitle, db)

        db.close()
    }

    fun updateGame(game: Game){
        val values = ContentValues()
        val db = this.writableDatabase
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_ORG_TITLE, game.originalTitle)
        values.put(COLUMN_YEAR_PUBLISHED, game.yearPublished)
        values.put(COLUMN_DESCRIPTION, game.description)
        if (game.dateOfOrder != null) {
            values.put(
                COLUMN_ORDER_DATE,
                game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        if (game.dateOfCollecting != null){
            values.put(
                COLUMN_COLLECT_DATE,
                game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        values.put(COLUMN_PRICE, game.pricePaid)
        values.put(COLUMN_SUGGESTED_DETAIL_PRICE, game.suggestedDetailPrice)
        values.put(COLUMN_EAN_OR_UPC, game.eanOrUpcCode)
        values.put(COLUMN_BGG_ID, game.bggId)
        values.put(COLUMN_PRODUCT_CODE, game.productCode)
        values.put(COLUMN_CURR_RANK, game.currRank)
        values.put(COLUMN_TYPE, game.type?.name)
        if (game.location != null){
            values.put(
                COLUMN_LOCAL_ID,
                LocationDBHelper.getIdOfLocation(game.location?.name, db)
            )
        }
        else {
            values.putNull(COLUMN_LOCAL_ID)
        }
        if(game.comment != null){
            values.put(COLUMN_COMMENT, game.comment)
        }
        if(game.thumbnail != null){
            val stream = ByteArrayOutputStream()
            game.thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            values.put(COLUMN_THUMBNAIL, stream.toByteArray())
        }
        if(game.imagePath != null){
            values.put(COLUMN_IMAGE_PATH, game.imagePath)
        }

        db.update(TABLE_GAMES, values, "$COLUMN_ORG_TITLE=?", arrayOf(game.originalTitle))

        ArtistDBHelper.updateArtist(game.originalTitle, game.artists, db)
        DesignerDBHelper.updateDesigner(game.originalTitle, game.designers, db)

        db.close()
    }

    private fun findGameOrgTitle(gameOrgTitle: String, db: SQLiteDatabase):Game? {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ORG_TITLE=?"
        val cursor = db.rawQuery(query, arrayOf(gameOrgTitle))
        var game: Game? = null

        if(cursor.moveToFirst()){
            game = buildGameFromData(cursor, db)
            cursor.close()
        }
        return game
    }

    fun find(gameOrgTitle: String):Game? {
        val db = this.writableDatabase
        val game: Game? = findGameOrgTitle(gameOrgTitle, db)
        db.close()
        return game
    }

    private fun isLocationUsed(locationId: Int, db: SQLiteDatabase): Boolean{
        val query = "SELECT COUNT(*) FROM $TABLE_GAMES WHERE $COLUMN_LOCAL_ID=?"
        val cursor = db.rawQuery(query, arrayOf(locationId.toString()))
        if(cursor.moveToFirst()){
            return cursor.getInt(0) > 0
        }
        cursor.close()
        return false
    }

    fun getGamesInLocation(locationId: Int) = sequence {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_LOCAL_ID=?"
        val db = this@GameDBHandler.writableDatabase
        val cursor = db.rawQuery(query, arrayOf(locationId.toString()))

        while (cursor.moveToNext()) {
            yield(buildGameFromData(cursor, db))
        }
        cursor.close()
        db.close()
    }

    fun deleteGame(gameOrgTitle: String): Boolean {
        var result = false
        val db = this.writableDatabase
        if (db.delete(TABLE_GAMES, "$COLUMN_ORG_TITLE = ?", arrayOf(gameOrgTitle)) > 0){
            result = true
        }
        db.close()
        return result
    }

    fun getGamesCollection() = sequence {
        val query = "SELECT * FROM $TABLE_GAMES"
        val db = this@GameDBHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            yield(buildGameFromData(cursor, db))
        }
        cursor.close()
        db.close()
    }

    private fun buildGameFromData(cursor: Cursor, db: SQLiteDatabase): Game {
        val title = DBQuery.queryNullable(cursor, COLUMN_TITLE)
        val originalTitle = cursor.getString(cursor.getColumnIndex(COLUMN_ORG_TITLE))
        val yearPublished = Integer.parseInt(cursor.getString(cursor.getColumnIndex(
            COLUMN_YEAR_PUBLISHED)))
        val designers = DesignerDBHelper.findDesigners(originalTitle, db)
        val artists = ArtistDBHelper.findArtists(originalTitle, db)
        val description = DBQuery.queryNullable(cursor, COLUMN_DESCRIPTION)
        val dateOfOrder = DBQuery.queryDate(cursor, COLUMN_ORDER_DATE)
        val dateOfCollecting = DBQuery.queryDate(cursor, COLUMN_COLLECT_DATE)
        val pricePaid = DBQuery.queryNullable(cursor, COLUMN_PRICE)
        val suggestedDetailPrice = DBQuery.queryNullable(cursor, COLUMN_SUGGESTED_DETAIL_PRICE)
        val eanOrUpcCode = DBQuery.queryNullableInt(cursor, COLUMN_EAN_OR_UPC)
        val bggId = DBQuery.queryNullableInt(cursor, COLUMN_BGG_ID)
        val productCode = DBQuery.queryNullable(cursor, COLUMN_PRODUCT_CODE)
        val currRank = DBQuery.queryNullableInt(cursor, COLUMN_CURR_RANK)
        val type = GameType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)))
        val comment = DBQuery.queryNullable(cursor, COLUMN_COMMENT)
        val thumbnail = DBQuery.queryBitmap(cursor, COLUMN_THUMBNAIL)
        val imagePath = DBQuery.queryNullable(cursor, COLUMN_IMAGE_PATH)

        val localisation = if (cursor.isNull(cursor.getColumnIndex(COLUMN_LOCAL_ID))) null else LocationDBHelper.findLocation(
                DBQuery.queryNullableInt(cursor, COLUMN_LOCAL_ID), db)
        val game =  Game(title, originalTitle, yearPublished, designers, artists, description,
            dateOfOrder, dateOfCollecting, pricePaid, suggestedDetailPrice, eanOrUpcCode,
            bggId, productCode, currRank, type, comment, thumbnail, localisation)
        game.imagePath = imagePath
        return game
    }

    fun addLocation(location: Location){
        val db = this.writableDatabase
        LocationDBHelper.addLocation(location, db)
        db.close()
    }

    fun getLocations(): Array<Location>{
        val db = this.writableDatabase
        var result: Array<Location> = emptyArray()
        LocationDBHelper.getLocations(db).forEach {
            result = result.plus(it) }
        db.close()
        return result
    }

    fun deleteLocation(name: String): Boolean{
        val db = this.writableDatabase
        val locationId = LocationDBHelper.getIdOfLocation(name, db)
        val result = if (!isLocationUsed(locationId, db))
            LocationDBHelper.deleteLocation(locationId, db) else false
        db.close()
        return result
    }

    fun updateLocation(location: Location){
        val db = this.writableDatabase
        LocationDBHelper.updateLocation(location, db)
        db.close()
    }

    fun addRank(gameOrgTitle: String, rank: GameRank){
        val db = this.writableDatabase
        RankDBHelper.addRank(rank, gameOrgTitle, db)
        db.close()
    }

    fun getRanks(gameOrgTitle: String): Array<GameRank>{
        val db = this.writableDatabase
        var result: Array<GameRank> = emptyArray()
        RankDBHelper.getAllGameRanks(gameOrgTitle, db).forEach {
            result = result.plus(it)
        }
        db.close()
        return result
    }

}